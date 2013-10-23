package com.github.chrisruffalo.eeconfig.strategy.locator;

import java.io.File;

import javax.enterprise.context.ApplicationScoped;

import com.github.chrisruffalo.eeconfig.annotations.Source;
import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.source.impl.FileSource;
import com.github.chrisruffalo.eeconfig.source.impl.UnfoundSource;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

@ApplicationScoped
public class FileSourceLocator extends BaseLocator {

	@Override
	public ISource locate(Source source, PropertyResolver resolver) {
		String path = this.resolve(source, resolver);
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
