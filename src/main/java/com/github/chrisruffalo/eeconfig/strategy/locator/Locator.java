package com.github.chrisruffalo.eeconfig.strategy.locator;

import com.github.chrisruffalo.eeconfig.source.ISource;

/**
 * Describes how {@link ISource}s can be located
 * 
 * @author Chris Ruffalo
 *
 */
public interface Locator {

	/**
	 * Return the {@link ISource} that resides at the given path
	 * 
	 * @param path to the {@link ISource}
	 * @return the {@link ISource} implementation at the path
	 */
	ISource locate(String path);
	
}
