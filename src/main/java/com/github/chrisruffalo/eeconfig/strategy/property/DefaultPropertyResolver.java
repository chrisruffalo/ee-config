package com.github.chrisruffalo.eeconfig.strategy.property;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the property resolver that can
 * resolve properties by using a given property map combined
 * with given System Properties
 * 
 * @author Chris Ruffalo
 *
 */
@Default
@ApplicationScoped
public class DefaultPropertyResolver implements PropertyResolver {
	
	// not injected so we can write some "normal" unit tests based around it
	private Logger logger;

	/**
	 * Create the default resolver
	 * 
	 */
	public DefaultPropertyResolver() {
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolveProperties(String fullString) {
		return resolveProperties(fullString, new HashMap<String, String>(0));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolveProperties(String fullString, Map<String, String> bootstrapProperties) {
		return resolveProperties(fullString, bootstrapProperties, new HashMap<String, String>(0));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolveProperties(String fullString, Map<String, String> bootstrapProperties, Map<String, String> defaultProperties) {
		
		// prevent loops
		Set<String> previousValues = new HashSet<String>();
		
		// string for manipulation
		String output = fullString;
		
		// initial value is a 'previous value'
		previousValues.add(output);
		
		// loop until the resolved string is 'stable'.  in previous
		// versions this was a recursive step but recursion was
		// dropped for two reasons.  the first is that it complicates
		// the 'previous value' resolution that stops cyclic
		// and recursive resolution.  the second is becuase Java
		// doesn't really gain anything from a recursive loop.
		while(true) {
			// find tokens
			String[] foundTokens = StringUtils.substringsBetween(output, "${", "}");
	
			// if no tokens are found, leave
			if(foundTokens == null || foundTokens.length == 0) {
				break;
			}
	
			// for each token, resolve
			for(String token : foundTokens) {
				// if the token is null, leave
				if(token == null) {
					continue;
				}
	
				// get the property (first from bootstrap, then from system properties, then from default)
				String property = null;
				if(bootstrapProperties != null && bootstrapProperties.containsKey(token)) {
					property = bootstrapProperties.get(token);
				} else if(System.getProperties().containsKey(token)) {
					property = System.getProperty(token);
				} else if(defaultProperties != null && defaultProperties.containsKey(token)) {
					property = defaultProperties.get(token);
				}
	
				// if the property is null, leave
				if(property == null) {
					continue;
				}
	
				// if the property is the same as the token
				// then leave
				if(token.equals(property)) {
					continue;
				}
	
				// now we have a non-null property that
				// is different than the initial token
				// so now we have something to replace with
				output = StringUtils.replace(output, "${" + token + "}", property);
			}
	
			// log
			this.logger.trace("Resolved '{}' to '{}'", fullString, output);
			
			// if the previous values list contains an earlier version of the string
			// we can consider it safe to return because it's either recursive or
			// cyclic
			if(previousValues.contains(output)) {
				this.logger.warn("Cyclic or recursive property resolution found for '{}', done resolving properties", output);
				break;
			}
			
			// store this step's value as a 'previous value'
			previousValues.add(output);
		}

		// return resolved output
		return output;
	}

}
