package com.github.chrisruffalo.eeconfig.strategy.locator;

import javax.enterprise.context.ApplicationScoped;

import com.github.chrisruffalo.eeconfig.annotations.Source;
import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.source.impl.ResourceSource;
import com.github.chrisruffalo.eeconfig.source.impl.UnfoundSource;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

@ApplicationScoped
public class ResourceLocator extends BaseLocator {

	@Override
	public ISource locate(Source source, PropertyResolver resolver) {
		String resource = this.resolve(source, resolver);
		if(resource == null || resource.isEmpty()) {
			return new UnfoundSource();
		}
		
		// get resource
		ResourceSource reSource = new ResourceSource(resource);
		
		if(!reSource.available()) {
			return new UnfoundSource(resource);
		}
		
		return reSource;
	}

}
