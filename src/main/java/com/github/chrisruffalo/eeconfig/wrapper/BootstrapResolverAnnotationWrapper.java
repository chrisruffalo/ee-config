package com.github.chrisruffalo.eeconfig.wrapper;

import com.github.chrisruffalo.eeconfig.annotations.Bootstrap;
import com.github.chrisruffalo.eeconfig.annotations.BootstrapResolver;
import com.github.chrisruffalo.eeconfig.annotations.DefaultProperty;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

/**
 * Compatibility wrapper for {@link BootstrapResolver} annotations
 * 
 * @author Chris Ruffalo
 *
 */
public class BootstrapResolverAnnotationWrapper implements ResolverWrapper {

	// local delegate
	private BootstrapResolver delegate;
	
	/**
	 * Wraps a given {@link BootstrapResolver} annotation
	 * 
	 * @param toWrap
	 */
	public BootstrapResolverAnnotationWrapper(BootstrapResolver toWrap) {
		this.delegate = toWrap;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<? extends PropertyResolver> impl() {
		return this.delegate.impl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bootstrap bootstrap() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DefaultProperty[] properties() {
		return this.delegate.properties();
	}

}
