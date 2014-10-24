package com.github.chrisruffalo.eeconfig.strategy.locator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import com.github.chrisruffalo.eeconfig.annotations.EEFallbackComponent;
import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.source.impl.UnfoundSource;

/**
 * Inspects the path to find the given {@link ISource} element.  This
 * implements the default behavior seen in versions 1.0-1.3 and may
 * be extended to use spi and other method to get "smarter".
 * 
 * @author Chris Ruffalo
 *
 */
@EEFallbackComponent
@ApplicationScoped
public class MultiLocator extends BaseLocator {

	/**
	 * Resource marker
	 */
	private static final String RESOURCE = "resource:";
	
	@Inject
	private FileLocator fLocator;
	
	@Inject
	private ResourceLocator rLocator;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ISource locate(String path) {
		// need some path to work with
		if(path == null || path.isEmpty()) {
			return new UnfoundSource(path);
		}		
		// get a local version of the path for comparing
		final String localPath = path.toLowerCase().trim();
		// has resource marker
		if(localPath.startsWith(MultiLocator.RESOURCE)) {
			path = StringUtils.removeStart(path, MultiLocator.RESOURCE);
			return this.rLocator.locate(path);
		}
		// otherwise use file system lookup
		return this.fLocator.locate(path);
	}

}
