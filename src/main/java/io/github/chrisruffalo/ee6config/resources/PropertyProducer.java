package io.github.chrisruffalo.ee6config.resources;

import io.github.chrisruffalo.ee6config.annotations.SystemProperty;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

/**
 * Injects a system property at the injection point after
 * looking up the desired property from the list
 * of System Properties available to the System.
 * 
 * @author Chris Ruffalo
 *
 */
public class PropertyProducer {

	@Inject
	private Logger logger;

	/**
	 * Produces the System Property Specified by the Injection Point.
	 * 
	 * @param ip
	 *            Injection Point
	 * @return Value of System Property
	 */
	@Produces
	@SystemProperty("")
	public String produceSystemProperty(InjectionPoint ip) {
		String property = ip.getAnnotated().getAnnotation(SystemProperty.class).value();
		String systemValue = System.getProperty(property);

		this.logger.debug("Retrieving System Property: " + property + " with Value: " + systemValue);

		return systemValue;
	}
}
