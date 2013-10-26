package com.github.bednar.security;

import com.github.bednar.base.http.AppBootstrap;
import com.github.bednar.base.inject.Injector;
import com.github.bednar.test.EmbeddedJetty;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Jakub Bednář (31/08/2013 3:07 PM)
 */
public abstract class AbstractSecurityTest
{
    /**
     * Class scope
     */
    protected static EmbeddedJetty embeddedJetty;
    protected static Injector injector;

    @BeforeClass
    public static void before() throws Exception
    {
        embeddedJetty = new EmbeddedJetty()
                .webFragments(true)
                .start();

        injector = (Injector) embeddedJetty.getServletContext().getAttribute(AppBootstrap.INJECTOR_KEY);
    }

    @AfterClass
    public static void after() throws Exception
    {
        embeddedJetty.stop();
    }
}