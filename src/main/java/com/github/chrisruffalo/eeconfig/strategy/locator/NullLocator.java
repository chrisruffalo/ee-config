package com.github.chrisruffalo.eeconfig.strategy.locator;

import javax.enterprise.context.ApplicationScoped;

import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.source.impl.UnfoundSource;

/**
 * A locator that does nothing but return an {@link UnfoundSource}
 * 
 * @author Chris Ruffalo
 *
 */
@ApplicationScoped
public class NullLocator implements Locator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ISource locate(String path) {
		return new UnfoundSource();
	}

}
