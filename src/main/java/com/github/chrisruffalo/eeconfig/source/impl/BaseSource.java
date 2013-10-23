package com.github.chrisruffalo.eeconfig.source.impl;

import java.io.InputStream;

import com.github.chrisruffalo.eeconfig.source.ISource;

public abstract class BaseSource implements ISource {

	private String path;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract boolean available();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract InputStream stream();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() {
		// protect against bad paths
		if(path == null || path.isEmpty()) {
			return "";
		}
		
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getExtension() {
		// some strings you just can't help
		if(this.path == null || this.path.isEmpty()) {
			return "";
		}
		// chop string
		String ext = this.path.substring(this.path.lastIndexOf('.')+1);
		// return
		return ext;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [available: " + this.available() + "]";
	}
	
}
