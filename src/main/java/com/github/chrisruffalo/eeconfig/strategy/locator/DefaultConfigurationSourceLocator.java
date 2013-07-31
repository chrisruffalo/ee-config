package com.github.chrisruffalo.eeconfig.strategy.locator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.FileConfigurationSource;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.IConfigurationSource;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.ResourceConfigurationSource;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.UnfoundConfigurationSource;
import com.github.chrisruffalo.eeconfig.strategy.property.DefaultPropertyResolver;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

/**
 * Default implementation of the strategy for locating configuration sources
 * 
 * @author Chris Ruffalo
 *
 */
public class DefaultConfigurationSourceLocator implements ConfigurationSourceLocator {
	
	/**
	 * Simple magic string for resource
	 * 
	 */
	private static final String RESOURCE = "resource:";
	
	private Logger logger;
	
	private PropertyResolver resolver;
	
	/**
	 * Create default instance of configuration source locator
	 * 
	 */
	public DefaultConfigurationSourceLocator() {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.resolver = new DefaultPropertyResolver();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IConfigurationSource> locate(Configuration configuration) {
		// safe but shouldn't really ever be able to get here
		if(configuration == null) {
			return emptyResponse();
		}
		
		String[] paths = configuration.paths();
		boolean resolve = configuration.resolveSystemProperties();
		
		List<IConfigurationSource> sources = this.locate(paths, resolve);
		
		return sources;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IConfigurationSource> locate(String[] inputPaths, boolean resolve) {
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
				String resolved = this.resolver.resolveProperties(path);
				paths.add(resolved);
			}
		} else {
			paths.addAll(Arrays.asList(inputPaths));
		}
		
		// check paths for configuration file 
		// and store into a list in case the
		// merge option is set
		List<IConfigurationSource> found = new ArrayList<IConfigurationSource>(inputPaths.length);
		for(String path : paths) {
		
			// an empty path is meaningless, as
			// it should point to a file and not
			// some magical empty space
			if(path == null || path.isEmpty()) {
				continue;
			}
			
			// stream that is found
			final IConfigurationSource source;
			
			// resolve according to if it was found
			// as a resource or as a normal path
			if(path.startsWith(DefaultConfigurationSourceLocator.RESOURCE)) {
				source = this.getConfigurationResourceAtPath(path);
			} else {
				source = this.getConfigurationAtPath(path);
			}
			
			// if a stream is found then save it
			// in the order it was found
			if(source != null) {
				found.add(source);
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
	 * Shared code for an empty response that
	 * is fairly safe to pass around
	 * 
	 * @return a list with one element that contains
	 * 		   an empty input stream
	 */
	private List<IConfigurationSource> emptyResponse() {
		List<IConfigurationSource> emptyReturn = new ArrayList<IConfigurationSource>(1);
		emptyReturn.add(new UnfoundConfigurationSource());
		return emptyReturn;
	}
	
	/**
	 * Looks for the given configuration file at the indicated location
	 * and returns an input stream for that file if the file is found.
	 * 
	 * @param path to look at for the given file
	 * 
	 * @return input stream for the given file 
	 */
	private IConfigurationSource getConfigurationAtPath(String path) {
		// create file pointer from given path
		File file = new File(path);
		FileConfigurationSource source = new FileConfigurationSource(file);
		if(!source.available()) {
			return new UnfoundConfigurationSource(path);
		}
		return source;
	}
	
	/**
	 * Returns the input stream, if it exists, for a given resource
	 * 
	 * @param resourcePath the path to the resource with "resource:" prepended
	 * 
	 * @return the InputStream for the resource if it was found
	 */
	private IConfigurationSource getConfigurationResourceAtPath(String resourcePath) {
		
		// create path to resource by removing prepended "resource:" if it exists as a prefix
		String resource = StringUtils.removeStart(resourcePath, DefaultConfigurationSourceLocator.RESOURCE);
		
		// get resource
		ResourceConfigurationSource source = new ResourceConfigurationSource(resource);
		
		if(!source.available()) {
			return new UnfoundConfigurationSource(resource);
		}
		
		return source;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPropertyResolver(PropertyResolver propertyResolver) {
		// do not override the current resolver (which starts out
		// as the default resolver) with a null one
		if(propertyResolver == null) {
			return;
		}
		
		this.resolver = propertyResolver;
	}

}
