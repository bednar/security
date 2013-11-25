package com.github.bednar.security.contract;

import javax.annotation.Nonnull;

/**
 * @author Jakub Bednář (08/09/2013 11:53 AM)
 */
public interface Authenticable
{
    /**
     * 'account' must be column identifier in database and must be unique.
     *
     * @return account unique identifier
     */
    @Nonnull
    String getAccount();

    /**
     * @return hashed password
     */
    @Nonnull
    String getPassword();
}
