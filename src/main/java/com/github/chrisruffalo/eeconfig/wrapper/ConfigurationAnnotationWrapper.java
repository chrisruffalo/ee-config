package com.github.chrisruffalo.eeconfig.wrapper;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.annotations.Source;

/**
 * Compatibility wrapper for a {@link Configuration} annotation
 * 
 * @author Chris Ruffalo
 *
 */
public class ConfigurationAnnotationWrapper implements ConfigurationWrapper {

	// local delegate
	private Configuration delegate;
	
	/**
	 * Wraps a given {@link Configuration} annotation
	 * 
	 * @param toWrap
	 */
	public ConfigurationAnnotationWrapper(Configuration toWrap) {
		this.delegate = toWrap;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Source[] sources() {
		return this.delegate.sources();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean merge() {
		return this.delegate.merge();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResolverWrapper resolver() {
		return new ResolverAnnotationWrapper(this.delegate.resolver());
	}

}
