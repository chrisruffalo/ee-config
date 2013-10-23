package com.github.chrisruffalo.eeconfig.strategy.locator;

import javax.enterprise.context.ApplicationScoped;

import com.github.chrisruffalo.eeconfig.annotations.Source;
import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.source.impl.UnfoundSource;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

@ApplicationScoped
public class NullLocator implements ISourceLocator {

	@Override
	public ISource locate(Source source, PropertyResolver resolver) {
		return new UnfoundSource();
	}

}
