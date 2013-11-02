package com.github.bednar.security.aspect;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

import com.github.bednar.base.http.AppBootstrap;
import com.github.bednar.base.utils.reflection.FluentReflection;
import com.github.bednar.persistence.contract.Resource;
import com.github.bednar.security.contract.ResourceAuthorize;
import com.google.common.collect.Maps;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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
        LOG.info("Before save: {}", point.getSignature());

        Object ret = point.proceed();

        LOG.info("After save: {}", point.getSignature());

        AspectHelper.saveCall += 1;

        return ret;
    }

    @Pointcut("execution(public * com.github.bednar.persistence.inject.service.Database.Transaction+.read(..))")
    public void read()
    {
    }

    @Around("read()")
    public Object read(ProceedingJoinPoint point) throws Throwable
    {
        LOG.info("Before read: {}", point.getSignature());

        Object ret = point.proceed();

        LOG.info("After read: {}", point.getSignature());

        AspectHelper.readCall += 1;

        return ret;
    }

    @Pointcut("execution(public * com.github.bednar.persistence.inject.service.Database.Transaction+.delete(..))")
    public void delete()
    {
    }

    @Around("delete()")
    public Object delete(ProceedingJoinPoint point) throws Throwable
    {
        LOG.info("Before delete: {}", point.getSignature());

        Object ret = point.proceed();

        LOG.info("After delete: {}", point.getSignature());

        AspectHelper.deleteCall += 1;

        return ret;
    }

    @Pointcut("execution(public * com.github.bednar.persistence.inject.service.Database.Transaction+.list(..))")
    public void list()
    {
    }

    @Around("list()")
    public Object list(ProceedingJoinPoint point) throws Throwable
    {
        LOG.info("Before list: {}", point.getSignature());

        Object ret = point.proceed();

        LOG.info("After list: {}", point.getSignature());

        AspectHelper.listCall += 1;

        return ret;
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
