package com.github.chrisruffalo.eeconfig.resources.configuration;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.chrisruffalo.eeconfig.annotations.AutoLogger;
import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.mime.MimeGuesser;
import com.github.chrisruffalo.eeconfig.mime.SupportedType;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.IConfigurationSource;

/**
 * Resolves the {@link Configuration} annotation for injection
 * into the target project.
 * 
 * @author Chris Ruffalo
 *
 */
@ApplicationScoped
public class PropertiesConfigurationProducer extends AbstractConfigurationProducer {
	
	@Inject
	@AutoLogger
	private Logger logger;
	
	/**
	 * Satisfies injection for java.util.Properties
	 * 
	 * @param injectionPoint EE6 injection point
	 * 
	 * @return the java.util.Properties loaded from the 
	 * 		   configuration files (if found)
	 */
	@Produces
	@Configuration(paths={})
	public Properties getProperties(InjectionPoint injectionPoint) {
		// properties should be stored here
		Properties properties = new Properties();
		
		// locate configurations
		Configuration configuration = this.getAnnotation(injectionPoint);
		List<IConfigurationSource> found = this.locate(configuration);
		
		// input stream list is immutable, copy so we can reverse
		// if it needs merge
		List<IConfigurationSource> copy = new ArrayList<IConfigurationSource>(found);
		
		// when merged the lowest priority should go first
		// since the list comes in the order where the
		// most important properties are found first
		// it needs to be reversed
		if(configuration.merge()) {
			Collections.reverse(copy);
		}
		
		// show how many streams were located
		this.logger.trace("Found {} streams to load properties from", copy.size());
		
		// load each properties item individually
		for(IConfigurationSource source : copy) {
			// get type for stream
			SupportedType type = MimeGuesser.guess(source);
			
			// if stream is not available, continue
			if(!source.available()) {
				continue;
			}
			
			// get stream
			InputStream stream = source.stream();
			
			// load properties
			Properties local = new Properties();
			try {
				
				// support XML as a type
				if(SupportedType.XML.equals(type)) {
					local.loadFromXML(stream);
				} else {
					local.load(stream);
				}
				
				// log
				this.logger.trace("Loaded {} properties from stream type '{}'", local.size(), type.name());
				
				// and then merge into properties
				properties.putAll(local);
			} catch (IOException e) {
				this.logger.error("An error occured while loading configuration properties: {}", e.getMessage());
			}
			
			// close stream
			try {
				stream.close();
			} catch (IOException e) {
				this.logger.trace("Could not close old stream: {}", stream);
			}		
			
			// if not merge, then we're done
			if(!configuration.merge()) {
				break;
			}
		}			
		
		// return properties
		return properties;
	}	

}
