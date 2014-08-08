package com.github.chrisruffalo.eeconfig.mime;

/**
 * Supported configuration file types
 * 
 * @author Chris Ruffalo
 * 
 */
public enum SupportedType {
    // default value, causes auto-guess
    AUTO,
    
    // actual implemented types
	XML,
	TEXT,
	YAML,
	JSON,
	INI
	;
}
