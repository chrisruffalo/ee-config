package com.github.chrisruffalo.eeconfig.resources.configuration;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.IConfigurationSource;

/**
 * Produces raw InputStreams for implementing custom configuration
 * without creating your own producer.
 * 
 * @author Chris Ruffalo
 *
 */
public class InputStreamConfigurationProducer extends AbstractConfigurationProducer {

	@Inject
	private Logger logger;
	
	@Produces
	@Configuration(paths={})
	public InputStream getInputStream(InjectionPoint injectionPoint) {
		// get configuration annotation
		Configuration annotation = this.getAnnotation(injectionPoint);
		
		// get input streams
		List<IConfigurationSource> sources = this.locate(annotation);
		
		// return the first stream
		return sources.get(0).stream();
	}
	
	@Produces
	@Configuration(paths={})
	public List<InputStream> getInputStreams(InjectionPoint injectionPoint) {
		// get configuration annotation
		Configuration annotation = this.getAnnotation(injectionPoint);
		
		// get sources
		List<IConfigurationSource> sources = this.locate(annotation);
		
		// get input streams
		List<InputStream> streams = new ArrayList<InputStream>(sources.size());
		
		// go through the sources and add them
		for(IConfigurationSource source : sources) {
			if(!source.available()) {
				this.logger.info("Source is not available: {}", source.getPath());
				continue;
			}
			this.logger.info("Source available: {}", source.getPath());
			streams.add(source.stream());
		}
		
		// per the contract, if the streams list is empty, add one
		if(streams.isEmpty()) {
			streams.add(new ByteArrayInputStream(new byte[0]));
		}
		
		// return the first stream
		return Collections.unmodifiableList(streams);	
	}
}
