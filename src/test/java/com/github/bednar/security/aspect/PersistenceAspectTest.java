package com.github.bednar.security.aspect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import com.github.bednar.base.event.Dispatcher;
import com.github.bednar.persistence.event.DeleteEvent;
import com.github.bednar.persistence.event.ListEvent;
import com.github.bednar.persistence.event.ReadEvent;
import com.github.bednar.persistence.event.SaveEvent;
import com.github.bednar.persistence.event.UniqueEvent;
import com.github.bednar.persistence.inject.service.Database;
import com.github.bednar.security.AbstractSecurityTest;
import com.github.bednar.security.resource.People;
import com.github.bednar.test.SecurityInit;
import org.apache.shiro.authz.AuthorizationException;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.github.bednar.test.AssertUtil.assertException;

/**
 * @author Jakub Bednář (30/10/2013 16:49)
 */
public class PersistenceAspectTest extends AbstractSecurityTest
{
    private Dispatcher dispatcher;

    @Before
    public void before()
    {
        super.before();

        dispatcher = injector.getInstance(Dispatcher.class);
    }

    @Test
    public void saveAuthorized()
    {
        Database.Transaction transaction = injector
                .getInstance(Database.class)
                .transaction();

        People people = (People) transaction.session().byId(People.class).load(1L);

        transaction.finish();

        dispatcher.publish(new SaveEvent(people));
    }

    @Test
    public void saveNotAuthorized()
    {
        Database.Transaction transaction = injector
                .getInstance(Database.class)
                .transaction();

        People people = (People) transaction.session().byId(People.class).load(2L);

        transaction.finish();

        try
        {
            dispatcher.publish(new SaveEvent(people));
        }
        catch (Exception e)
        {
            assertException(AuthorizationException.class, e);
        }
    }

    @Test
    public void createNewAuthorized()
    {
        SecurityInit
                .build()
                .destroySubject();

        SecurityInit
                .build()
                .buildSubject("people3");

        People people = new People();

        people.setAccount("createNewAuthorized");
        people.setPassword("secret");

        dispatcher.publish(new SaveEvent(people));
    }

    @Test
    public void createNewNotAuthorized()
    {
        People people = new People();

        people.setAccount("createNewNotAuthorized");
        people.setPassword("secret");

        try
        {
            dispatcher.publish(new SaveEvent(people));
        }
        catch (Exception e)
        {
            assertException(AuthorizationException.class, e);
        }
    }

    @Test
    public void readAuthorized()
    {
        dispatcher.publish(new ReadEvent<>(1L, People.class));
    }

    @Test
    public void readNotAuthorized()
    {
        try
        {
            dispatcher.publish(new ReadEvent<>(2L, People.class));
        }
        catch (Exception e)
        {
            assertException(AuthorizationException.class, e);
        }
    }

    @Test
    public void deleteAuthorized()
    {
        Database.Transaction transaction = injector
                .getInstance(Database.class)
                .transaction();

        People people = (People) transaction.session().byId(People.class).load(4L);

        transaction.finish();

        SecurityInit
                .build()
                .destroySubject();

        SecurityInit
                .build()
                .buildSubject("people3");

        dispatcher.publish(new DeleteEvent(people));
    }

    @Test
    public void deleteNotAuthorized()
    {
        Database.Transaction transaction = injector
                .getInstance(Database.class)
                .transaction();

        People people = (People) transaction.session().byId(People.class).load(5L);

        transaction.finish();

        try
        {
            dispatcher.publish(new DeleteEvent(people));
        }
        catch (Exception e)
        {
            assertException(AuthorizationException.class, e);
        }
    }

    @Test
    public void list()
    {
        ListEvent<People> events = new ListEvent<People>(Restrictions.not(Restrictions.eq("id", -1L)), People.class)
        {
            @Override
            public void success(@Nonnull final List<People> peoples)
            {
                Assert.assertEquals(1, peoples.size());
            }
        };

        dispatcher.publish(events);
    }

    @Test
    public void uniqueWithPermission()
    {
        dispatcher.publish(new UniqueEvent<People>(Restrictions.eq("account", "people1"), People.class)
        {
            @Override
            public void success(@Nullable final People people)
            {
                Assert.assertNotNull(people);
                Assert.assertEquals((Object) 1L, people.getId());
                Assert.assertEquals("people1", people.getAccount());
            }
        });
    }

    @Test
    public void uniqueWithoutPermission()
    {
        dispatcher.publish(new UniqueEvent<People>(Restrictions.eq("account", "people2"), People.class)
        {
            @Override
            public void success(@Nullable final People value)
            {
                Assert.assertNull(value);
            }
        });
    }
}
