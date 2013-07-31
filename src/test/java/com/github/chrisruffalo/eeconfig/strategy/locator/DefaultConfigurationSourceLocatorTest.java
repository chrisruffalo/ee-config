package com.github.chrisruffalo.eeconfig.strategy.locator;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.FileConfigurationSource;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.IConfigurationSource;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.ResourceConfigurationSource;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.UnfoundConfigurationSource;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

/**
 * Test the behavior of the default configuration
 * source locator
 * 
 * @author Chris Ruffalo
 *
 */
public class DefaultConfigurationSourceLocatorTest {

	/**
	 * Test empty location configuration
	 * 
	 */
	@Test
	public void testNoLocation() {
		Configuration conf = this.getConfiguration(new String[0]);
		
		DefaultConfigurationSourceLocator locator = new DefaultConfigurationSourceLocator();
		
		List<IConfigurationSource> sources = locator.locate(conf);
		
		Assert.assertEquals(1, sources.size());
		Assert.assertEquals(UnfoundConfigurationSource.class, sources.get(0).getClass());
	}
	
	/**
	 * Test single resource location configuration
	 * 
	 */
	@Test
	public void testSimpleResourceLocation() {
		Configuration conf = this.getConfiguration(new String[]{
			"resource:xml/priority4.xml"
		});
		
		DefaultConfigurationSourceLocator locator = new DefaultConfigurationSourceLocator();
		
		List<IConfigurationSource> sources = locator.locate(conf);
		
		Assert.assertEquals(1, sources.size());
		Assert.assertEquals(ResourceConfigurationSource.class, sources.get(0).getClass());
	}
	
	/**
	 * Test single file location configuration
	 * 
	 * @throws URISyntaxException
	 */
	@Test
	public void testSimpleFileLocation() throws URISyntaxException {
		URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource("yaml/priority6.yaml");
		File file = new File(resourceUrl.toURI());
		
		Configuration conf = this.getConfiguration(new String[]{
			file.getAbsolutePath()
		});
		
		DefaultConfigurationSourceLocator locator = new DefaultConfigurationSourceLocator();
		
		List<IConfigurationSource> sources = locator.locate(conf);
		
		Assert.assertEquals(1, sources.size());
		Assert.assertEquals(FileConfigurationSource.class, sources.get(0).getClass());
	}
	
	/**
	 * Simple method to create a new configuration annotation
	 * for testing.
	 * 
	 * @param paths
	 * @return
	 */
	private Configuration getConfiguration(final String[] paths) {
		Configuration configuration = new Configuration() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return Configuration.class;
			}
			
			@Override
			public boolean resolveSystemProperties() {
				return true;
			}
			
			@Override
			public Class<PropertyResolver> propertyResolver() {
				return null;
			}
			
			@Override
			public String[] paths() {
				return paths;
			}
			
			@Override
			public boolean merge() {
				return false;
			}
			
			@Override
			public Class<ConfigurationSourceLocator> locator() {
				return null;
			}
		};		
		return configuration;
	}
}
