package com.github.chrisruffalo.eeconfig.wrapper;

import com.github.chrisruffalo.eeconfig.annotations.Bootstrap;
import com.github.chrisruffalo.eeconfig.annotations.DefaultProperty;
import com.github.chrisruffalo.eeconfig.annotations.Resolver;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

/**
 * Compatibility wrapper for {@link Resolver} annotations
 * 
 * @author Chris Ruffalo
 *
 */
public class ResolverAnnotationWrapper implements ResolverWrapper {

	// local delegate
	private Resolver delegate;
	
	/**
	 * Wraps a given {@link Resolver} annotation
	 * 
	 * @param toWrap
	 */
	public ResolverAnnotationWrapper(Resolver toWrap) {
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
		return this.delegate.bootstrap();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DefaultProperty[] properties() {
		return this.delegate.properties();
	}

}
