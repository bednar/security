package com.github.bednar.security.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.github.bednar.base.api.ApiResource;
import com.github.bednar.base.event.Dispatcher;
import com.github.bednar.security.event.AuthenticateViaFormEvent;
import com.github.bednar.security.event.IsAuthenticatedEvent;
import com.github.bednar.security.event.UnAuthenticateEvent;
import com.wordnik.swagger.annotations.Api;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;

/**
 * @author Jakub Bednář (31/08/2013 10:32 AM)
 */
@Path("/security")
@Api(value = "Security API", description = "API for authentication and security interaction.")
public class Security implements ApiResource
{
    @Inject
    private Dispatcher dispatcher;

    /**
     * Simple Web-Authentication via HTML Form. After success authentication return HTTP 200 OK response if
     * authentication not finish success, than return HTTP 401 UNAUTHORIZED with reason message.
     *
     * @param username Form parameter with id="username"
     * @param password Form parameter with id="password"
     */
    @POST
    @Path("/authenticateViaForm")
    public void authenticateViaForm(@Nonnull @Suspend final AsynchronousResponse response,
                                    @Nullable @FormParam("username") final String username,
                                    @Nullable @FormParam("password") final String password)
    {
        dispatcher.publish(new AuthenticateViaFormEvent(username, password)
        {
            @Override
            public void success(@Nonnull final Boolean authentiacted)
            {
                if (authentiacted)
                {
                    response.setResponse(Response.ok().build());
                }
                else
                {
                    response.setResponse(Response.status(Response.Status.UNAUTHORIZED).build());
                }
            }
        });
    }

    /**
     * If actual session is authenticated, than invalidate session and unauthenticated it. Always return HTTP 200 OK.
     */
    @GET
    @Path("/unAuthenticate")
    public void unAuthenticate(@Nonnull @Suspend final AsynchronousResponse response)
    {
        dispatcher.publish(new UnAuthenticateEvent()
        {
            @Override
            public void success(@Nonnull final Void value)
            {
                response.setResponse(Response.ok().build());
            }
        });
    }

    /**
     * Return HTTP 200 OK response if actual server session is authenticated and HTTP 401 UNAUTHORIZED if not.
     */
    @GET
    @Path("/isAuthenticated")
    public void isAuthenticated(@Nonnull @Suspend final AsynchronousResponse response)
    {
        dispatcher.publish(new IsAuthenticatedEvent()
        {
            @Override
            public void success(@Nonnull final Boolean isAuthenticated)
            {
                if (isAuthenticated)
                {
                    response.setResponse(Response.ok().build());
                }
                else
                {
                    response.setResponse(Response.status(Response.Status.UNAUTHORIZED).build());
                }
            }
        });
    }
}
