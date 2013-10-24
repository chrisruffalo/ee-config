package com.github.chrisruffalo.eeconfig.source.impl;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Class for representing classpath resource elements
 * 
 * @author Chris Ruffalo
 *
 */
public class ResourceSource extends BaseSource {

	/**
	 * Create a resource from a resource path string
	 * 
	 * @param path
	 */
	public ResourceSource(String path) {
		this.setPath(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean available() {
		return this.getUrl() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream stream() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream(this.getPath());
		stream = new BufferedInputStream(stream);
		return stream;
	}
	
	/**
	 * Get the url to find out if the resource is available
	 * 
	 * @return URL of the resource
	 */
	private URL getUrl() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(this.getPath());
		return url;
	}

}
