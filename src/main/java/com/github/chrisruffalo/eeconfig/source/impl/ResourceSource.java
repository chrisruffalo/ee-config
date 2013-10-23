package com.github.chrisruffalo.eeconfig.source.impl;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

public class ResourceSource extends BaseSource {

	public ResourceSource(String path) {
		this.setPath(path);
	}
	
	@Override
	public boolean available() {
		return this.getUrl() != null;
	}

	@Override
	public InputStream stream() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream(this.getPath());
		stream = new BufferedInputStream(stream);
		return stream;
	}
	
	public URL getUrl() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(this.getPath());
		return url;
	}

}
