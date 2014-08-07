package com.github.chrisruffalo.eeconfig.wrapper;

import com.github.chrisruffalo.eeconfig.annotations.Bootstrap;
import com.github.chrisruffalo.eeconfig.annotations.EEConfiguration;

/**
 * Create the correct wrapper for {@link Bootstrap} and {@link EEConfiguration}
 * annotations
 * 
 * @author Chris Ruffalo
 *
 */
public final class WrapperFactory {

	// private constructor for factory/static class
	private WrapperFactory() {
		
	}
	
	/**
	 * Wrap a {@link Bootstrap} or {@link EEConfiguration} element
	 * in another class so that they are compatible with the
	 * same methods
	 * 
	 * @param toWrap
	 * @return
	 */
	public static ConfigurationWrapper wrap(Object toWrap) {
		// cannot wrap a null element
		if(toWrap == null) {
			throw new IllegalArgumentException("A null object cannot be wrapped");
		}
		
		// wrap one of the two types
		if(toWrap instanceof Bootstrap) {
			return new BootstrapAnnotationWrapper((Bootstrap)toWrap);
		} else if(toWrap instanceof EEConfiguration) {
			return new ConfigurationAnnotationWrapper((EEConfiguration)toWrap);
		}
		
		// should not get here
		throw new IllegalArgumentException("The object " + toWrap.getClass() + " cannot be wrapped");
	}
	
}
