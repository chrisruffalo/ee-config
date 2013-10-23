package com.github.chrisruffalo.eeconfig.resources.configuration;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.source.ISource;


/**
 * Returns raw configuration source ({@link ISource}) resources as loaded
 * from the paths specified in the {@link Configuration} annotation.
 * 
 * @author Chris Ruffalo
 *
 */
@ApplicationScoped
public class RawConfigurationSourceProducer extends AbstractConfigurationProducer {

	/**
	 * Satisfies raw injection for {@link ISource} elements
	 * 
	 * @param injectionPoint EE6 injection point
	 * 
	 * @return the raw configuration sources found for the given paths
	 */
	@Produces
	@Configuration
	public List<ISource> getProperties(InjectionPoint injectionPoint) {
		// get configuration annotation
		Configuration annotation = this.getAnnotation(injectionPoint);
		
		// get input streams
		List<ISource> sources = this.locate(annotation);
		
		return sources;
	}	
	
}
