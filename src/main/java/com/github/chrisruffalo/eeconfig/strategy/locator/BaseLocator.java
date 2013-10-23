package com.github.chrisruffalo.eeconfig.strategy.locator;

import com.github.chrisruffalo.eeconfig.annotations.Source;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

public abstract class BaseLocator implements ISourceLocator {

	protected String resolve(Source source, PropertyResolver resolver) {
		String path = source.value();
		if(source.resolve() && resolver != null) {
			path = resolver.resolveProperties(path);
		}
		return path;
	}

}
