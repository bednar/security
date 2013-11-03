package com.github.bednar.security.aspect;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.bednar.base.http.AppBootstrap;
import com.github.bednar.base.http.AppContext;
import com.github.bednar.base.utils.reflection.FluentReflection;
import com.github.bednar.persistence.contract.Resource;
import com.github.bednar.persistence.inject.service.Database;
import com.github.bednar.security.contract.ResourceAuthorize;
import com.google.common.collect.Maps;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (30/10/2013 16:23)
 */
@Aspect
public class PersistenceAspect
{
    private static final Logger LOG = LoggerFactory.getLogger(PersistenceAspect.class);

    private Map<Class<? extends Resource>, ResourceAuthorize> authorizers = Maps.newHashMap();

    public PersistenceAspect()
    {
        LOG.info("[init-resource-authorizers]");

        for (Class<? extends ResourceAuthorize> authorizerType : findAuthorizerTypes())
        {
            ResourceAuthorize authorizer = createAuthorizer(authorizerType);

            authorizers.put(authorizer.getType(), authorizer);
        }

        LOG.info("[{}][done]", authorizers.size());
    }

    @Pointcut("execution(public * com.github.bednar.persistence.inject.service.Database.Transaction+.save(..))")
    public void save()
    {
    }

    @Around("save()")
    public Object save(ProceedingJoinPoint point) throws Throwable
    {
        Resource resource   = (Resource) point.getArgs()[0];

        if (authorizers.containsKey(resource.getClass()))
        {
            Criterion criterion = authorizers.get(resource.getClass()).update(getPrincipal());

            check(resource.getClass(), resource.getId(), criterion, "cannot-save");
        }

        return point.proceed();
    }

    @Pointcut("execution(public * com.github.bednar.persistence.inject.service.Database.Transaction+.read(..))")
    public void read()
    {
    }

    @Around("read()")
    public Object read(ProceedingJoinPoint point) throws Throwable
    {
        Long key            = (Long) point.getArgs()[0];
        Class type          = (Class) point.getArgs()[1];

        if (authorizers.containsKey(type))
        {
            Criterion criterion = authorizers.get(type).read(getPrincipal());

            check(type, key, criterion, "cannot-read");
        }

        return point.proceed();
    }

    @Pointcut("execution(public * com.github.bednar.persistence.inject.service.Database.Transaction+.delete(..))")
    public void delete()
    {
    }

    @Around("delete()")
    public Object delete(ProceedingJoinPoint point) throws Throwable
    {
        Resource resource   = (Resource) point.getArgs()[0];

        if (authorizers.containsKey(resource.getClass()))
        {
            Criterion criterion = authorizers.get(resource.getClass()).delete(getPrincipal());

            check(resource.getClass(), resource.getId(), criterion, "cannot-delete");
        }

        return point.proceed();
    }

    @Pointcut("execution(public * com.github.bednar.persistence.inject.service.Database.Transaction+.list(..))")
    public void list()
    {
    }

    @Around("list()")
    public Object list(ProceedingJoinPoint point) throws Throwable
    {
        return point.proceed();
    }

    private void check(@Nonnull final Class type, @Nonnull final Long key, @Nonnull final Criterion criterion, @Nonnull final String message)
    {
        Database.Transaction transaction = AppContext
                .getInjector()
                .getInstance(Database.class)
                .transaction();

        //noinspection unchecked
        List<Long> list = (List<Long>) transaction.session()
                .createCriteria(type)
                .add(criterion)
                .setProjection(Projections.id())
                .list();

        transaction.finish();

        if (!list.contains(key))
        {
            String mesage = String.format("[%s][%s][%s][%s]", message, type.getName(), key, getPrincipal());

            throw new AuthorizationException(mesage);
        }
    }

    @Nonnull
    private String getPrincipal()
    {
        return SecurityUtils.getSubject().getPrincipal().toString();
    }

    @Nonnull
    private ResourceAuthorize createAuthorizer(@Nonnull final Class<? extends ResourceAuthorize> authorizerType)
    {
        try
        {
            return authorizerType.newInstance();
        }
        catch (Exception e)
        {
            throw new PersitenceAspectException(e);
        }
    }

    @Nonnull
    private Set<Class<? extends ResourceAuthorize>> findAuthorizerTypes()
    {
        return FluentReflection
                .forPackage(AppBootstrap.SYMBOL_BASE_PACKAGE)
                .getSubTypesOf(ResourceAuthorize.class);
    }

    private class PersitenceAspectException extends RuntimeException
    {
        private PersitenceAspectException(@Nonnull final Throwable cause)
        {
            super(cause);
        }
    }
}
