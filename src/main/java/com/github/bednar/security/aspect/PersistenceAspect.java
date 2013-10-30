package com.github.bednar.security.aspect;

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
}
