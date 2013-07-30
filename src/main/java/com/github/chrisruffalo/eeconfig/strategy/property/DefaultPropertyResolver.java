package com.github.chrisruffalo.eeconfig.strategy.property;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPropertyResolver implements PropertyResolver {

	private Logger logger;

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
	public String resolveProperties(String fullString, Map<String, String> additionalProperties) {
		// find tokens
		String[] foundTokens = StringUtils.substringsBetween(fullString, "${", "}");

		// if no tokens are found, leave
		if(foundTokens == null || foundTokens.length == 0) {
			return fullString;
		}

		// output string manipulation
		String output = fullString;

		// for each token, resolve
		for(String token : foundTokens) {
			// if the token is null, leave
			if(token == null) {
				continue;
			}

			// get the property
			final String property;
			if(additionalProperties != null && additionalProperties.containsKey(token)) {
				property = additionalProperties.get(token);
			} else {
				property = System.getProperty(token);
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

		// recurse to re-resolve any properties
		output = this.resolveProperties(output, additionalProperties);

		// return resolved output
		return output;
	}

}
