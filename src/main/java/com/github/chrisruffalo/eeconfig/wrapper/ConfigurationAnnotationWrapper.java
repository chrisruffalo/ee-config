package com.github.chrisruffalo.eeconfig.wrapper;

import com.github.chrisruffalo.eeconfig.annotations.EEConfiguration;
import com.github.chrisruffalo.eeconfig.annotations.Source;

/**
 * Compatibility wrapper for a {@link EEConfiguration} annotation
 * 
 * @author Chris Ruffalo
 *
 */
public class ConfigurationAnnotationWrapper implements ConfigurationWrapper {

	// local delegate
	private EEConfiguration delegate;
	
	/**
	 * Wraps a given {@link EEConfiguration} annotation
	 * 
	 * @param toWrap
	 */
	public ConfigurationAnnotationWrapper(EEConfiguration toWrap) {
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

	/**
	 * {@inheritDoc}
	 */
    @Override
    public boolean log() {
        return this.delegate.log();
    }

}
