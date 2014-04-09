package com.github.chrisruffalo.eeconfig.source.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Represents a source that could not be found
 * 
 * @author Chris Ruffalo
 *
 */
public class UnfoundSource extends BaseSource {

	/**
	 * Create an unfound source
	 * 
	 */
	public UnfoundSource() {
		this("");
	}
	
	/**
	 * Create an unfound source with a path string
	 * which would be useful for debugging or checking
	 * what the resource should have been
	 * 
	 * @param path
	 */
	public UnfoundSource(String path) {
		this.setPath(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean available() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream stream() {
		return new ByteArrayInputStream(new byte[0]);
	}

}
