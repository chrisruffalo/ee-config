package com.github.chrisruffalo.eeconfig.wrapper;

import com.github.chrisruffalo.eeconfig.annotations.Bootstrap;
import com.github.chrisruffalo.eeconfig.annotations.Source;

/**
 * Compatibility wrapper for a {@link Bootstrap} annotation
 * 
 * @author Chris Ruffalo
 *
 */
public class BootstrapAnnotationWrapper implements ConfigurationWrapper {

	// local delegate
	private Bootstrap delegate;
	
	/**
	 * Wraps a given {@link Bootstrap} annotation
	 * 
	 * @param toWrap
	 */
	public BootstrapAnnotationWrapper(Bootstrap toWrap) {
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
    public boolean log() {
        return this.delegate.log();
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResolverWrapper resolver() {
		return new BootstrapResolverAnnotationWrapper(this.delegate.resolver());
	}

}
