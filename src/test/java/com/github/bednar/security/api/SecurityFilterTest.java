package com.github.bednar.security.api;

import javax.annotation.Nonnull;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.github.bednar.security.AbstractSecurityTest;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (22/11/2013 08:58)
 */
public class SecurityFilterTest extends AbstractSecurityTest
{
    @Nonnull
    @Override
    protected String getResourcePath()
    {
        return "securityFilter";
    }

    @Test
    public void notAuthenticated() throws ExecutionException, InterruptedException
    {
        Response authenticated = ClientBuilder.newClient()
                .target(urlPath("requiresAuthentication"))
                .request("application/json")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(403, authenticated.getStatus());
    }

    @Test
    public void authenticated() throws ExecutionException, InterruptedException
    {
        Entity<Form> form = Entity.form(
                new Form()
                        .param("username", "people1")
                        .param("password", "people1"));

        Response authenticateViaForm = ClientBuilder.newClient()
                .target(urlPath("security", "authenticateViaForm"))
                .request("application/json")
                .buildPost(form)
                .submit()
                .get();

        Map<String, NewCookie> cookies = authenticateViaForm.getCookies();

        Response authenticated = ClientBuilder.newClient()
                .target(urlPath("requiresAuthentication"))
                .request("application/json")
                .cookie(cookies.get(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME))
                .cookie(cookies.get(ShiroHttpSession.DEFAULT_SESSION_ID_NAME))
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, authenticated.getStatus());
    }
}
