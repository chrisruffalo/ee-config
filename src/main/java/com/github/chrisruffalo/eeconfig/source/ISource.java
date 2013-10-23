package com.github.chrisruffalo.eeconfig.source;

import java.io.InputStream;

public interface ISource {

	/**
	 * Is the source available/readable.
	 * 
	 * @return false when the source is unreadable or unfound, true otherwise
	 */
	boolean available();

	/**
	 * An input stream to the contents of the source
	 * 
	 * @return
	 */
	InputStream stream();

	/**
	 * Full path to the source
	 * 
	 * @return
	 */
	String getPath();

	/**
	 * File extension, if it exists, of the source
	 * 
	 * @return
	 */
	String getExtension();
	
}
