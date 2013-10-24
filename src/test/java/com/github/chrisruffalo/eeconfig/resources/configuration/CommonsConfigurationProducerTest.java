package com.github.chrisruffalo.eeconfig.resources.configuration;


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
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.annotations.Source;
import com.github.chrisruffalo.eeconfig.strategy.locator.ResourceLocator;
import com.github.chrisruffalo.eeconfig.support.DeploymentFactory;
import com.google.common.io.ByteStreams;

@RunWith(Arquillian.class)
public class CommonsConfigurationProducerTest {

	//@Inject
	//@AutoLogger
	//private Logger logger;
	
	@Deployment
	public static JavaArchive deployment() {
		JavaArchive archive = DeploymentFactory.createDeployment();
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
	public void testEmptyPaths(@Configuration org.apache.commons.configuration.Configuration properties) {
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
					@Source(value="no/path/here.properties", locator=ResourceLocator.class),
					@Source(value="/bad/path/file.properties", locator=ResourceLocator.class)
				}
			) 
			org.apache.commons.configuration.Configuration properties
		) {
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
	public void testNoMergeResources(			
		@Configuration(
			sources={
				@Source(value="properties/priority1.properties", locator=ResourceLocator.class),
				@Source(value="properties/priority2.properties", locator=ResourceLocator.class),
				@Source(value="properties/priority3.properties", locator=ResourceLocator.class),
			}
		) 
		org.apache.commons.configuration.Configuration properties) 
	{
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
	public void testNoMergeResourcesWithDifferentOrder(			
		@Configuration(
			sources={
				@Source(value="properties/priority2.properties", locator=ResourceLocator.class),
				@Source(value="properties/priority1.properties", locator=ResourceLocator.class),
				@Source(value="properties/priority3.properties", locator=ResourceLocator.class),
			}
		) 
		org.apache.commons.configuration.Configuration properties) 
	{
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
	public void testMergedResources(		
		@Configuration(
			sources = {
				@Source(value="properties/priority1.properties", locator=ResourceLocator.class),
				@Source(value="properties/priority2.properties", locator=ResourceLocator.class),
				@Source(value="properties/priority3.properties", locator=ResourceLocator.class),
			},
			merge = true
		) 
		org.apache.commons.configuration.Configuration properties) 
	{
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
	public void testMergeFilesWithSystemPropertiesAndMixedResource(		
		@Configuration(
			sources = {
				@Source(value="${java.io.tmpdir}/priority2.properties", resolve=true),
				@Source(value="${java.io.tmpdir}/priority1.properties", resolve=true),
				@Source(value="properties/priority3.properties", locator=ResourceLocator.class),
			},
			merge = true
		) 
		org.apache.commons.configuration.Configuration properties) 
	{
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
	public void testMergeFilesWithoutSystemPropertiesAndMixedResource(
		@Configuration(
			sources = {
				@Source(value="${java.io.tmpdir}/priority2.properties", resolve=false),
				@Source(value="${java.io.tmpdir}/priority1.properties", resolve=false),
				@Source(value="properties/priority3.properties", locator=ResourceLocator.class),
			},
			merge = true
		) 
		org.apache.commons.configuration.Configuration properties) 
	{
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
