package io.github.chrisruffalo.ee6config.resources.configuration;

import io.github.chrisruffalo.ee6config.annotations.Configuration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

/**
 * Core shared logic for loading configuration files
 * 
 * @author Chris Ruffalo <cruffalo@redhat.com>
 *
 */
public abstract class AbstractConfigurationProducer {

	/**
	 * Simple magic string for resource
	 * 
	 */
	private static final String RESOURCE = "resource:";

	@Inject
	private Logger logger;

	/**
	 * Mass close of given streams.  Does the best it
	 * can to ensure all the given streams are closed
	 * before continuing.
	 * 
	 * @param streams to close
	 */
	protected void close(Collection<InputStream> streams) {
		for(InputStream stream : streams) {
			try {
				stream.close();
			} catch (IOException e) {
				this.logger.error("Could not close stream: {}", e.getMessage());
			}
		}
	}
	
	/**
	 * Utility to get {@link Configuration} annotation from the
	 * injection point with minimal effort
	 * 
	 * @param injectionPoint the injection point that was used
	 * 		  to call the producer for satisfaction
	 * 
	 * @return configuration annotation
	 */
	protected Configuration getAnnotation(InjectionPoint injectionPoint) {
		Configuration configuration = injectionPoint.getAnnotated().getAnnotation(Configuration.class);
		return configuration;
	}
	
	/**
	 * Breaks down the {@link Configuration} annotation into
	 * the segments to look for configuration files
	 * 
	 * @param configuration annotation to use to find configuration files
	 * 
	 * @return List of InputStreams representing the configuration found
	 */
	protected List<InputStream> locate(Configuration configuration) {
		// safe but shouldn't really ever be able to get here
		if(configuration == null) {
			return emptyResponse();
		}
		
		String[] paths = configuration.paths();
		boolean resolve = configuration.resolveSystemProperties();
		
		List<InputStream> streams = this.locate(paths, resolve);
		
		return streams;
	}
	
	/**
	 * Takes the inputs of the configuration annotation and uses
	 * them to resolve the location of the configuration file.
	 * 
	 * @param inputPaths list of input paths
	 * @param name of the configuration file
	 * @param resolve should paths be resolved if they contain system properties
	 * 
	 * @return List of InputStreams representing the configuration found
	 */
	private List<InputStream> locate(String[] inputPaths, boolean resolve) {
		// return an empty input stream and create a warning
		if(inputPaths == null || inputPaths.length == 0) {
			this.logger.warn("Empty or null configuration deatils passed to configuration location resolver.");
			return this.emptyResponse();
		}		
		
		// get paths
		List<String> paths = new ArrayList<String>(inputPaths.length);
		if(resolve) {
			// if the resolve option is set, resolve
			// each individual path so that if a
			// resolved property is found then it is
			// used
			for(String path : inputPaths) {
				String resolved = this.resolveProperty(path);
				paths.add(resolved);
			}
		} else {
			paths.addAll(Arrays.asList(inputPaths));
		}
		
		// check paths for configuration file 
		// and store into a list in case the
		// merge option is set
		List<InputStream> found = new ArrayList<InputStream>(inputPaths.length);
		for(String path : paths) {
		
			// an empty path is meaningless, as
			// it should point to a file and not
			// some magical empty space
			if(path == null || path.isEmpty()) {
				continue;
			}
			
			// stream that is found
			final InputStream stream;
			
			// resolve according to if it was found
			// as a resource or as a normal path
			if(path.startsWith(AbstractConfigurationProducer.RESOURCE)) {
				stream = this.getConfigurationResourceAtPath(path);
			} else {
				stream = this.getConfigurationAtPath(path);
			}
			
			// if a stream is found then save it
			// in the order it was found
			if(stream != null) {
				found.add(stream);
			}
		}		
		
		// if found is empty, return safe empty result
		if(found.isEmpty()) {
			return this.emptyResponse();
		}
		
		// return unmodifiable list
		return Collections.unmodifiableList(found);
	}
	
	/**
	 * Resolve system properties within the given string
	 * 
	 * @param fullString including ${} tokens
	 * 
	 * @return string with tokens resolved where they exist and have values
	 */
	private String resolveProperty(String fullString) {
		// find tokens
		String[] foundTokens = StringUtils.substringsBetween(fullString, "${", "}");
		
		// output string
		String output = fullString;
		
		// for each token, resolve
		for(String token : foundTokens) {
			// if the token is null, leave
			if(token == null) {
				continue;
			}
			
			// get the property
			String property = System.getProperty(token);
						
			// if the property is null, leave
			if(property == null) {
				continue;
			}
			
			// if the property is the same as the token
			// then leave
			if(token.equals(property)) {
				continue;
			}
			
			// now we have a non-null property that
			// is different than the initial token
			// so now we have something to replace with
			output = StringUtils.replace(output, token, property);
		}
		
		// log
		this.logger.debug("Resolved '{}' to '{}'", fullString, output);
		
		// return resolved output
		return output;
	}
	
	/**
	 * Looks for the given configuration file at the indicated location
	 * and returns an input stream for that file if the file is found.
	 * 
	 * @param path to look at for the given file
	 * 
	 * @return input stream for the given file 
	 */
	private InputStream getConfigurationAtPath(String path) {
		File file = new File(path);
		
		// no file found, return null
		if(!file.exists() || !file.isFile()) {
			return null;
		}
		
		FileInputStream foundFileInputStream;
		try {
			foundFileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			this.logger.error("File found with java.io.File but exception thrown: {}", e.getMessage());
			return null;
		}
		
		return foundFileInputStream;
	}
	
	/**
	 * Returns the input stream, if it exists, for a given resource
	 * 
	 * @param resourcePath the path to the resource with "resource:" prepended
	 * 
	 * @return the InputStream for the resource if it was found
	 */
	private InputStream getConfigurationResourceAtPath(String resourcePath) {
		
		// create path to resource by removing prepended "resource:" if it exists as a prefix
		String resource = StringUtils.removeStart(resourcePath, AbstractConfigurationProducer.RESOURCE);
		
		// get resoruce from context
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
		
		// debug logging
		if(stream == null) {
			this.logger.trace("No resource found at path '{}' for resource", resource);
		} else {
			this.logger.trace("Found resource at path '{}' for resource", resource);
		}
		
		return stream;
	}

	/**
	 * Shared code for an empty response that
	 * is fairly safe to pass around
	 * 
	 * @return a list with one element that contains
	 * 		   an empty input stream
	 */
	private List<InputStream> emptyResponse() {
		List<InputStream> emptyReturn = new ArrayList<InputStream>(1);
		InputStream emptyResult = new ByteArrayInputStream(new byte[0]);
		emptyReturn.add(emptyResult);
		return emptyReturn;
	}
}
