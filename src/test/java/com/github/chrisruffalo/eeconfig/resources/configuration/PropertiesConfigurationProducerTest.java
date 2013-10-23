package com.github.chrisruffalo.eeconfig.resources.configuration;


import java.util.Properties;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.annotations.Source;
import com.github.chrisruffalo.eeconfig.strategy.locator.ResourceLocator;
import com.github.chrisruffalo.eeconfig.support.DeploymentFactory;

@RunWith(Arquillian.class)
public class PropertiesConfigurationProducerTest {

	//@Inject
	//@AutoLogger
	//private Logger logger;
	
	@Deployment
	public static WebArchive deployment() {
		WebArchive archive = DeploymentFactory.createDeployment();

		return archive;
	}
	
	/**
	 * Test that no paths produce no valid properties
	 * 
	 * @param properties
	 */
	@Test
	@Inject
	public void testEmptyPaths(@Configuration Properties properties) {
		Assert.assertNotNull(properties);
		Assert.assertTrue(properties.isEmpty());
	}
	
	/**
	 * Test that non-existent properties produces no values
	 * 
	 * @param properties
	 */
	@Test
	@Inject
	public void testNonexistantPaths(
		@Configuration(
			sources={
				@Source(value="no/path/here.properties"),
				@Source(value="/bad/path/file.properties")
			}
		)
		Properties properties) 
	{
		Assert.assertNotNull(properties);
		Assert.assertTrue(properties.isEmpty());
	}
	
	/**
	 * Test priority ordering and that, without merge,
	 * the values of only one property file are loaded
	 * 
	 * @param properties
	 */
	@Test
	@Inject
	//@Ignore
	public void testNoMergeResources(@Configuration(
		sources = {
			@Source(value="properties/priority1.properties", locator=ResourceLocator.class),
			@Source(value="properties/priority2.properties", locator=ResourceLocator.class),
			@Source(value="properties/priority3.properties", locator=ResourceLocator.class),
		}
	) Properties properties) {
		Assert.assertNotNull(properties);
		// this one has content
		Assert.assertFalse(properties.isEmpty());
		// check content
		Assert.assertEquals("value1", properties.get("common"));
		Assert.assertEquals("shared", properties.get("shared"));
		Assert.assertEquals("one", properties.get("one"));
		Assert.assertNull(properties.get("two"));
		Assert.assertNull(properties.get("three"));
	}
	
	/**
	 * Test priority ordering and that, without merge,
	 * the values of only one property file are loaded.
	 * <br/>
	 * This version is slightly different and tests that
	 * the ordering/priority mechanism is working
	 * 
	 * @param properties
	 */
	@Test
	@Inject
	//@Ignore
	public void testNoMergeResourcesWithDifferentOrder(@Configuration(
		sources = {
			@Source(value="properties/priority2.properties", locator=ResourceLocator.class),
			@Source(value="properties/priority1.properties", locator=ResourceLocator.class),
			@Source(value="properties/priority3.properties", locator=ResourceLocator.class),
		}
	) Properties properties) {
		Assert.assertNotNull(properties);
		// this one has content
		Assert.assertFalse(properties.isEmpty());
		// check content
		Assert.assertEquals("value2", properties.get("common"));
		Assert.assertEquals("shared", properties.get("shared"));
		Assert.assertEquals("two", properties.get("two"));
		Assert.assertNull(properties.get("one"));
		Assert.assertNull(properties.get("three"));
	}
	
	/**
	 * Test merging resources with priority ordering
	 * 
	 * @param properties
	 */
	@Test
	@Inject
	//@Ignore
	public void testMergedResources(@Configuration(
		sources = {
			@Source(value="properties/priority1.properties", locator=ResourceLocator.class),
			@Source(value="properties/priority2.properties", locator=ResourceLocator.class),
			@Source(value="properties/priority3.properties", locator=ResourceLocator.class),
		},
		merge = true
	) Properties properties) {
		Assert.assertNotNull(properties);
		// this one has content
		Assert.assertFalse(properties.isEmpty());
		// check content
		Assert.assertEquals("value1", properties.get("common"));
		Assert.assertEquals("shared", properties.get("shared"));
		Assert.assertEquals("one", properties.get("one"));
		Assert.assertEquals("two", properties.get("two"));
		Assert.assertEquals("three", properties.get("three"));
		Assert.assertEquals("true", properties.get("minor"));
	}
	
}
