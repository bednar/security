package com.github.bednar.security.resource;

import javax.annotation.Nonnull;

import com.github.bednar.persistence.contract.Resource;
import com.github.bednar.security.contract.Authenticable;
import com.github.bednar.security.contract.ResourceAuthorize;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * @author Jakub Bednář (02/11/2013 10:47)
 */
public class PeopleAuthorizer implements ResourceAuthorize
{
    @Nonnull
    @Override
    public Class<? extends Resource> getType()
    {
        return People.class;
    }

    @Nonnull
    @Override
    public Criterion read(@Nonnull final Authenticable authenticable)
    {
        //same people
        return Restrictions.eq("account", authenticable.getAccount());
    }

    @Nonnull
    @Override
    public Criterion update(@Nonnull final Authenticable authenticable)
    {
        //same people
        return Restrictions.eq("account", authenticable.getAccount());
    }

    @Nonnull
    @Override
    public Criterion delete(@Nonnull final Authenticable authenticable)
    {
        //only devil can delete YOU ;)
        if (authenticable.getAccount().equals("people3"))
        {
            //all
            return Restrictions.not(Restrictions.eq("id", -1L));
        }
        else
        {
            //nothing
            return Restrictions.eq("id", -1L);
        }
    }
}
