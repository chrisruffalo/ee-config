package com.github.chrisruffalo.eeconfig.resources.configuration;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.chrisruffalo.eeconfig.annotations.AutoLogger;
import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.source.ISource;

/**
 * Produces raw InputStreams for implementing custom configuration
 * without creating your own producer.
 * 
 * @author Chris Ruffalo
 *
 */
@ApplicationScoped
public class InputStreamConfigurationProducer extends AbstractConfigurationProducer {

	@Inject
	@AutoLogger
	private Logger logger;
	
	/**
	 * Returns an input stream, the first one found, for a given {@link Configuration}
	 * 
	 * @param injectionPoint
	 * @return
	 */
	@Produces
	@Configuration
	public InputStream getInputStream(InjectionPoint injectionPoint) {
		// get configuration annotation
		Configuration annotation = this.getAnnotation(injectionPoint);
		
		// get input streams
		List<ISource> sources = this.locate(annotation);
		
		// return first available source
		for(ISource source : sources) {
			if(source.available()) {
				return source.stream();
			}
		}
		
		// otherwise just return the first stream
		return sources.get(0).stream();	
	}
	
	@Produces
	@Configuration
	public List<InputStream> getInputStreams(InjectionPoint injectionPoint) {
		// get configuration annotation
		Configuration annotation = this.getAnnotation(injectionPoint);
		
		// get sources
		List<ISource> sources = this.locate(annotation);
		
		// get input streams
		List<InputStream> streams = new ArrayList<InputStream>(sources.size());
		
		// go through the sources and add them
		for(ISource source : sources) {
			if(!source.available()) {
				this.logger.trace("Source is not available: {}", source.getPath());
				continue;
			}
			this.logger.trace("Source available: {}", source.getPath());
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
