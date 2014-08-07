package com.github.chrisruffalo.eeconfig.resources;


import java.nio.file.Path;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.chrisruffalo.eeconfig.annotations.Property;
import com.github.chrisruffalo.eeconfig.support.DeploymentFactory;

/**
 * Test system properties
 * 
 * @author Chris Ruffalo
 *
 */
@RunWith(Arquillian.class)
public class PropertyProducerTest {
	
	@Deployment
	public static JavaArchive deployment() {
		JavaArchive archive = DeploymentFactory.createDeployment();
		return archive;
	}

	/**
	 * Test that a property with an empty key returns empty
	 * 
	 * @param property
	 */
	@Test
	@Inject
	public void testEmptyPropertyName(@Property("") String property) {
		Assert.assertEquals("", property);
	}

	/**
	 * Test that a non-existent property returns an empty value
	 * 
	 * @param property
	 */
	@Test
	@Inject
	public void testNonexistentPropertyName(@Property("${no.property.exists}") String property) {
		Assert.assertEquals("", property);
	}
	
	/**
	 * Test that a property that exists returns a value
	 * 
	 * @param property
	 */
	@Test
	@Inject
	public void testTempDir(@Property("${java.io.tmpdir}") String property) {
		Assert.assertNotNull(property);
		Assert.assertFalse(property.isEmpty());
	}
	
	/**
	 * Test that a property that exists returns a value and it
	 * is not impacted by the setting of the default value
	 * 
	 * @param property
	 */
	@Test
	@Inject
	public void testTempDirWithDefault(@Property(value="${java.io.tmpdir}", defaultValue="dir") String property) {
		Assert.assertNotNull(property);
		Assert.assertFalse(property.isEmpty());
		Assert.assertFalse("dir".equals(property));
	}
	
	/**
     * Test the Path injection from Property
     * 
     * @param path
     */
    @Test
    @Inject
    public void testTmpDirPath(@Property(value="${java.io.tmpdir}") Path path) {
        Assert.assertNotNull(path);
        Assert.assertFalse(path.toString().isEmpty());
        Assert.assertFalse("/".equals(path.toString()));
    }
	
	/**
	 * Test that the default value will come into play if a 
	 * value is not found
	 * 
	 * @param property
	 */
	@Test
	@Inject
	public void testDefaultProperty(@Property(value="${no.property.found}", defaultValue="default") String property) {
		Assert.assertNotNull(property);
		Assert.assertEquals("default", property);
	}
}
