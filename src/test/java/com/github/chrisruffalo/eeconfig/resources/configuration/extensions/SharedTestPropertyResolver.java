package com.github.chrisruffalo.eeconfig.resources.configuration.extensions;

import java.util.Map;

import javax.inject.Singleton;

import com.github.chrisruffalo.eeconfig.strategy.property.DefaultPropertyResolver;

/**
 * Test resolver implementation
 * 
 * @author Chris Ruffalo
 *
 */
@Singleton
public class SharedTestPropertyResolver extends DefaultPropertyResolver {

	// local count storage
	private int count;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolveProperties(String fullString, Map<Object, Object> bootstrapProperties, Map<Object, Object> defaultProperties) {
		this.count++;
		return super.resolveProperties(fullString, bootstrapProperties, defaultProperties);
	}

	/**
	 * Count number of resolutions for testing purposes
	 * 
	 * @return
	 */
	public int getCount() {
		return this.count;
	}
}
