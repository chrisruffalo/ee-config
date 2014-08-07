package com.github.chrisruffalo.eeconfig.strategy.locator;

import org.apache.commons.lang.NotImplementedException;

import com.github.chrisruffalo.eeconfig.annotations.EEPlaceholderComponent;
import com.github.chrisruffalo.eeconfig.source.ISource;

/**
 * A locator that is not implemented to provide a marker
 * for the annotation and resolve to nothing
 * 
 * @author Chris Ruffalo
 *
 */
@EEPlaceholderComponent
public class NullLocator implements Locator {
    
    public NullLocator() {
        throw new NotImplementedException("This class should not be constructed.");
    }
    
    /**
	 * {@inheritDoc}
	 */
	@Override
	public ISource locate(String path) {
		return null;
	}

}
