package com.github.chrisruffalo.eeconfig.resources.configuration;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.ConfigurationSource;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.IConfigurationSource;


/**
 * Returns raw configuration source ({@link IConfigurationSource}) resources as loaded
 * from the paths specified in the {@link Configuration} annotation.
 * 
 * @author Chris Ruffalo
 *
 */
@ApplicationScoped
public class RawConfigurationSourceProducer extends AbstractConfigurationProducer {

	/**
	 * Satisfies raw injection for {@link ConfigurationSource} elements
	 * 
	 * @param injectionPoint EE6 injection point
	 * 
	 * @return the raw configuration sources found for the given paths
	 */
	@Produces
	@Configuration(paths={})
	public List<IConfigurationSource> getProperties(InjectionPoint injectionPoint) {
		// get configuration annotation
		Configuration annotation = this.getAnnotation(injectionPoint);
		
		// get input streams
		List<IConfigurationSource> sources = this.locate(annotation);
		
		return sources;
	}	
	
}
