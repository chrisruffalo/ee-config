package com.github.chrisruffalo.eeconfig.resources.logging;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.chrisruffalo.eeconfig.annotations.EELogging;
import com.github.chrisruffalo.eeconfig.annotations.Resolver;
import com.github.chrisruffalo.eeconfig.resources.ResolverFactory;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;
import com.github.chrisruffalo.eeconfig.wrapper.ResolverAnnotationWrapper;
import com.github.chrisruffalo.eeconfig.wrapper.ResolverWrapper;

/**
 * Simple producer that creates loggers to satisfy
 * injection points
 * 
 * @author Chris Ruffalo
 *
 */
@ApplicationScoped
public class SLF4JProducer {

	@Inject
	private ResolverFactory resolverFactory;	
	
	/**
	 * Creates a Logger with using the class name of the injection point.
	 * 
	 * @param injectionPoint Injection Point of the Injection.
	 * @return Logger for the Class
	 */
	@Produces
	@EELogging
	public Logger createLogger(InjectionPoint injectionPoint) {
		// the annotation should not be null as it is a qualifier for this producer
		EELogging annotation = injectionPoint.getAnnotated().getAnnotation(EELogging.class);
		
		// get the logger name
		String name = annotation.name();
		
		// if the name is empty resolve to the class name 
		// that contains the injection point
		if(name == null || name.isEmpty()) {
			return this.createClassLogger(injectionPoint);
		}
		
		// get resolver
		Resolver resolverAnnotation = annotation.resolver();
		ResolverWrapper wrapper = new ResolverAnnotationWrapper(resolverAnnotation);
		PropertyResolver resolver = this.resolverFactory.createPropertyResolver(wrapper);
		Map<Object,Object> bootstrapMap = this.resolverFactory.getBootstrapProperties(wrapper);
		Map<Object,Object> defaultMap = this.resolverFactory.getDefaultProperties(wrapper);
		
		// resolve name
		String resolvedName = resolver.resolveProperties(name, bootstrapMap, defaultMap);
				
		// otherwise use the name to create the logger
		return LoggerFactory.getLogger(resolvedName);
	}
	
	/**
	 * Utility for creating class logger from injection point
	 * 
	 * @param injectionPoint
	 * @return
	 */
	private Logger createClassLogger(InjectionPoint injectionPoint) {
		return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
	}
	
}
