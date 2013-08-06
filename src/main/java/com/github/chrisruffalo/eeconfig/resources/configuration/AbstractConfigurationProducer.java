package com.github.chrisruffalo.eeconfig.resources.configuration;


import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.IConfigurationSource;
import com.github.chrisruffalo.eeconfig.strategy.locator.ConfigurationSourceLocator;
import com.github.chrisruffalo.eeconfig.strategy.locator.DefaultConfigurationSourceLocator;
import com.github.chrisruffalo.eeconfig.strategy.property.DefaultPropertyResolver;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

/**
 * Implements shared logic for loading configuration files
 * 
 * @author Chris Ruffalo
 *
 */
public abstract class AbstractConfigurationProducer {
	
	@Inject
	private Logger logger;
	
	@Inject
	private BeanManager manager;
	
	@Inject
	private Instance<ConfigurationSourceLocator> locatorInstance;
	
	@Inject
	private Instance<PropertyResolver> resolverInstance;
	
	/**
	 * Utility to get {@link Configuration} annotation from the
	 * injection point with minimal effort
	 * 
	 * @param injectionPoint the injection point that was used
	 * 		  to call the producer for satisfaction
	 * 
	 * @return configuration annotation
	 */
	protected Configuration getAnnotation(InjectionPoint injectionPoint) {
		Configuration configuration = injectionPoint.getAnnotated().getAnnotation(Configuration.class);
		return configuration;
	}
	
	/**
	 * Breaks down the {@link Configuration} annotation into
	 * the segments to look for configuration files
	 * 
	 * @param configuration annotation to use to find configuration files
	 * 
	 * @return List of InputStreams representing the configuration found
	 */
	protected List<IConfigurationSource> locate(Configuration configuration) {
				
		if(configuration == null) {
			throw new IllegalArgumentException("A non-null Configuration annotation must be provided");
		}
		
		ConfigurationSourceLocator locator = null;
		Class<? extends ConfigurationSourceLocator> sourceLocatorClass = configuration.locator();
		if(sourceLocatorClass == null) {
			this.logger.info("No alternate locator provided, using default");
			sourceLocatorClass = DefaultConfigurationSourceLocator.class;
		} else {
			this.logger.info("Requesting alternate locator: {}", sourceLocatorClass.getName());
		} 
		locator = this.resolveBean(sourceLocatorClass, DefaultConfigurationSourceLocator.class);
				
		PropertyResolver resolver = null;
		Class<? extends PropertyResolver> propertyResolverClass = configuration.propertyResolver();
		if(propertyResolverClass == null) {
			this.logger.info("No alternate property resolver provided, using default");
			propertyResolverClass = DefaultPropertyResolver.class;
		} else {
			this.logger.info("Requesting alternate property resolver: {}", propertyResolverClass.getName());
		}
		resolver = this.resolveBean(propertyResolverClass, DefaultPropertyResolver.class);
		locator.setPropertyResolver(resolver);
				
		// locate through the location source
		List<IConfigurationSource> sources = locator.locate(configuration);
		
		return sources;
	}	
	
	/**
	 * Resolve managed bean for given types
	 * 
	 * @param typeToResolve
	 * @param defaultType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <B, T extends B, D extends B> B resolveBean(Class<T> typeToResolve, Class<D> defaultType) {

		// null request leads to null resolution
		if(typeToResolve == null) {
			return null;
		}
		
		Set<Bean<?>> candidates = this.manager.getBeans(typeToResolve);
		
		// if no candidates are available, resolve
		// using next class up
		if(!candidates.iterator().hasNext()) {
			this.logger.info("No candidates for: {}", typeToResolve.getName());
			// try and resolve only the default type
			return resolveBean(defaultType, null);
		} 
		
		this.logger.info("Requesting resolution on: {}", typeToResolve.getName());
		
		// get candidate
		Bean<?> bean = candidates.iterator().next();
		CreationalContext<?> context = this.manager.createCreationalContext(bean);
		Type type = (Type) bean.getTypes().iterator().next();
	    B result = (B)this.manager.getReference(bean, type, context);
		
		this.logger.info("Resolved to: {}", result.getClass().getName());
		
		return result;
	}
}
