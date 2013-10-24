package com.github.chrisruffalo.eeconfig.resources.configuration;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.annotations.Source;
import com.github.chrisruffalo.eeconfig.resources.configuration.extensions.DummySource;
import com.github.chrisruffalo.eeconfig.resources.configuration.extensions.SharedTestPropertyResolver;
import com.github.chrisruffalo.eeconfig.resources.configuration.extensions.SharedTestSourceLocator;
import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.support.DeploymentFactory;

@RunWith(Arquillian.class)
public class PluginUseTest {

	@Inject
	private SharedTestPropertyResolver resolver;
	
	@Deployment
	public static JavaArchive deployment() {
		JavaArchive archive = DeploymentFactory.createDeployment();
		return archive;
	}
	
	/**
	 * Test how and that the system property resolver is loaded
	 * 
	 * @param properties
	 */
	@Test
	@Inject
	public void testPropertyResolverPlugin(@Configuration(
		sources={
			@Source(value="${java.io.tmpdir}/priority2.properties", resolve=true),
			@Source(value="${java.io.tmpdir}/priority1.properties", resolve=true),
		},
		resolver = SharedTestPropertyResolver.class
	) List<ISource> sources) {
		// shared resolver has been called some number of times
		Assert.assertTrue(this.resolver.getCount() > 0);
	}
	
	/**
	 * Test how and that the system property resolver is loaded
	 * 
	 * @param properties
	 */
	@Test
	@Inject
	public void testPropertyResolverPluginWithOneResolve(@Configuration(
		sources = {
			@Source(value="${java.io.tmpdir}/priority2.properties", resolve=true),
			@Source(value="${java.io.tmpdir}/priority1.properties", resolve=false),
		},
		resolver = SharedTestPropertyResolver.class
	) List<ISource> sources) {
		// shared resolver has been called some number of times
		Assert.assertTrue(this.resolver.getCount() > 0);
	}
	
	/**
	 * Test how and that the configuration locator is loaded
	 * 
	 * @param properties
	 */
	@Test
	@Inject
	public void testLocatorPlugin(@Configuration(
		sources = {
			@Source(value="properties/priority1.properties", locator=SharedTestSourceLocator.class),
			@Source(value="properties/priority2.properties", locator=SharedTestSourceLocator.class),
			@Source(value="properties/priority3.properties", locator=SharedTestSourceLocator.class),
			@Source(value="properties/priority4.properties", locator=SharedTestSourceLocator.class)
		}
	) List<ISource> sources) {
		Assert.assertEquals(4, sources.size());
		for(ISource source : sources) {
			Assert.assertEquals(DummySource.class, source.getClass());
		}	
	}	
}
