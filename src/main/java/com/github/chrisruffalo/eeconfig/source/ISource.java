package com.github.chrisruffalo.eeconfig.source;

import java.io.InputStream;

import com.github.chrisruffalo.eeconfig.mime.SupportedType;

/**
 * Interface for a configuration source element
 * 
 * @author Chris Ruffalo
 *
 */
public interface ISource {

	/**
	 * Is the source available/readable.
	 * 
	 * @return false when the source is unreadable or unfound, true otherwise
	 */
	boolean available();
	
	/**
	 * The type of the resource.  The default is AUTO.  Allows
	 * a resource to have its type overridden despite the name/mime
	 * guess of the type.  It also allows non-traditional sources
	 * (like a database or document store, maybe) to return a
	 * constant type without messing with the mime guesser.
	 * 
	 * @return
	 */
	SupportedType type();
	
	/**
	 * Allow the type to be set as it passes through locators and filters
	 * 
	 * @param type
	 */
	void type(SupportedType type);

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
