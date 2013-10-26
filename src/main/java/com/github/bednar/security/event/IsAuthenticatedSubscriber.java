package com.github.bednar.security.event;

import javax.annotation.Nonnull;

import com.github.bednar.base.event.AbstractSubscriber;
import com.mycila.event.Event;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * @author Jakub Bednář (31/08/2013 3:27 PM)
 */
public class IsAuthenticatedSubscriber extends AbstractSubscriber<IsAuthenticatedEvent>
{
    @Nonnull
    @Override
    public Class<IsAuthenticatedEvent> eventType()
    {
        return IsAuthenticatedEvent.class;
    }

    @Override
    public void onEvent(final Event<IsAuthenticatedEvent> event) throws Exception
    {
        Subject subject = SecurityUtils.getSubject();

        event.getSource().success(subject != null && subject.isAuthenticated());
    }
}
