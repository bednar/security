package com.github.bednar.security.resource;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.github.bednar.persistence.contract.Resource;
import com.github.bednar.security.contract.Authenticable;

/**
 * @author Jakub Bednář (08/09/2013 12:01 PM)
 */
@Entity
public class People extends Resource implements Authenticable
{
    @Column(nullable = false, length = 100)
    private String account;

    @Column(nullable = false, length = 500)
    private String password;

    @Nonnull
    @Override
    public String getAccount()
    {
        return account;
    }

    public void setAccount(final @Nonnull String account)
    {
        this.account = account;
    }

    @Nonnull
    @Override
    public String getPassword()
    {
        return password;
    }

    public void setPassword(final @Nonnull String password)
    {
        this.password = password;
    }
}
