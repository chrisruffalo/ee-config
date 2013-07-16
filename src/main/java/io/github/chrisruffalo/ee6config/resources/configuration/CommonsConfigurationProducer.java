package io.github.chrisruffalo.ee6config.resources.configuration;

import io.github.chrisruffalo.ee6config.annotations.Configuration;

import java.io.InputStream;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.slf4j.Logger;

public class CommonsConfigurationProducer extends AbstractConfigurationProducer {

	@Inject
	private Logger logger;
	
	@Produces
	@Configuration(paths={})
	public org.apache.commons.configuration.Configuration getConfiguration(InjectionPoint injectionPoint) {
		// get configuration annotation
		Configuration annotation = this.getAnnotation(injectionPoint);
		
		// get input streams
		List<InputStream> streams = this.locate(annotation);
		
		// create configuration combiner
		OverrideCombiner combiner = new OverrideCombiner();
		CombinedConfiguration combined = new CombinedConfiguration(combiner);
		
		// combine configurations
		for(InputStream stream : streams) {
			PropertiesConfiguration local = new PropertiesConfiguration();
			try {
				local.load(stream);
				combined.addConfiguration(local);
			} catch (ConfigurationException e) {
				this.logger.info("An error occurred while reading properties: {}", e.getMessage());
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
