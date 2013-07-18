package io.github.chrisruffalo.ee6config.resources.configuration.source;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class UnfoundConfigurationSource extends ConfigurationSource {

	public UnfoundConfigurationSource() {
		this("");
	}
	
	public UnfoundConfigurationSource(String path) {
		this.setPath(path);
	}

	@Override
	public boolean available() {
		return false;
	}

	@Override
	public InputStream stream() {
		return new ByteArrayInputStream(new byte[0]);
	}

}
