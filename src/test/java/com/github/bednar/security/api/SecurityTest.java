package com.github.bednar.security.api;

import javax.annotation.Nonnull;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.github.bednar.security.AbstractSecurityTest;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jakub Bednář (31/08/2013 3:18 PM)
 */
public class SecurityTest extends AbstractSecurityTest
{
    @Nonnull
    @Override
    protected String getResourcePath()
    {
        return "security";
    }

    @Test
    public void authenticateViaFormSuccess() throws ExecutionException, InterruptedException, UnsupportedEncodingException
    {
        Entity<Form> form = Entity.form(
                new Form()
                        .param("username", "people1")
                        .param("password", "people1"));

        Response response = ClientBuilder.newClient()
                .target(urlPath("authenticateViaForm"))
                .request("application/json")
                .buildPost(form)
                .submit()
                .get();

        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void authenticateViaFormFailure() throws ExecutionException, InterruptedException, UnsupportedEncodingException
    {
        Entity<Form> form = Entity.form(
                new Form()
                        .param("username", "people1")
                        .param("password", "people2"));

        Response response = ClientBuilder.newClient()
                .target(urlPath("authenticateViaForm"))
                .request("application/json")
                .buildPost(form)
                .submit()
                .get();

        Assert.assertEquals(401, response.getStatus());
    }

    @Test
    public void isNotAuthenticated() throws ExecutionException, InterruptedException
    {
        Response response = ClientBuilder.newClient()
                .target(urlPath("isAuthenticated"))
                .request("application/json")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(401, response.getStatus());
    }

    @Test
    public void isAuthenticated() throws ExecutionException, InterruptedException
    {
        Entity<Form> form = Entity.form(
                new Form()
                        .param("username", "people1")
                        .param("password", "people1"));

        Response authenticateViaForm = ClientBuilder.newClient()
                .target(urlPath("authenticateViaForm"))
                .request("application/json")
                .buildPost(form)
                .submit()
                .get();

        Map<String, NewCookie> cookies = authenticateViaForm.getCookies();

        Response isAuthenticated = ClientBuilder.newClient()
                .target(urlPath("isAuthenticated"))
                .request("application/json")
                .cookie(cookies.get(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME))
                .cookie(cookies.get(ShiroHttpSession.DEFAULT_SESSION_ID_NAME))
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, isAuthenticated.getStatus());
    }

    @Test
    public void unAuthenticate() throws ExecutionException, InterruptedException
    {
        Response unAuthenticate = ClientBuilder.newClient()
                .target(urlPath("unAuthenticate"))
                .request("application/json")
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, unAuthenticate.getStatus());
    }

    @Test
    public void unAuthenticateAfterAuthenticate() throws ExecutionException, InterruptedException
    {
        Entity<Form> form = Entity.form(
                new Form()
                        .param("username", "people1")
                        .param("password", "people1"));

        Response authenticateViaForm = ClientBuilder.newClient()
                .target(urlPath("authenticateViaForm"))
                .request("application/json")
                .buildPost(form)
                .submit()
                .get();

        Map<String, NewCookie> cookies = authenticateViaForm.getCookies();

        Response unAuthenticate = ClientBuilder.newClient()
                .target(urlPath("unAuthenticate"))
                .request("application/json")
                .cookie(cookies.get(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME))
                .cookie(cookies.get(ShiroHttpSession.DEFAULT_SESSION_ID_NAME))
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(200, unAuthenticate.getStatus());

        Response isAuthenticated = ClientBuilder.newClient()
                .target(urlPath("isAuthenticated"))
                .request("application/json")
                .cookie(cookies.get(CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME))
                .cookie(cookies.get(ShiroHttpSession.DEFAULT_SESSION_ID_NAME))
                .buildGet()
                .submit()
                .get();

        Assert.assertEquals(401, isAuthenticated.getStatus());
    }
}
