package com.github.chrisruffalo.eeconfig.resources.configuration.extensions;

import javax.inject.Singleton;

import com.github.chrisruffalo.eeconfig.annotations.Source;
import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.strategy.locator.ISourceLocator;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

@Singleton
public class SharedTestSourceLocator implements ISourceLocator {

	@Override
	public ISource locate(Source source, PropertyResolver resolver) {
		// do resolve
		resolver.resolveProperties(source.value());
		// return dummy source		
		return new DummySource();
	}
	
}
