package com.github.chrisruffalo.eeconfig.resources.configuration.extensions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import com.github.chrisruffalo.eeconfig.annotations.Configuration;
import com.github.chrisruffalo.eeconfig.resources.configuration.source.IConfigurationSource;
import com.github.chrisruffalo.eeconfig.strategy.locator.ConfigurationSourceLocator;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

@Singleton
public class SharedTestConfigurationSourceLocator implements ConfigurationSourceLocator {

	@Override
	public void setPropertyResolver(PropertyResolver propertyResolver) {
		// no-op
	}

	@Override
	public List<IConfigurationSource> locate(Configuration configuration) {
		return this.sources();
	}

	@Override
	public List<IConfigurationSource> locate(String[] inputPaths, boolean resolve) {
		return this.sources();
	}

	private List<IConfigurationSource> sources() {
		List<IConfigurationSource> sources = new ArrayList<IConfigurationSource>(5);
		sources.add(new DummyConfigurationSource());
		sources.add(new DummyConfigurationSource());
		sources.add(new DummyConfigurationSource());
		sources.add(new DummyConfigurationSource());
		sources.add(new DummyConfigurationSource());
		
		return sources;
	}
}
