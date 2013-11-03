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
    Class<? extends Resource> getType();

    /**
     * @param principal of logged subject
     *
     * @return which resource can {@code principal} read
     */
    @Nonnull
    Criterion read(@Nonnull final String principal);

    /**
     * @param principal of logged subject
     *
     * @return which resource can {@code principal} update
     */
    @Nonnull
    Criterion update(@Nonnull final String principal);

    /**
     * @param principal of logged subject
     *
     * @return which resource can {@code principal} delete
     */
    @Nonnull
    Criterion delete(@Nonnull final String principal);
}
