package com.github.chrisruffalo.eeconfig.resources;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.chrisruffalo.eeconfig.resources.support.AnotherBeanDefault;
import com.github.chrisruffalo.eeconfig.resources.support.AnotherBeanInterface;
import com.github.chrisruffalo.eeconfig.resources.support.BeanFallback;
import com.github.chrisruffalo.eeconfig.resources.support.BeanInterface;
import com.github.chrisruffalo.eeconfig.resources.support.BeanOne;
import com.github.chrisruffalo.eeconfig.resources.support.BeanTwo;
import com.github.chrisruffalo.eeconfig.support.DeploymentFactory;

/**
 * Test that bean resolution is operating correctly
 * 
 * @author Chris Ruffalo
 *
 */
@RunWith(Arquillian.class)
public class BeanResolverTest {

    @Deployment
    public static JavaArchive deployment() {
        JavaArchive archive = DeploymentFactory.createDeployment();
        return archive;
    }
    
    @Inject
    private BeanResolver resolver;
    
    @Test
    public void testResolveBean() {
        BeanInterface one = this.resolver.resolveBean(BeanInterface.class, BeanOne.class);
        Assert.assertEquals(BeanOne.class, one.getClass());
    }    
    
    @Test
    public void testResolveAnnotatedBean() {
        BeanInterface two = this.resolver.resolveBean(BeanInterface.class, BeanTwo.class);
        Assert.assertEquals(BeanTwo.class, two.getClass());
    }
    
    @Test
    public void testFallbackBean() {
        BeanInterface fallback = this.resolver.resolveBean(BeanInterface.class, null);
        Assert.assertEquals(BeanFallback.class, fallback.getClass());
    }
    
    @Test
    public void testDefaultBean() {
        AnotherBeanInterface another = this.resolver.resolveBean(AnotherBeanInterface.class, null);
        Assert.assertEquals(AnotherBeanDefault.class, another.getClass());
    }
}
