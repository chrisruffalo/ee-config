package com.github.chrisruffalo.eeconfig.resources.configuration;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.chrisruffalo.eeconfig.annotations.Bootstrap;
import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.annotations.Property;
import com.github.chrisruffalo.eeconfig.annotations.Resolver;
import com.github.chrisruffalo.eeconfig.annotations.Source;
import com.github.chrisruffalo.eeconfig.support.DeploymentFactory;

@RunWith(Arquillian.class)
public class BootstrapedConfigurationTest {

	@Deployment
	public static JavaArchive deployment() {
		JavaArchive archive = DeploymentFactory.createDeployment();
		return archive;
	}
	
	@Test
	@Inject
	public void testBootstrappedConfiguration(			
		@Configuration(
			sources={
				@Source(value="resource:${propertiesPath}/${file}1.${extension}", resolve = true),
				@Source(value="resource:${propertiesPath}/${file}2.${extension}", resolve = true),
				@Source(value="resource:${propertiesPath}/${file}3.${extension}", resolve = true),
			},
			resolver=@Resolver(
				bootstrap=@Bootstrap(
					sources=@Source("resource:properties/bootstrap.properties")
				),
				properties={
					@Property(key="file", value="priority")
				}
			)
			
		) 
		org.apache.commons.configuration.Configuration properties) 
	{
		// matches exactly the usual property test from CommonsConfigurationProducerTest
		Assert.assertNotNull(properties);
		// this one has content
		Assert.assertFalse(properties.isEmpty());
		// check content
		Assert.assertEquals("value1", properties.getString("common"));
		Assert.assertEquals("shared", properties.getString("shared"));
		Assert.assertEquals("one", properties.getString("one"));
		Assert.assertNull(properties.getString("two"));
		Assert.assertNull(properties.getString("three"));
	}
	
	@Test
	@Inject
	public void testThatDefaultPropertiesDontMessItUp(			
		@Configuration(
			sources={
				@Source(value="resource:${propertiesPath}/${file}1.${extension}", resolve = true),
				@Source(value="resource:${propertiesPath}/${file}2.${extension}", resolve = true),
				@Source(value="resource:${propertiesPath}/${file}3.${extension}", resolve = true),
			},
			resolver=@Resolver(
				bootstrap=@Bootstrap(
					sources=@Source("resource:properties/bootstrap.properties")
				),
				properties={
					@Property(key="file", value="priority"),
					@Property(key="extension", value="bad"),
					@Property(key="propertiesPath", value="none"),
					@Property(key="file", value="dupe"),
				}
			)
			
		) 
		org.apache.commons.configuration.Configuration properties) 
	{
		// matches exactly the usual property test from CommonsConfigurationProducerTest
		Assert.assertNotNull(properties);
		// this one has content
		Assert.assertFalse(properties.isEmpty());
		// check content
		Assert.assertEquals("value1", properties.getString("common"));
		Assert.assertEquals("shared", properties.getString("shared"));
		Assert.assertEquals("one", properties.getString("one"));
		Assert.assertNull(properties.getString("two"));
		Assert.assertNull(properties.getString("three"));
	}
}
