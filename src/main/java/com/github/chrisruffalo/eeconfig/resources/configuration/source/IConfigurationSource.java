package com.github.chrisruffalo.eeconfig.resources.configuration.source;

import java.io.InputStream;

/**
 * Abstract view of a configuration source.  This allows
 * an implementor to read and view the details of the
 * source without being worried how it was found or how
 * to "open" it's stream.
 * 
 * @author Chris Ruffalo
 *
 */
public interface IConfigurationSource {

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