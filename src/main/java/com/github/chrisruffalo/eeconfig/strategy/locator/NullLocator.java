package com.github.chrisruffalo.eeconfig.strategy.locator;

import javax.enterprise.context.ApplicationScoped;

import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.source.impl.UnfoundSource;

@ApplicationScoped
public class NullLocator implements Locator {

	@Override
	public ISource locate(String path) {
		return new UnfoundSource();
	}

}
