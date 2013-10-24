package com.github.chrisruffalo.eeconfig.resources;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.chrisruffalo.eeconfig.annotations.AutoLogger;

/**
 * Simple producer that creates loggers to satisfy
 * injection points
 * 
 * @author Chris Ruffalo
 *
 */
public class LoggerProducer {

	/**
	 * Creates a Logger with using the class name of the injection point.
	 * 
	 * @param injectionPoint Injection Point of the Injection.
	 * @return Logger for the Class
	 */
	@Produces
	@AutoLogger
	public Logger createLogger(InjectionPoint injectionPoint) {
		return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
	}
	
}
