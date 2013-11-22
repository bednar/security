package com.github.bednar.security.api;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import com.github.bednar.base.event.Dispatcher;
import com.github.bednar.security.event.IsAuthenticatedEvent;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.jboss.resteasy.core.ResourceMethodInvoker;

/**
 * @author Jakub Bednář (22/11/2013 08:26)
 */
@Provider
public class SecurityFilter implements ContainerRequestFilter
{
    @Inject
    private Dispatcher dispatcher;

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException
    {
        ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext
                .getProperty(ResourceMethodInvoker.class.getCanonicalName());

        RequiresAuthentication authentication = methodInvoker
                .getMethod().getAnnotation(RequiresAuthentication.class);

        if (authentication == null)
        {
            return;
        }

        //check authentication
        dispatcher.publish(new IsAuthenticatedEvent()
        {
            @Override
            public void success(@Nonnull final Boolean value)
            {
                if (!Boolean.TRUE.equals(value))
                {
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
                }
            }
        });
    }
}
