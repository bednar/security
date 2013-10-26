package com.github.bednar.security.event;

import javax.annotation.Nullable;

import com.github.bednar.base.event.AbstractEvent;

/**
 * @author Jakub Bednář (10/09/2013 5:16 PM)
 */
public class AuthenticateViaFormEvent extends AbstractEvent<Boolean>
{
    private final String username;
    private final String password;

    public AuthenticateViaFormEvent(final @Nullable String username, final @Nullable String password)
    {
        this.username = username;
        this.password = password;
    }

    @Nullable
    public String getUsername()
    {
        return username;
    }

    @Nullable
    public String getPassword()
    {
        return password;
    }
}
