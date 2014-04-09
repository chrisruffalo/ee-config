package com.github.chrisruffalo.eeconfig.resources.configuration.extensions;

import java.io.InputStream;

import com.github.chrisruffalo.eeconfig.source.impl.BaseSource;

public class DummySource extends BaseSource {

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
