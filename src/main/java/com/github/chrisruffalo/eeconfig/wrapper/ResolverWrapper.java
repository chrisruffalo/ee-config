package com.github.chrisruffalo.eeconfig.wrapper;

import com.github.chrisruffalo.eeconfig.annotations.Bootstrap;
import com.github.chrisruffalo.eeconfig.annotations.BootstrapResolver;
import com.github.chrisruffalo.eeconfig.annotations.Property;
import com.github.chrisruffalo.eeconfig.annotations.Resolver;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

/**
 * Interface for wrapping {@link Resolver} and {@link BootstrapResolver} so
 * that they are compatible with one another
 * 
 * @author Chris Ruffalo
 *
 */
public interface ResolverWrapper {

	/**
	 * return impl() on the wrapped instance
	 * 
	 */
	Class<? extends PropertyResolver> impl();
	
	/**
	 * return properties() on the wrapped instance
	 * 
	 */
	Bootstrap bootstrap();
	
	/**
	 * return properties() on the wrapped instance
	 * 
	 */
	Property[] properties();
}
