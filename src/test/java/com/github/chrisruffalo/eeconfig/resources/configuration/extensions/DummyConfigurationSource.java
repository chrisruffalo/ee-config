package com.github.chrisruffalo.eeconfig.resources.configuration.extensions;

import java.io.InputStream;

import com.github.chrisruffalo.eeconfig.resources.configuration.source.IConfigurationSource;

public class DummyConfigurationSource implements IConfigurationSource {

	@Override
	public boolean available() {
		return false;
	}

	@Override
	public InputStream stream() {
		return null;
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public String getExtension() {
		return null;
	}

}
