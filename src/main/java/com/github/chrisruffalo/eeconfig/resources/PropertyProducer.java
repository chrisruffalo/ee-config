package com.github.chrisruffalo.eeconfig.resources;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.chrisruffalo.eeconfig.annotations.EELogging;
import com.github.chrisruffalo.eeconfig.annotations.Property;
import com.github.chrisruffalo.eeconfig.annotations.Resolver;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;
import com.github.chrisruffalo.eeconfig.wrapper.ResolverAnnotationWrapper;
import com.github.chrisruffalo.eeconfig.wrapper.ResolverWrapper;

/**
 * Injects a system property at the injection point after
 * looking up the desired property from the list
 * of System Properties available to the System.
 * 
 * @author Chris Ruffalo
 *
 */
@ApplicationScoped
public class PropertyProducer {

	@Inject
	@EELogging
	private Logger logger;
	
	@Inject
	private ResolverFactory resolverFactory;
	
	/**
	 * Produces the System Property Specified by the Injection Point.
	 * 
	 * @param ip
	 *            Injection Point
	 * @return Value of System Property
	 */
	@Produces
	@Property(value="")
	public String produceResolvedProperty(InjectionPoint ip) {
	    return this.resolveProperty(ip);
	}
	
	/**
	 * Produces the path to an item as specified by the injection point
	 * 
	 * @param ip
	 * @return
	 */
	@Produces
	@Property(value="")
	public Path producesPathSpecifiedByProperty(InjectionPoint ip) {
	    String pathString = this.resolveProperty(ip);
	    
	    // get and normalize path
	    Path path = Paths.get(pathString);
	    path = path.normalize();
	    
	    // return path
	    return path;
	}
	
	/**
	 * Internal method for resolving the property as given by the injection point
	 * 
	 * @param ip
	 * @return
	 */
	private String resolveProperty(InjectionPoint ip) {
	    Property annotation = ip.getAnnotated().getAnnotation(Property.class);
    
        String propertyKey = annotation.value();
        String defaultValue = annotation.defaultValue();
        
        // return empty when the key is null or empty
        // this is a safe way of just providing a way 
        // to return something
        if(propertyKey == null || propertyKey.isEmpty()) {
            return defaultValue;
        }
        
        // get resolver
        Resolver resolverAnnotation = annotation.resolver();
        ResolverWrapper wrapper = new ResolverAnnotationWrapper(resolverAnnotation);
        PropertyResolver resolver = this.resolverFactory.createPropertyResolver(wrapper);
        Map<Object,Object> bootstrapMap = this.resolverFactory.getBootstrapProperties(wrapper);
        Map<Object,Object> defaultMap = this.resolverFactory.getDefaultProperties(wrapper);     
        
        this.logger.trace("original default value for '{}' is '{}'", propertyKey, defaultValue);
        
        // to default values add resolved default value
        String resolvedDefault = resolver.resolveProperties(defaultValue, bootstrapMap, defaultMap);
        
        this.logger.trace("default value for '{}' resolved to '{}'", propertyKey, defaultValue);
        
        // resolve value
        String value = resolver.resolveProperties(propertyKey, bootstrapMap, defaultMap);
        
        // if null, empty, or unchanged use default
        if(value == null || value.isEmpty() || propertyKey.equals(value)) {
            value = resolvedDefault;
        }
        
        this.logger.trace("value for '{}' resolved to '{}'", propertyKey, value);
        
        return value;
	}
}
