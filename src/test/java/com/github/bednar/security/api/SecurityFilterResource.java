package com.github.bednar.security.api;

import javax.annotation.Nonnull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.github.bednar.base.api.ApiResource;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;

/**
 * @author Jakub Bednář (22/11/2013 08:59)
 */
@Path("/securityFilter")
public class SecurityFilterResource implements ApiResource
{
    @GET
    @RequiresAuthentication
    @Path("/requiresAuthentication")
    public void requiresAuthentication(@Nonnull @Suspend final AsynchronousResponse response)
    {
        response.setResponse(Response.ok().build());
    }
}
