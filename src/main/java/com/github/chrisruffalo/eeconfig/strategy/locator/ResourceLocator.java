package com.github.chrisruffalo.eeconfig.strategy.locator;

import javax.enterprise.context.ApplicationScoped;

import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.source.impl.ResourceSource;
import com.github.chrisruffalo.eeconfig.source.impl.UnfoundSource;

@ApplicationScoped
public class ResourceLocator extends BaseLocator {

	@Override
	public ISource locate(String path) {
		if(path == null || path.isEmpty()) {
			return new UnfoundSource();
		}
		
		// get resource
		ResourceSource reSource = new ResourceSource(path);
		
		if(!reSource.available()) {
			return new UnfoundSource(path);
		}
		
		return reSource;
	}

}
