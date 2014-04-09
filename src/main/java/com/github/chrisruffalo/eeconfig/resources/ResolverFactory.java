package com.github.chrisruffalo.eeconfig.resources;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.configuration.ConfigurationMap;
import org.slf4j.Logger;

import com.github.chrisruffalo.eeconfig.annotations.Logging;
import com.github.chrisruffalo.eeconfig.annotations.DefaultProperty;
import com.github.chrisruffalo.eeconfig.annotations.Resolver;
import com.github.chrisruffalo.eeconfig.resources.configuration.CommonsConfigurationProducer;
import com.github.chrisruffalo.eeconfig.strategy.property.DefaultPropertyResolver;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;
import com.github.chrisruffalo.eeconfig.wrapper.ConfigurationWrapper;
import com.github.chrisruffalo.eeconfig.wrapper.WrapperFactory;
import com.github.chrisruffalo.eeconfig.wrapper.ResolverWrapper;

@ApplicationScoped
public class ResolverFactory {

	@Inject
	@Logging
	private Logger logger;
	
	@Inject
	private Instance<CommonsConfigurationProducer> producer;
	
	@Inject
	private BeanResolver beanResolver;
	
	/**
	 * Resolve the property resolver instance to use from the {@link Resolver} 
	 * annotation in the configuration element
	 * 
	 * @param resolverAnnotation
	 * @return
	 */
	public PropertyResolver createPropertyResolver(ResolverWrapper resolverAnnotation) {
		PropertyResolver resolver = null;
		
		// use the impl class to get the property resolver
		Class<? extends PropertyResolver> propertyResolverClass = resolverAnnotation.impl();
		if(propertyResolverClass == null) {
			this.logger.debug("No alternate property resolver provided, using default");
			propertyResolverClass = DefaultPropertyResolver.class;
		} else {
			this.logger.debug("Requesting alternate property resolver: {}", propertyResolverClass.getName());
		}
		resolver = this.beanResolver.resolveBeanWithDefaultClass(propertyResolverClass, DefaultPropertyResolver.class);
		
		return resolver;
	}
	
	/**
	 * Get the properties used to bootstrap the resolver
	 * 
	 * @param resolver
	 * @return
	 */
	public Map<Object, Object> getBootstrapProperties(ResolverWrapper resolver) {
		// return empty map
		if(resolver.bootstrap() == null) {
			return Collections.emptyMap();	
		}
		
		// get configuration wrapper
		ConfigurationWrapper wrapper = WrapperFactory.wrap(resolver.bootstrap());
		
		// bail early if nothing to do
		if(wrapper.sources() == null || wrapper.sources().length == 0) {
			return Collections.emptyMap();
		}
		
		// obtain instance of CommonsConfiguration provider
		CommonsConfigurationProducer instance = this.producer.get();
		
		// get commons configuration object from bootstrap
		org.apache.commons.configuration.Configuration config = instance.getConfiguration(wrapper);
		
		// wrap as map and return
		ConfigurationMap map = new ConfigurationMap(config);
		return map;
	}
	
	/**
	 * Get the properties that should be used as defaults when no other
	 * properties are found for that value
	 * 
	 * @param resolver
	 * @return
	 */
	public Map<Object, Object> getDefaultProperties(ResolverWrapper resolver) {
		// if no resolver is given or the list of default properties is empty then 
		// return an empty map
		if(resolver == null || resolver.properties() == null || resolver.properties().length == 0) {
			return Collections.emptyMap();
		}
		
		// get properties from the resolver annotation
		DefaultProperty[] properties = resolver.properties();
		
		// copy each property annotation into the map for later use
		Map<Object, Object> propertyMap = new HashMap<>(properties.length);
		for(DefaultProperty property : properties) {
			// nulls and empty keys are bad
			if(property.key() == null || property.key().isEmpty() || property.value() == null) {
				continue;
			}
			// no dupes
			if(propertyMap.containsKey(property.key())) {
				continue;
			}
			// put in map
			propertyMap.put(property.key(), property.value());
		}		
		return propertyMap;
	}
	
}
