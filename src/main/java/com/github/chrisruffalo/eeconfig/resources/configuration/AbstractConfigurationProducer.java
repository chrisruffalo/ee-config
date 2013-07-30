package com.github.chrisruffalo.eeconfig.resources.configuration;


import java.util.List;

import javax.enterprise.inject.spi.InjectionPoint;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.IConfigurationSource;
import com.github.chrisruffalo.eeconfig.strategy.locator.ConfigurationSourceLocator;
import com.github.chrisruffalo.eeconfig.strategy.locator.DefaultConfigurationSourceLocator;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

/**
 * Implements shared logic for loading configuration files
 * 
 * @author Chris Ruffalo
 *
 */
public abstract class AbstractConfigurationProducer {
	
	/**
	 * Utility to get {@link Configuration} annotation from the
	 * injection point with minimal effort
	 * 
	 * @param injectionPoint the injection point that was used
	 * 		  to call the producer for satisfaction
	 * 
	 * @return configuration annotation
	 */
	protected Configuration getAnnotation(InjectionPoint injectionPoint) {
		Configuration configuration = injectionPoint.getAnnotated().getAnnotation(Configuration.class);
		return configuration;
	}
	
	/**
	 * Breaks down the {@link Configuration} annotation into
	 * the segments to look for configuration files
	 * 
	 * @param configuration annotation to use to find configuration files
	 * 
	 * @return List of InputStreams representing the configuration found
	 */
	protected List<IConfigurationSource> locate(Configuration configuration) {
		
		if(configuration == null) {
			throw new IllegalArgumentException("A non-null Configuration annotation must be provided");
		}
		
		ConfigurationSourceLocator locator = null;
		Class<ConfigurationSourceLocator> sourceLocatorClass = configuration.locator();
		if(sourceLocatorClass != null) {
			try {
				locator = sourceLocatorClass.newInstance();
			} catch (InstantiationException e) {
				// no operation, fall through to default
			} catch (IllegalAccessException e) {
				// no operation, fall through to default
			}
		}
		
		// if no locator is available, use the default
		if(locator == null) {
			locator = new DefaultConfigurationSourceLocator();
		}
		
		PropertyResolver resolver = null;
		Class<PropertyResolver> propertyResolverClass = configuration.propertyResolver();
		if(propertyResolverClass != null) {
			try {
				resolver = propertyResolverClass.newInstance();
			} catch (InstantiationException e) {
				// no operation, fall through to default
			} catch (IllegalAccessException e) {
				// no operation, fall through to default
			}
		}
		
		// if a resolver was created, set it on the locator
		if(resolver != null) {
			locator.setPropertyResolver(resolver);
		}
		
		// locate through the location source
		List<IConfigurationSource> sources = locator.locate(configuration);
		
		return sources;
	}	
}
