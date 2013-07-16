package io.github.chrisruffalo.ee6config.resources;

import io.github.chrisruffalo.ee6config.annotations.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

/**
 * Resolves the {@link Configuration} annotation for injection
 * into the target project.
 * 
 * @author Chris Ruffalo
 *
 */
public class ProperiesConfigurationProducer extends AbstractConfigurationProducer {
	
	@Inject
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
		Properties properties = new Properties();
		
		// locate configurations
		Configuration configuration = this.getAnnotation(injectionPoint);
		List<InputStream> found = this.locate(configuration);
		
		// different behaviors for merge and unmerged properties
		if(!configuration.merge()) {
			// get top result
			InputStream first = found.get(0);
			try {
				// and load properties from it
				properties.load(first);
			} catch (IOException e) {
				this.logger.error("An error occured while loading configuration properties: {}", e.getMessage());
			}
		} else {
			// when merged the lowest priority should go first
			// since the list comes in the order where the
			// most important properties are found first
			// it needs to be reversed
			List<InputStream> reversed = new ArrayList<InputStream>(found);
			Collections.reverse(reversed);
			
			// load each properties item individually
			for(InputStream stream : reversed) {
				Properties local = new Properties();
				try {
					local.load(stream);
					// and then merge into properties
					properties.putAll(local);
				} catch (IOException e) {
					this.logger.error("An error occured while loading configuration properties: {}", e.getMessage());
				}
			}			
		}
		
		// close streams
		this.close(found);
		
		// return properties
		return properties;
	}	

}
