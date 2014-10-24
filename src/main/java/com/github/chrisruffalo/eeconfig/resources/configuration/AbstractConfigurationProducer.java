package com.github.chrisruffalo.eeconfig.resources.configuration;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.chrisruffalo.eeconfig.annotations.EEConfiguration;
import com.github.chrisruffalo.eeconfig.annotations.EELogging;
import com.github.chrisruffalo.eeconfig.annotations.Source;
import com.github.chrisruffalo.eeconfig.resources.BeanResolver;
import com.github.chrisruffalo.eeconfig.resources.ResolverFactory;
import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.source.impl.UnfoundSource;
import com.github.chrisruffalo.eeconfig.strategy.locator.Locator;
import com.github.chrisruffalo.eeconfig.strategy.locator.NullLocator;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;
import com.github.chrisruffalo.eeconfig.wrapper.ConfigurationWrapper;
import com.github.chrisruffalo.eeconfig.wrapper.ResolverWrapper;
import com.github.chrisruffalo.eeconfig.wrapper.WrapperFactory;

/**
 * Implements shared logic for loading configuration files
 * 
 * @author Chris Ruffalo
 *
 */
public abstract class AbstractConfigurationProducer {
    
    public static final String EE_CONFIG_RESOURCE_CHAIN_PROPERTY = "com.github.chrisruffalo.eeconfig.resources.configuration.ResourceChainProperty";
	
	@Inject
	@EELogging
	private Logger logger;
	
	@Inject
	private ResolverFactory resolverFactory;
	
	@Inject
	private BeanResolver beanResolver;
	
	/**
	 * Utility to get {@link EEConfiguration} annotation from the
	 * injection point with minimal effort
	 * 
	 * @param injectionPoint the injection point that was used
	 * 		  to call the producer for satisfaction
	 * 
	 * @return configuration annotation
	 */
	protected ConfigurationWrapper getConfigurationWrapper(InjectionPoint injectionPoint) {
		EEConfiguration configuration = injectionPoint.getAnnotated().getAnnotation(EEConfiguration.class);
		return WrapperFactory.wrap(configuration);
	}
	
	/**
	 * Breaks down the {@link EEConfiguration} annotation into
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
		
		// debug/trace
		this.logger.trace("Creating new resolver wrapper for : {}", configuration.getClass().getName());
		
		// create resolver from configuration annotation's resolver element
		ResolverWrapper resolverWrapper = configuration.resolver();
		PropertyResolver resolver = this.resolverFactory.createPropertyResolver(resolverWrapper);
		Map<Object,Object> bootstrapMap = this.resolverFactory.getBootstrapProperties(resolverWrapper);
		Map<Object,Object> defaultMap = this.resolverFactory.getDefaultProperties(resolverWrapper);
		
		// found sources
		List<ISource> foundSources = new ArrayList<ISource>(0);
		
		// create sources
		List<Source> sources = new ArrayList<Source>(Arrays.asList(configuration.sources()));
		
		// resolve sources as normal
		for(Source source : sources) {
			ISource found = this.resloveSource(source, resolver, bootstrapMap, defaultMap);
			if(found != null) {
				foundSources.add(found);
				if(configuration.log() && this.logger.isInfoEnabled()) {
				    this.logger.info("source '{}' => '{}' (availalble: {}, class={})", source.value(), found.getPath(), found.available(), found.getClass().getName());
				}
			}
		}
		
		// fix no sources found, a source SHOULD always be returned
		if(foundSources.isEmpty()) {
			foundSources.add(new UnfoundSource());
		}
		
		return foundSources;
	}
		
	/**
	 * Resolve a given source from the provided {@link Source} annotation
	 * 
	 * @param source
	 * @param resolver
	 * @return
	 */
	private ISource resloveSource(Source source, PropertyResolver resolver, Map<Object, Object> bootstrapMap, Map<Object, Object> defaultMap) {
		Class<? extends Locator> locatorClass = source.locator();
		if(NullLocator.class.equals(locatorClass)) {
			locatorClass = null;
		}
		final Locator locator = this.beanResolver.resolveBean(Locator.class, locatorClass);
		this.logger.trace("Using locator: '{}'", locator.getClass().getName());
		
		// resolve path if enabled
		String path = source.value();
		if(resolver != null && source.resolve()) {
			path = resolver.resolveProperties(path, bootstrapMap, defaultMap);
		}
				
		// log path
		this.logger.trace("Looking for source at path: '{}'", path);
		
		// locate
		final ISource foundSource = locator.locate(path);
		
		// set mime from provided
		if(foundSource != null) {
		    foundSource.type(source.type());
		}        
		
		// log results
		this.logger.trace("Source: '{}' (using locator '{}')", foundSource, locator.getClass().getName());
		return foundSource;
	}

}
