package com.github.bednar.security.aspect;

import com.github.bednar.base.event.Dispatcher;
import com.github.bednar.persistence.event.ListEvent;
import com.github.bednar.persistence.event.ReadEvent;
import com.github.bednar.persistence.event.SaveEvent;
import com.github.bednar.security.AbstractSecurityTest;
import com.github.bednar.security.resource.People;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jakub Bednář (30/10/2013 16:49)
 */
public class PersistenceAspectTest extends AbstractSecurityTest
{
    private Dispatcher dispatcher;

    @Before
    public void before()
    {
        dispatcher = injector.getInstance(Dispatcher.class);
    }

    @Test
    public void save()
    {
        Integer calls = AspectHelper.saveCall;

        People people = new People();
        people.setAccount("save_aspect");
        people.setPassword("my_super_secret");

        dispatcher.publish(new SaveEvent(people));

        Assert.assertEquals((Integer) (calls + 1), AspectHelper.saveCall);
    }

    @Test
    public void read()
    {
        Integer calls = AspectHelper.readCall;

        People people = new People();
        people.setAccount("read_aspect");
        people.setPassword("my_super_secret");

        dispatcher.publish(new SaveEvent(people));
        dispatcher.publish(new ReadEvent<>(people.getId(), People.class));

        Assert.assertEquals((Integer) (calls + 1), AspectHelper.saveCall);
    }

    @Test
    public void list()
    {
        Integer calls = AspectHelper.listCall;

        dispatcher.publish(new ListEvent<>(Restrictions.eq("id", -1L), People.class));

        Assert.assertEquals((Integer) (calls + 1), AspectHelper.listCall);
    }
}
