package com.github.chrisruffalo.eeconfig.resources.configuration.extensions;

import java.util.Map;

import javax.inject.Singleton;

import com.github.chrisruffalo.eeconfig.strategy.property.DefaultPropertyResolver;

@Singleton
public class SharedTestPropertyResolver extends DefaultPropertyResolver {

	private int count;
	
	@Override
	public String resolveProperties(String fullString, Map<String, String> additionalProperties) {
		this.count++;
		return super.resolveProperties(fullString, additionalProperties);
	}

	public int getCount() {
		return this.count;
	}
}
