package com.github.chrisruffalo.eeconfig.resources.configuration.extensions;

import javax.inject.Singleton;

import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.strategy.locator.Locator;

@Singleton
public class SharedTestSourceLocator implements Locator {

	@Override
	public ISource locate(String path) {
		// return dummy source		
		return new DummySource();
	}
	
}
