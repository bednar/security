package com.github.bednar.security.event;

import javax.annotation.Nonnull;

import com.github.bednar.base.event.AbstractSubscriber;
import com.mycila.event.Event;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (10/09/2013 5:16 PM)
 */
public class AuthenticateViaFormSubscriber extends AbstractSubscriber<AuthenticateViaFormEvent>
{
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticateViaFormSubscriber.class);

    @Nonnull
    @Override
    public Class<AuthenticateViaFormEvent> eventType()
    {
        return AuthenticateViaFormEvent.class;
    }

    @Override
    public void onEvent(final Event<AuthenticateViaFormEvent> event) throws Exception
    {
        String username = event.getSource().getUsername();
        String password = event.getSource().getPassword();

        AuthenticationToken token = new UsernamePasswordToken(username, password);

        Subject subject = SecurityUtils.getSubject();
        try
        {
            subject.login(token);

            event.getSource().success(true);
        }
        catch (AuthenticationException au)
        {
            event.getSource().success(false);

            LOG.error("[error-authentication]", au);
        }
    }
}
