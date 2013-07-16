package io.github.chrisruffalo.ee6config.resources.configuration;

import io.github.chrisruffalo.ee6config.annotations.Configuration;
import io.github.chrisruffalo.ee6config.support.DeploymentFactory;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CommonsConfigurationProducerTest {

	//@Inject
	//private Logger logger;
	
	@Deployment
	public static WebArchive deployment() {
		WebArchive archive = DeploymentFactory.createDeployment()
			// add resources specific to this test
			.addAsResource("properties/priority1.properties")
			.addAsResource("properties/priority2.properties")
		    .addAsResource("properties/priority3.properties")
		;

		return archive;
	}
	
	/**
	 * Test that no paths produce no valid properties
	 * 
	 * @param properties
	 */
	@Test
	@Inject
	public void testEmptyPaths(@Configuration(paths={}) org.apache.commons.configuration.Configuration properties) {
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
	public void testNonexistantPaths(@Configuration(paths={"resource:no/path/here.properties","/bad/path/file.properties"}) org.apache.commons.configuration.Configuration properties) {
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
	public void testNoMergeResources(@Configuration(
		paths = {
			"resource:properties/priority1.properties",
			"resource:properties/priority2.properties",
			"resource:properties/priority3.properties"
		}
	) org.apache.commons.configuration.Configuration properties) {
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
	public void testNoMergeResourcesWithDifferentOrder(@Configuration(
		paths = {
			"resource:properties/priority2.properties",
			"resource:properties/priority1.properties",
			"resource:properties/priority3.properties"
		}
	) org.apache.commons.configuration.Configuration properties) {
		Assert.assertNotNull(properties);
		// this one has content
		Assert.assertFalse(properties.isEmpty());
		// check content
		Assert.assertEquals("value2", properties.getString("common"));
		Assert.assertEquals("shared", properties.getString("shared"));
		Assert.assertEquals("two", properties.getString("two"));
		Assert.assertNull(properties.getString("one"));
		Assert.assertNull(properties.getString("three"));
	}
	
	/**
	 * Test merging resources with priority ordering
	 * 
	 * @param properties
	 */
	@Test
	@Inject
	public void testMergedResources(@Configuration(
		paths = {
			"resource:properties/priority1.properties",
			"resource:properties/priority2.properties",
			"resource:properties/priority3.properties"
		},
		merge = true
	) org.apache.commons.configuration.Configuration properties) {
		Assert.assertNotNull(properties);
		// this one has content
		Assert.assertFalse(properties.isEmpty());
		// check content
		Assert.assertEquals("value1", properties.getString("common"));
		Assert.assertEquals("shared", properties.getString("shared"));
		Assert.assertEquals("one", properties.getString("one"));
		Assert.assertEquals("two", properties.getString("two"));
		Assert.assertEquals("three", properties.getString("three"));
		Assert.assertEquals("true", properties.getString("minor"));
	}
	
}
