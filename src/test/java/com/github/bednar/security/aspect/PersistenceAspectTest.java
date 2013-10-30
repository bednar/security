package com.github.bednar.security.aspect;

import javax.annotation.Nonnull;
import java.util.List;

import com.github.bednar.base.event.Dispatcher;
import com.github.bednar.persistence.event.ListEvent;
import com.github.bednar.security.AbstractSecurityTest;
import com.github.bednar.security.resource.People;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (30/10/2013 16:49)
 */
public class PersistenceAspectTest extends AbstractSecurityTest
{
    @Test
    public void listPeople()
    {
        Integer listCall = AspectHelper.listCall;

        ListEvent<People> event = new ListEvent<People>(Restrictions.eq("id", -1L), People.class)
        {
            @Override
            public void success(@Nonnull final List<People> value)
            {
                Assert.assertEquals(0, value.size());
            }

            @Override
            public void fail(@Nonnull final Throwable error)
            {
                Assert.fail();
            }
        };

        Dispatcher dispatcher = injector.getInstance(Dispatcher.class);
        dispatcher.publish(event);

        Assert.assertEquals((Integer)(listCall + 1), AspectHelper.listCall);
    }
}