package io.github.chrisruffalo.ee6config.resources;

import io.github.chrisruffalo.ee6config.annotations.Configuration;
import io.github.chrisruffalo.ee6config.support.DeploymentFactory;

import java.util.Properties;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PropertiesConfigurationProducerTest {

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
	public void testEmptyPaths(@Configuration(paths={}) Properties properties) {
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
	public void testNonexistantPaths(@Configuration(paths={"resource:no/path/here.properties","/bad/path/file.properties"}) Properties properties) {
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
		paths = {
			"resource:properties/priority1.properties",
			"resource:properties/priority2.properties",
			"resource:properties/priority3.properties"
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
		paths = {
			"resource:properties/priority2.properties",
			"resource:properties/priority1.properties",
			"resource:properties/priority3.properties"
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
		paths = {
			"resource:properties/priority1.properties",
			"resource:properties/priority2.properties",
			"resource:properties/priority3.properties"
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
