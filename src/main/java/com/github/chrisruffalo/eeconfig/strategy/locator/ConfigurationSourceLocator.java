package com.github.chrisruffalo.eeconfig.strategy.locator;

import java.util.List;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.IConfigurationSource;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

/**
 * Interface for locating configuration sources.
 * 
 * @author Chris Ruffalo
 *
 */
public interface ConfigurationSourceLocator {

	/**
	 * Set the property resolver that will be used when looking
	 * at tokens in the property path.
	 * 
	 * @param propertyResolver
	 */
	void setPropertyResolver(PropertyResolver propertyResolver);
	
	/**
	 * Breaks down the {@link Configuration} annotation into
	 * the segments to look for configuration files.
	 * 
	 * @param configuration annotation to use to find configuration files
	 * 
	 * @return List of InputStreams representing the configuration found
	 */
	List<IConfigurationSource> locate(Configuration configuration);
	
	/**
	 * Takes the inputs of the configuration annotation and uses
	 * them to resolve the location of the configuration file.
	 * 
	 * @param inputPaths list of input paths
	 * @param name of the configuration file
	 * @param resolve should paths be resolved if they contain system properties
	 * 
	 * @return List of InputStreams representing the configuration found
	 */
	List<IConfigurationSource> locate(String[] inputPaths, boolean resolve);
	
}
