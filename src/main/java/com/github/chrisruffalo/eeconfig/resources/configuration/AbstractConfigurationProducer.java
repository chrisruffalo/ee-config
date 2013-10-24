package com.github.chrisruffalo.eeconfig.resources.configuration;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.chrisruffalo.eeconfig.annotations.AutoLogger;
import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.annotations.Property;
import com.github.chrisruffalo.eeconfig.annotations.Resolver;
import com.github.chrisruffalo.eeconfig.annotations.Source;
import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.source.impl.UnfoundSource;
import com.github.chrisruffalo.eeconfig.strategy.locator.Locator;
import com.github.chrisruffalo.eeconfig.strategy.locator.NullLocator;
import com.github.chrisruffalo.eeconfig.strategy.property.DefaultPropertyResolver;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;
import com.github.chrisruffalo.eeconfig.wrapper.ConfigurationWrapper;
import com.github.chrisruffalo.eeconfig.wrapper.ConfigurationWrapperFactory;
import com.github.chrisruffalo.eeconfig.wrapper.ResolverWrapper;

/**
 * Implements shared logic for loading configuration files
 * 
 * @author Chris Ruffalo
 *
 */
public abstract class AbstractConfigurationProducer {
	
	@Inject
	@AutoLogger
	private Logger logger;
	
	@Inject
	private BeanManager manager;
	
	/**
	 * Utility to get {@link Configuration} annotation from the
	 * injection point with minimal effort
	 * 
	 * @param injectionPoint the injection point that was used
	 * 		  to call the producer for satisfaction
	 * 
	 * @return configuration annotation
	 */
	protected ConfigurationWrapper getConfigurationWrapper(InjectionPoint injectionPoint) {
		Configuration configuration = injectionPoint.getAnnotated().getAnnotation(Configuration.class);
		return ConfigurationWrapperFactory.wrap(configuration);
	}
	
	/**
	 * Breaks down the {@link Configuration} annotation into
	 * the segments to look for configuration files
	 * 
	 * @param configuration annotation to use to find configuration files
	 * 
	 * @return List of InputStreams representing the configuration found
	 */
	protected List<ISource> locate(ConfigurationWrapper configuration) {
				
		if(configuration == null) {
			throw new IllegalArgumentException("A non-null Configuration annotation must be provided");
		}
		
		// create resolver from configuration annotation's resolver element
		ResolverWrapper resolverWrapper = configuration.resolver();
		PropertyResolver resolver = this.createPropertyResolver(resolverWrapper);
		Map<String,String> bootstrapMap = this.getBootstrapProperties(resolverWrapper);
		Map<String,String> defaultMap = this.getDefaultProperties(resolverWrapper);
		
		// found sources
		List<ISource> foundSources = new ArrayList<ISource>(0);
		
		// create sources
		List<Source> sources = new ArrayList<Source>(Arrays.asList(configuration.sources()));
		
		// resolve sources as normal
		for(Source source : sources) {
			ISource found = this.resloveSource(source, resolver, bootstrapMap, defaultMap);
			if(found != null) {
				foundSources.add(found);
			}
		}
		
		// fix no sources found, a source SHOULD always be returned
		if(foundSources.isEmpty()) {
			foundSources.add(new UnfoundSource());
		}
		
		return foundSources;
	}
	
	/**
	 * Resolve the property resolver instance to use from the {@link Resolver} 
	 * annotation in the configuration element
	 * 
	 * @param resolverAnnotation
	 * @return
	 */
	private PropertyResolver createPropertyResolver(ResolverWrapper resolverAnnotation) {
		PropertyResolver resolver = null;
		
		// use the impl class to get the property resolver
		Class<? extends PropertyResolver> propertyResolverClass = resolverAnnotation.impl();
		if(propertyResolverClass == null) {
			this.logger.debug("No alternate property resolver provided, using default");
			propertyResolverClass = DefaultPropertyResolver.class;
		} else {
			this.logger.debug("Requesting alternate property resolver: {}", propertyResolverClass.getName());
		}
		resolver = this.resolveBeanWithDefaultClass(propertyResolverClass, DefaultPropertyResolver.class);
		
		return resolver;
	}
	
	/**
	 * Get the properties used to bootstrap the resolver
	 * 
	 * @param resolver
	 * @return
	 */
	private Map<String, String> getBootstrapProperties(ResolverWrapper resolver) {
		return Collections.emptyMap();
	}
	
	/**
	 * Get the properties that should be used as defaults when no other
	 * properties are found for that value
	 * 
	 * @param resolver
	 * @return
	 */
	private Map<String, String> getDefaultProperties(ResolverWrapper resolver) {
		// if no resolver is given or the list of default properties is empty then 
		// return an empty map
		if(resolver == null || resolver.properties() == null || resolver.properties().length == 0) {
			return Collections.emptyMap();
		}
		
		// get properties from the resolver annotation
		Property[] properties = resolver.properties();
		
		// copy each property annotation into the map for later use
		Map<String, String> propertyMap = new HashMap<>(properties.length);
		for(Property property : properties) {
			propertyMap.put(property.key(), property.value());
		}		
		return propertyMap;
	}
	
	/**
	 * Resolve a given source from the provided {@link Source} annotation
	 * 
	 * @param source
	 * @param resolver
	 * @return
	 */
	private ISource resloveSource(Source source, PropertyResolver resolver, Map<String, String> bootstrapMap, Map<String, String> defaultMap) {
		Class<? extends Locator> locatorClass = source.locator();
		if(locatorClass == null) {
			locatorClass = NullLocator.class;
		}
		Locator locator = this.resolveBeanWithDefaultClass(locatorClass, NullLocator.class);
		this.logger.trace("Using locator: '{}'", locator.getClass().getName());
		
		// resolve path if enabled
		String path = source.value();
		if(resolver != null && source.resolve()) {
			path = resolver.resolveProperties(path, bootstrapMap, defaultMap);
		}
		
		// locate
		ISource foundSource = locator.locate(path);
		
		this.logger.trace("Found source: '{}' (using locator '{}')", foundSource, locator.getClass().getName());
		return foundSource;
	}
	
	/**
	 * Resolve managed bean for given type
	 * 
	 * @param typeToResolve
	 * @param defaultType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <B, T extends B, D extends B> B resolveBeanWithDefaultClass(Class<T> typeToResolve, Class<D> defaultType) {

		// if type to resolve is null, do nothing, not even the default
		if(typeToResolve == null) {
			return null;
		}
		
		// get candidate resolve types
		Set<Bean<?>> candidates = this.manager.getBeans(typeToResolve);
		
		// if no candidates are available, resolve
		// using next class up
		if(!candidates.iterator().hasNext()) {
			this.logger.trace("No candidates for: {}", typeToResolve.getName());
			// try and resolve only the default type
			return resolveBeanWithDefaultClass(defaultType, null);
		} 
		
		this.logger.trace("Requesting resolution on: {}", typeToResolve.getName());
		
		// get candidate
		Bean<?> bean = candidates.iterator().next();
		CreationalContext<?> context = this.manager.createCreationalContext(bean);
		Type type = (Type) bean.getTypes().iterator().next();
	    B result = (B)this.manager.getReference(bean, type, context);
		
		this.logger.trace("Resolved to: {}", result.getClass().getName());
		
		return result;
	}
}
