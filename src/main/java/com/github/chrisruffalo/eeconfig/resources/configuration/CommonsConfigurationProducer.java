package com.github.chrisruffalo.eeconfig.resources.configuration;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.slf4j.Logger;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.mime.MimeGuesser;
import com.github.chrisruffalo.eeconfig.mime.SupportedType;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.IConfigurationSource;

public class CommonsConfigurationProducer extends AbstractConfigurationProducer {

	@Inject
	private Logger logger;
	
	@Produces
	@Configuration(paths={})
	public org.apache.commons.configuration.Configuration getConfiguration(InjectionPoint injectionPoint) {
		// get configuration annotation
		Configuration annotation = this.getAnnotation(injectionPoint);
		
		// get input streams
		List<IConfigurationSource> sources = this.locate(annotation);
		
		// create configuration combiner
		OverrideCombiner combiner = new OverrideCombiner();
		CombinedConfiguration combined = new CombinedConfiguration(combiner);
		
		// combine configurations
		for(IConfigurationSource source : sources) {
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