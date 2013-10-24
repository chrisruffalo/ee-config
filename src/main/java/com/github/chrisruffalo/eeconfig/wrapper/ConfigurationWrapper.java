package com.github.chrisruffalo.eeconfig.wrapper;

import com.github.chrisruffalo.eeconfig.annotations.Bootstrap;
import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.annotations.Source;

/**
 * Wraps {@link Bootstrap} and {@link Configuration} elements
 * so that they can be used interchangeably.
 * 
 * @author Chris Ruffalo
 *
 */
public interface ConfigurationWrapper {

	/**
	 * Return the sources() from the configuration-style element
	 * 
	 */
	Source[] sources();
	
	/**
	 * Return the merge() from the configuration-style element
	 * 
	 */
	boolean merge();
	
	/**
	 * Return the resolver() from the configuration-style element
	 * 
	 */
	ResolverWrapper resolver();
	
}
