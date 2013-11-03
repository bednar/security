package com.github.bednar.security.contract;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.github.bednar.persistence.contract.Resource;
import org.hibernate.criterion.Criterion;

/**
 * Criterion used for authorize persist operation.
 *
 * @see org.apache.shiro.subject.Subject#getPrincipal() Principal description
 *
 * @author Jakub Bednář (31/10/2013 17:35)
 */
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
     * @return which principals can create new resource of {@code type}
     */
    @Nonnull
    Criterion createNew(@Nonnull final String principal);

    /**
     * @param principal of logged subject
     *
     * @return which resources can {@code principal} read
     */
    @Nonnull
    Criterion read(@Nonnull final String principal);

    /**
     * @param principal of logged subject
     *
     * @return which resources can {@code principal} update
     */
    @Nonnull
    Criterion update(@Nonnull final String principal);

    /**
     * @param principal of logged subject
     *
     * @return which resources can {@code principal} delete
     */
    @Nonnull
    Criterion delete(@Nonnull final String principal);
}
