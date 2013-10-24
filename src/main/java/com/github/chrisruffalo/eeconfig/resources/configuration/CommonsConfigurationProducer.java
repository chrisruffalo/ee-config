package com.github.chrisruffalo.eeconfig.resources.configuration;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.slf4j.Logger;

import com.github.chrisruffalo.eeconfig.annotations.AutoLogger;
import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.mime.MimeGuesser;
import com.github.chrisruffalo.eeconfig.mime.SupportedType;
import com.github.chrisruffalo.eeconfig.source.ISource;

/**
 * Provides configuration injections to satisfy injection points for various
 * Commons Configuration types
 * 
 * @author Chris Ruffalo
 *
 */
@ApplicationScoped
public class CommonsConfigurationProducer extends AbstractConfigurationProducer {

	@Inject
	@AutoLogger
	private Logger logger;
	
	/**
	 * Given the injection point, resolve an instance of Apache Commons Configuration
	 * 
	 * @param injectionPoint
	 * @return
	 */
	@Produces
	@Configuration
	public org.apache.commons.configuration.Configuration getConfiguration(InjectionPoint injectionPoint) {
		// get configuration annotation
		Configuration annotation = this.getAnnotation(injectionPoint);
		// use shared implementation to get configuration
		return this.getConfiguration(annotation);
	}
	
	/**
	 * Shared implementation that is used to bootstrap other configurations if
	 * requested.
	 * 
	 * @param annotation the annotation to use for configuring
	 * @return the common configuration values
	 */
	public org.apache.commons.configuration.Configuration getConfiguration(Configuration annotation) {
		// get input streams
		List<ISource> sources = this.locate(annotation);
		
		// create configuration combiner
		OverrideCombiner combiner = new OverrideCombiner();
		CombinedConfiguration combined = new CombinedConfiguration(combiner);
		
		// combine configurations
		for(ISource source : sources) {
			// determine mime type in order to create proper commons object
			SupportedType type = MimeGuesser.guess(source);
			
			// if the source isn't available, continue
			if(!source.available()) {
				continue;
			}
			
			InputStream stream = source.stream();
			
			// load properties based on MIME type
			// todo: update for more commons-configuration
			// supported mime types
			if(SupportedType.XML.equals(type)) {
				XMLConfiguration xmlConfiguration = new XMLConfiguration();
				try {
					xmlConfiguration.load(stream);
					combined.addConfiguration(xmlConfiguration);
				} catch(ConfigurationException e) {
					this.logger.error("An error occurred while reading XML properties: {}", e.getMessage());	
				}
			} else {
				PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
				try {
					propertiesConfiguration.load(stream);
					combined.addConfiguration(propertiesConfiguration);
				} catch (ConfigurationException e) {
					this.logger.error("An error occurred while reading properties: {}", e.getMessage());
				}				
			}
			
			// close stream
			try {
				stream.close();
			} catch (IOException e) {
				this.logger.trace("Could not close old stream: {}", stream);
			}
			
			// leave on first loop if merge is on
			if(!annotation.merge()) {
				break;
			}
		}		
		// return configuration
		return combined;
	}
}
