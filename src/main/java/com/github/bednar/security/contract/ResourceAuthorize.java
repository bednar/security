package com.github.bednar.security.contract;

/**
 * Criterion used for authorize persist operation.
 *
 * @author Jakub Bednář (31/10/2013 17:35)
 */

import javax.annotation.Nonnull;

import com.github.bednar.persistence.contract.Resource;
import org.hibernate.criterion.Criterion;

public interface ResourceAuthorize<R extends Resource>
{
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
