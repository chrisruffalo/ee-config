package com.github.chrisruffalo.eeconfig.mime;


import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.chrisruffalo.eeconfig.resources.configuration.source.FileConfigurationSource;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.IConfigurationSource;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;

/**
 * Simple helper for guessing a condensed MIME type.
 * 
 * @author Chris Ruffalo
 *
 */
public class MimeGuesser {

	/**
	 * Guesses the supported type from the given source.  If the source
	 * type cannot be guessed then it returns TEXT.  If the source is
	 * empty it returns TEXT.  If it cannot guess then it returns TEXT.
	 * 
	 * @param source the configuration source to guess the type for
	 * 
	 * @return guessed type from the set of supported types
	 */
	public static SupportedType guess(IConfigurationSource source) {
		// create logger
		Logger logger = LoggerFactory.getLogger(MimeGuesser.class);
		
		// the type is assumed to be text
		SupportedType type = SupportedType.TEXT;
		
		// if no source is available, leave
		if(!source.available()) {
			return type;
		}
		
		// get stream from source
		InputStream stream = source.stream();
		
		// the stream needs to have available 
		// bytes otherwise treat as plain text
		// immediately
		try {
			if(stream.available() <= 0) {
				logger.trace("Blank files will be treated as plain text");
				return type;
			}
		} catch (IOException e1) {
			logger.debug("Error while checking file availability: {}", e1.getMessage());
			return type;
		}

		try {
			// string for mime type
			String typeString = "";
			
			// create content type checker
			ContentInfoUtil util = new ContentInfoUtil();
			
			// mark stream for later reset
			if(stream.markSupported()) {
				stream.mark(Integer.MAX_VALUE);
			}
			
			// find match direct from stream
			ContentInfo info = util.findMatch(stream);
			
			// if no info found from file, look up by file
			if(info == null && source instanceof FileConfigurationSource) {
				FileConfigurationSource fileSource = (FileConfigurationSource)source;
				info = util.findMatch(fileSource.getFile());
			}
			
			// check again using built-in java stream checking
			if(info == null) {
				stream.reset();
				URLConnection.guessContentTypeFromStream(stream);
			}
			
			// check again using java url
			if(info == null) {
				URLConnection.guessContentTypeFromName(source.getPath());
			}
			
			// if info is *still* null, look up by name
			if(info == null) {
				info = ContentInfoUtil.findExtensionMatch(source.getExtension());
			}
			
			// just brute force the string
			if(info == null) {
				typeString = source.getExtension().toLowerCase();
			} else {
				typeString = info.toString().toLowerCase();
			}
 
			logger.trace("Mime type: " + typeString);
						
			// check if the type is xml
			if(typeString.contains("xml")) {
				type = SupportedType.XML;
			} else if(typeString.contains("json")) {
				type = SupportedType.JSON;
			} else if(typeString.contains("yaml")) {
				type = SupportedType.YAML;
			}
		} catch (IOException e) {
			// show an error
			logger.error("Could not get MIME type due to i/o exception: {}", e.getMessage());
		} catch (Exception ex) {
			// if an error happens, type should be text
			logger.error("Could not get MIME due to exception: {}", ex.getMessage());
			type = SupportedType.TEXT;
		} finally {
			// the stream is no longer needed so close it
			try {
				stream.close();
			} catch (IOException e) {
				logger.trace("Could not close stream: {}", e.getMessage());
			}
		}		
		
		// return the type
		return type;		
	}

}
