package com.github.chrisruffalo.eeconfig.resources.configuration;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.resources.configuration.extensions.DummyConfigurationSource;
import com.github.chrisruffalo.eeconfig.resources.configuration.extensions.SharedTestConfigurationSourceLocator;
import com.github.chrisruffalo.eeconfig.resources.configuration.extensions.SharedTestPropertyResolver;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.IConfigurationSource;
import com.github.chrisruffalo.eeconfig.support.DeploymentFactory;

@RunWith(Arquillian.class)
public class PluginUseTest {

	@Inject
	private SharedTestPropertyResolver resolver;
	
	@Deployment
	public static WebArchive deployment() {
		WebArchive archive = DeploymentFactory.createDeployment();

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
		paths = {
			"${java.io.tmpdir}/priority2.properties",
			"${java.io.tmpdir}/priority1.properties",
		},
		resolveSystemProperties = true,
		propertyResolver = SharedTestPropertyResolver.class
	) List<IConfigurationSource> sources) {
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
		paths = {
			"resource:properties/priority1.properties",
			"resource:properties/priority2.properties",
			"resource:properties/priority3.properties",
			"resource:properties/priority4.properties"
		},
		resolveSystemProperties = true,
		locator = SharedTestConfigurationSourceLocator.class
	) List<IConfigurationSource> sources) {
		Assert.assertEquals(5, sources.size());
		for(IConfigurationSource source : sources) {
			Assert.assertTrue(source instanceof DummyConfigurationSource);
		}	
	}	
}
