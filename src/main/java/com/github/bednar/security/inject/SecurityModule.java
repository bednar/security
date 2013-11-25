package com.github.bednar.security.inject;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.util.WebUtils;
import org.grouplens.grapht.Context;
import org.grouplens.grapht.Module;

/**
 * @author Jakub Bednář (14/09/2013 10:26 AM)
 */
public class SecurityModule implements Module
{
    private final ServletContext servletContext;

    public SecurityModule(final @Nonnull ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    @Override
    public void configure(final @Nonnull Context context)
    {
        //Security Manager
        DefaultSecurityManager securityManager = (DefaultSecurityManager) WebUtils
                .getRequiredWebEnvironment(servletContext)
                .getSecurityManager();
        context
                .bind(SecurityManager.class)
                .to(securityManager);

        //Password Service
        PasswordService passwordService = new DefaultPasswordService();
        context
                .bind(PasswordService.class)
                .to(passwordService);

        //Credentials Matcher
        PasswordMatcher matcher = new PasswordMatcher();
        matcher.setPasswordService(passwordService);
        context
                .bind(CredentialsMatcher.class)
                .to(matcher);

        //Authorization Realm
        WebAuthenticatingRealm realm = new WebAuthenticatingRealm(servletContext, securityManager);
        context
                .bind(WebAuthenticatingRealm.class)
                .to(realm);
    }
}
