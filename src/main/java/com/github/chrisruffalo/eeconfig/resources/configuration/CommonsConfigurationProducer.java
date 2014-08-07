package com.github.chrisruffalo.eeconfig.resources.configuration;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.slf4j.Logger;

import com.github.chrisruffalo.eeconfig.annotations.EEConfiguration;
import com.github.chrisruffalo.eeconfig.annotations.EELogging;
import com.github.chrisruffalo.eeconfig.mime.MimeGuesser;
import com.github.chrisruffalo.eeconfig.mime.SupportedType;
import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.wrapper.ConfigurationWrapper;

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
	@EELogging
	private Logger logger;
	
	/**
	 * Given the injection point, resolve an instance of Apache Commons Configuration
	 * 
	 * @param injectionPoint
	 * @return
	 */
	@Produces
	@EEConfiguration
	public org.apache.commons.configuration.Configuration getConfiguration(InjectionPoint injectionPoint) {
		// get configuration annotation
		ConfigurationWrapper annotation = this.getConfigurationWrapper(injectionPoint);
		// use shared implementation to get configuration
		return this.getConfiguration(annotation);
	}
	
	/**
	 * Shared implementation that is used to bootstrap other configurations if
	 * requested.
	 * 
	 * @param wrapper the annotation to use for configuring
	 * @return the common configuration values
	 */
	public org.apache.commons.configuration.Configuration getConfiguration(ConfigurationWrapper wrapper) {
		// get input streams
		List<ISource> sources = this.locate(wrapper);
		
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
				this.loadAndAdd(stream, xmlConfiguration, combined);
			} else if(SupportedType.INI.equals(type)) {
			    HierarchicalINIConfiguration iniConfiguration = new HierarchicalINIConfiguration();
                this.loadAndAdd(stream, iniConfiguration, combined);
            } else {
				PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
				this.loadAndAdd(stream, propertiesConfiguration, combined);				
			}
			
			// close stream
			try {
				stream.close();
			} catch (IOException e) {
				this.logger.trace("Could not close old stream: {}", stream);
			}
			
			// leave on first loop if merge is on
			if(!wrapper.merge()) {
				break;
			}
		}		
		// return configuration
		return combined;
	}
	
	private void loadAndAdd(InputStream toLoad, AbstractConfiguration abstractConfiguration, CombinedConfiguration combined) {
	    if(abstractConfiguration == null) {
	        return;
	    }
	        
        try {
            if(abstractConfiguration instanceof FileConfiguration) {
                ((FileConfiguration)abstractConfiguration).load(toLoad);
            }
            combined.addConfiguration(abstractConfiguration);
        } catch (ConfigurationException e) {
            this.logger.error("An error occurred while reading properties: {}", e.getMessage());
        }       

	}
}
