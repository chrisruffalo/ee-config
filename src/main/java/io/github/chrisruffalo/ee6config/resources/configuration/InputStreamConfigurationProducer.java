package io.github.chrisruffalo.ee6config.resources.configuration;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import io.github.chrisruffalo.ee6config.annotations.Configuration;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Produces raw InputStreams for implementing custom configuration
 * without creating your own producer.
 * 
 * @author Chris Ruffalo
 *
 */
public class InputStreamConfigurationProducer extends AbstractConfigurationProducer {

	@Produces
	@Configuration(paths={})
	public InputStream getInputStream(InjectionPoint injectionPoint) {
		// get configuration annotation
		Configuration annotation = this.getAnnotation(injectionPoint);
		
		// get input streams
		List<InputStream> streams = this.locate(annotation);
		
		// return the first stream
		return streams.get(0);
	}
	
	@Produces
	@Configuration(paths={})
	public List<InputStream> getInputStreams(InjectionPoint injectionPoint) {
		// get configuration annotation
		Configuration annotation = this.getAnnotation(injectionPoint);
		
		// get input streams
		List<InputStream> streams = this.locate(annotation);
		
		// return the first stream
		return Collections.unmodifiableList(streams);	
	}
}
