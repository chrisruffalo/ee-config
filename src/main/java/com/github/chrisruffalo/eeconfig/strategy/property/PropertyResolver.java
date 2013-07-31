package com.github.chrisruffalo.eeconfig.strategy.property;

import java.util.Map;

/**
 * Resolves property tokens inside of strings
 * 
 * @author Chris Ruffalo
 *
 */
public interface PropertyResolver {

	/**
	 * Resolve system properties within the provided string
	 * 
	 * @param fullString including ${} enclosed tokens
	 * 
	 * @return string with tokens resolved where they exist and have values
	 */
	String resolveProperties(String fullString);
	
	/**
	 * Resolve system properties and given properties within the provided string
	 * 
	 * @param fullString including ${} enclosed tokens
	 * @param additionalProperties map of additional properties to use
	 * 
	 * @return string with tokens resolved where they exist and have values
	 */
	String resolveProperties(String fullString, Map<String, String> additionalProperties);
	
}
