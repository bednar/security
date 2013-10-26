package com.github.bednar.security.event;

import javax.annotation.Nonnull;

import com.github.bednar.base.event.AbstractSubscriber;
import com.mycila.event.Event;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * @author Jakub Bednář (14/09/2013 3:11 PM)
 */
public class UnAuthenticateSubscriber extends AbstractSubscriber<UnAuthenticateEvent>
{
    @Nonnull
    @Override
    public Class<UnAuthenticateEvent> eventType()
    {
        return UnAuthenticateEvent.class;
    }

    @Override
    public void onEvent(final Event<UnAuthenticateEvent> event) throws Exception
    {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null)
        {
            subject.logout();
        }

        event.getSource().success(null);
    }
}
