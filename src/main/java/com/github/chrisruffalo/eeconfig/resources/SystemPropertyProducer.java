package com.github.chrisruffalo.eeconfig.resources;


import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import com.github.chrisruffalo.eeconfig.annotations.SystemProperty;

/**
 * Injects a system property at the injection point after
 * looking up the desired property from the list
 * of System Properties available to the System.
 * 
 * @author Chris Ruffalo
 *
 */
public class SystemPropertyProducer {

	//@Inject
	//@AutoLogger
	//private Logger logger;

	/**
	 * Produces the System Property Specified by the Injection Point.
	 * 
	 * @param ip
	 *            Injection Point
	 * @return Value of System Property
	 */
	@Produces
	@SystemProperty(key="")
	public String produceSystemProperty(InjectionPoint ip) {
		SystemProperty annotation = ip.getAnnotated().getAnnotation(SystemProperty.class);
		
		String propertyKey = annotation.key();
		String defaultValue = annotation.defaultValue();
		
		// return empty when the key is null or empty
		// this is a safe way of just providing a way 
		// to return something
		if(propertyKey == null || propertyKey.isEmpty()) {
			return defaultValue;
		}
		
		// if the property does not exist just return the 
		// default value
		if(!System.getProperties().containsKey(propertyKey)) {
			return defaultValue;
		}
		
		// otherwise get the value
		String systemValue = System.getProperty(propertyKey);

		// and return it
		return systemValue;
	}
}
