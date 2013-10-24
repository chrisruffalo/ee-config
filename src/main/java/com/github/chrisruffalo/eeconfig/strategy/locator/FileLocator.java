package com.github.chrisruffalo.eeconfig.strategy.locator;

import java.io.File;

import javax.enterprise.context.ApplicationScoped;

import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.source.impl.FileSource;
import com.github.chrisruffalo.eeconfig.source.impl.UnfoundSource;

/**
 * Locate a file source from the path given
 * 
 * @author Chris Ruffalo
 *
 */
@ApplicationScoped
public class FileLocator extends BaseLocator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ISource locate(String path) {
		if(path == null || path.isEmpty()) {
			return new UnfoundSource();
		}
		// create file pointer from given path
		File file = new File(path);
		FileSource fileSource = new FileSource(file);
		if(!fileSource.available()) {
			return new UnfoundSource(path);
		}
		return fileSource; 
	}

}
