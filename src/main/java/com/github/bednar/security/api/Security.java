package com.github.bednar.security.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.bednar.base.api.ApiResource;
import com.github.bednar.base.event.Dispatcher;
import com.github.bednar.security.event.AuthenticateViaFormEvent;
import com.github.bednar.security.event.IsAuthenticatedEvent;
import com.github.bednar.security.event.UnAuthenticateEvent;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;

/**
 * @author Jakub Bednář (31/08/2013 10:32 AM)
 */
@Path("security")
@Consumes("application/json")
@Produces("application/json")
@Api(
        value = "Authentication",
        description = "API for authentication subject. Supported authentication method: HTML Form.")
public class Security implements ApiResource
{
    @Inject
    private Dispatcher dispatcher;

    /**
     * @param username Form parameter with id="username"
     * @param password Form parameter with id="password"
     */
    @POST
    @Path("authenticateViaForm")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @ApiOperation(
            position = 1,
            value = "Simple Web-Authentication via HTML Form. After success authentication " +
                    "return HTTP 200 OK response if authentication not finish success, " +
                    "than return HTTP 401 UNAUTHORIZED.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "{}"),
            @ApiResponse(code = 401, message = "{}")})
    public void authenticateViaForm(@Nonnull @Suspend final AsynchronousResponse response,
                                    @Nullable  @FormParam("username")
                                    @ApiParam(name = "username", value = "Username of Subject (Form param)", required = true)
                                    final String username,
                                    @Nullable @FormParam("password")
                                    @ApiParam(name = "password", value = "Password of Subject (Form param)", required = true)
                                    final String password)
    {
        dispatcher.publish(new AuthenticateViaFormEvent(username, password)
        {
            @Override
            public void success(@Nonnull final Boolean authentiacted)
            {
                Response.ResponseBuilder builder;
                if (authentiacted)
                {
                    builder = Response.ok();
                }
                else
                {
                    builder = Response.status(Response.Status.UNAUTHORIZED);
                }

                response.setResponse(builder.entity("{}").build());
            }
        });
    }

    /**
     * If actual session is authenticated, than invalidate session and unauthenticated it. Always return HTTP 200 OK.
     */
    @GET
    @Path("unAuthenticate")
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
    @Path("isAuthenticated")
    @Consumes("application/json")
    @Produces("application/json")
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
