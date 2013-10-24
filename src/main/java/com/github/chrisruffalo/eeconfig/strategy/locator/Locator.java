package com.github.chrisruffalo.eeconfig.strategy.locator;

import com.github.chrisruffalo.eeconfig.source.ISource;

public interface Locator {

	ISource locate(String path);
	
}
