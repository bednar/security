package com.github.bednar.security.inject;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.github.bednar.base.http.AppBootstrap;
import com.github.bednar.base.inject.Injector;
import com.github.bednar.persistence.contract.Resource;
import com.github.bednar.persistence.inject.service.Database;
import com.github.bednar.security.contract.Authenticable;
import com.google.common.collect.Lists;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;
import org.hibernate.criterion.Restrictions;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (14/09/2013 10:29 AM)
 */
public class WebAuthenticatingRealm extends AuthenticatingRealm
{
    private final ServletContext servletContext;

    private DelegateAuthenticatingRealm delegate;

    public WebAuthenticatingRealm(final @Nonnull ServletContext servletContext, final @Nonnull DefaultSecurityManager securityManager)
    {
        Collection<Realm> realms = securityManager.getRealms();
        if (realms == null)
        {
            realms = Lists.newArrayList();
        }

        realms.add(this);
        securityManager.setRealms(realms);

        this.servletContext = servletContext;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken token) throws AuthenticationException
    {
        DelegateAuthenticatingRealm delegate = getDelegate();

        return delegate.doGetAuthenticationInfo(token);
    }

    @Override
    public CredentialsMatcher getCredentialsMatcher()
    {
        DelegateAuthenticatingRealm delegate = getDelegate();

        return delegate.getCredentialsMatcher();
    }

    @Nonnull
    private synchronized DelegateAuthenticatingRealm getDelegate()
    {
        if (delegate == null)
        {
            delegate = AppBootstrap
                    .getInjector(servletContext)
                    .getInstance(DelegateAuthenticatingRealm.class);
        }

        return delegate;
    }

    @Nonnull
    public Class getAuthenticableType()
    {
        return getDelegate().type;
    }

    public static class DelegateAuthenticatingRealm extends org.apache.shiro.realm.AuthenticatingRealm
    {
        private static final Logger LOG = LoggerFactory.getLogger(DelegateAuthenticatingRealm.class);

        @Inject
        private Database database;

        @Inject
        private CredentialsMatcher credentialsMatcher;

        private Class<Resource> type;

        @Override
        protected AuthenticationInfo doGetAuthenticationInfo(final @Nonnull AuthenticationToken token) throws AuthenticationException
        {
            try (Database.Transaction transaction = database.transaction())
            {
                Object account = token.getPrincipal();

                //noinspection unchecked
                List<Resource> accounts = transaction
                        .session()
                        .createCriteria(type)
                        .add(Restrictions.eq("account", account))
                        .list();

                if (accounts.isEmpty())
                {
                    LOG.error("[empty-account][{}][{}]", account, accounts.size());

                    return null;
                }
                else if (accounts.size() > 1)
                {
                    LOG.error("[multiple-accounts][{}][{}]", account, accounts.size());

                    return null;
                }

                Authenticable authenticable = (Authenticable) accounts.get(0);

                return new SimpleAuthenticationInfo(account, authenticable.getPassword(), getName());
            }
            catch (Exception e)
            {
                throw new WebAuthenticatingRealmException(e);
            }
        }

        @PostConstruct
        public void initialize(final @Nonnull Injector injector)
        {
            Reflections reflections = new Reflections(AppBootstrap.SYMBOL_BASE_PACKAGE);

            Set<Class<? extends Authenticable>> subTypesOf = reflections.getSubTypesOf(Authenticable.class);
            if (subTypesOf.size() != 1)
            {
                throw new WebAuthenticatingRealmException("Cannot find " + Authenticable.class + " resource!");
            }

            try
            {
                //noinspection unchecked
                type = (Class<Resource>) Class.forName(subTypesOf.iterator().next().getName());
            }
            catch (ClassNotFoundException e)
            {
                throw new WebAuthenticatingRealmException(e);
            }

            LOG.info("[authenticable-resource][{}]", type.getName());
        }

        @Override
        public CredentialsMatcher getCredentialsMatcher()
        {
            return credentialsMatcher;
        }
    }

    private static class WebAuthenticatingRealmException extends RuntimeException
    {
        private WebAuthenticatingRealmException(final String message)
        {
            super(message);
        }

        private WebAuthenticatingRealmException(final Throwable cause)
        {
            super(cause);
        }
    }
}
