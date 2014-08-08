package com.github.chrisruffalo.eeconfig.source.impl;

import java.io.InputStream;

import com.github.chrisruffalo.eeconfig.mime.SupportedType;
import com.github.chrisruffalo.eeconfig.source.ISource;

/**
 * Common implementation for sources
 * 
 * @author Chris Ruffalo
 *
 */
public abstract class BaseSource implements ISource {

	// the path to the source
	private String path;
	
	// the type if one is provided (defaults to AUTO otherwise)
	private SupportedType type = SupportedType.AUTO;
	
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

	/**
	 * Set the path of the element
	 * 
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	
	/**
	 * {@inheritDoc}
	 */
	public SupportedType type() {
        return type;
    }

	/**
	 * Set the type of the file source
	 * 
	 * @param type
	 */
    public void type(SupportedType type) {
        this.type = type;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [available: " + this.available() + "]";
	}
	
}
