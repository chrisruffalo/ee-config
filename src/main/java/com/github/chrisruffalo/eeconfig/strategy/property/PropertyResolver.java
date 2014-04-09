package com.github.chrisruffalo.eeconfig.strategy.property;

import java.util.Map;

/**
 * Interface for resolving property tokens inside of strings
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
	 * @param bootstrapProperties map of base properties to use
	 * 
	 * @return string with tokens resolved where they exist and have values
	 */
	String resolveProperties(String fullString, Map<Object, Object> bootstrapProperties);
	
	/**
	 * Resolve system properties and given properties within the provided string
	 * 
	 * @param fullString including ${} enclosed tokens
	 * @param bootstrapProperties map of base properties to use
	 * @param defaultProperties map of default properties to use if no value is found given other properties
	 * 
	 * @return string with tokens resolved where they exist and have values
	 */
	String resolveProperties(String fullString, Map<Object, Object> bootstrapProperties, Map<Object, Object> defaultProperties);

	
}
