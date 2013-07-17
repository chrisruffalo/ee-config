package io.github.chrisruffalo.ee6config.resources.configuration;

import io.github.chrisruffalo.ee6config.annotations.Configuration;
import io.github.chrisruffalo.ee6config.support.DeploymentFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.io.ByteStreams;

@RunWith(Arquillian.class)
public class CommonsConfigurationProducerTest {

	//@Inject
	//private Logger logger;
	
	@Deployment
	public static WebArchive deployment() {
		WebArchive archive = DeploymentFactory.createDeployment();

		return archive;
	}
	
	@BeforeClass
	public static void moveFilesToTemp() throws IOException {
		String tempDirPath = System.getProperty("java.io.tmpdir");
		File tempDir = new File(tempDirPath);
		
		// fail if it can't be used
		if(!tempDir.exists() || !tempDir.isDirectory()) {
			Assert.fail("Could not find system temporary folder");
		}
		
		// create bases
		String[] properties = new String[]{
			"priority1.properties",
			"priority2.properties",
			"priority3.properties"
		};
		
		for(String propertyPath : properties) {
			// get output
			File outputFile = new File(tempDir.getAbsolutePath() + File.separator + propertyPath);
			
			// delete output file if it exists
			if(outputFile.exists()) {
				outputFile.delete();
			}
			
			// create file
			outputFile.createNewFile();
			
			OutputStream output;
			try {
				output = new FileOutputStream(outputFile);
			} catch (FileNotFoundException e) {
				output = new ByteArrayOutputStream();
			}
			
			// get input
			InputStream input = 
				Thread.currentThread().getContextClassLoader().getResourceAsStream("properties/" + propertyPath);
			
			// copy
			ByteStreams.copy(input, output);
			
			// flush output
			try {
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// close streams
			input.close();
			output.close();
		}
		
		
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
	
	/**
	 * Test priority ordering with merge.
	 * <br/>
	 * This version uses a slightly different ordering
	 * and it calls out to the filesystem using a system
	 * property resolved path. 
	 * <br/>
	 * Also loads mixed file and resource items
	 * 
	 * @param properties
	 */
	@Test
	@Inject
	public void testMergeFilesWithSystemPropertiesAndMixedResource(@Configuration(
		paths = {
			"${java.io.tmpdir}/priority2.properties",
			"${java.io.tmpdir}/priority1.properties",
			"resource:properties/priority3.properties"
		},
		resolveSystemProperties = true,
		merge = true
	) org.apache.commons.configuration.Configuration properties) {
		Assert.assertNotNull(properties);
		// this one has content
		Assert.assertFalse(properties.isEmpty());
		// check content
		Assert.assertEquals("value2", properties.getString("common"));
		Assert.assertEquals("shared", properties.getString("shared"));
		Assert.assertEquals("one", properties.getString("one"));
		Assert.assertEquals("two", properties.getString("two"));
		Assert.assertEquals("three", properties.getString("three"));
		Assert.assertEquals("true", properties.getString("minor"));
	}
	
	/**
	 * Test that system properties can be set to not resolve which
	 * results in not being able to find configuration items with
	 * system properties in them.
	 * 
	 * @param properties
	 */
	@Test
	@Inject
	public void testMergeFilesWithoutSystemPropertiesAndMixedResource(@Configuration(
		paths = {
			"${java.io.tmpdir}/priority2.properties",
			"${java.io.tmpdir}/priority1.properties",
			"resource:properties/priority3.properties"
		},
		resolveSystemProperties = false,
		merge = true
	) org.apache.commons.configuration.Configuration properties) {
		Assert.assertNotNull(properties);
		// this one has content
		Assert.assertFalse(properties.isEmpty());
		// check content
		Assert.assertEquals("value3", properties.getString("common"));
		Assert.assertEquals("shared", properties.getString("shared"));
		Assert.assertNull(properties.getString("one"));
		Assert.assertNull(properties.getString("two"));
		Assert.assertEquals("three", properties.getString("three"));
		Assert.assertEquals("false", properties.getString("minor"));
	}
}
