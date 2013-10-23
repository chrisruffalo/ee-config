package com.github.chrisruffalo.eeconfig.strategy.locator;

import com.github.chrisruffalo.eeconfig.annotations.Source;
import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

public interface ISourceLocator {

	ISource locate(Source source, PropertyResolver resolver);
	
}
