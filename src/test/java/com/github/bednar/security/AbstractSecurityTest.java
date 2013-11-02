package com.github.bednar.security;

import com.github.bednar.base.http.AppContext;
import com.github.bednar.base.inject.Injector;
import com.github.bednar.test.EmbeddedJetty;
import com.github.bednar.test.SecurityInit;
import org.junit.AfterClass;
import org.junit.Before;
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

    protected Injector injector;

    @Before
    public void before()
    {
        injector = AppContext.getInjector();
    }

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        embeddedJetty = new EmbeddedJetty()
                .webFragments(true)
                .start();

        AppContext.initInjector(embeddedJetty.getServletContext());

        SecurityInit.build()
                .bindSecurityManager(embeddedJetty);
    }

    @AfterClass
    public static void afterClass() throws Exception
    {
        SecurityInit.build()
                .unBindSecurityManager();

        AppContext.clear();

        embeddedJetty.stop();
    }
}