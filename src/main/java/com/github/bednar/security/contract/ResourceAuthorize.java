package com.github.bednar.security.contract;

/**
 * Criterion used for authorize persist operation.
 *
 * @author Jakub Bednář (31/10/2013 17:35)
 */

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.github.bednar.persistence.contract.Resource;
import org.hibernate.criterion.Criterion;

@ThreadSafe
public interface ResourceAuthorize
{
    /**
     * @return type of resource
     */
    @Nonnull
    public Class<? extends Resource> getType();

    /**
     * @param authenticable logged subject
     *
     * @return which resource can {@code authenticable} read
     */
    @Nonnull
    Criterion read(@Nonnull final Authenticable authenticable);

    /**
     * @param authenticable logged subject
     *
     * @return which resource can {@code authenticable} update
     */
    @Nonnull
    Criterion update(@Nonnull final Authenticable authenticable);

    /**
     * @param authenticable logged subject
     *
     * @return which resource can {@code authenticable} delete
     */
    @Nonnull
    Criterion delete(@Nonnull final Authenticable authenticable);
}
