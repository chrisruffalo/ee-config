package com.github.chrisruffalo.eeconfig.resources.configuration.source;

import java.io.InputStream;

public interface IConfigurationSource {

	public abstract boolean available();

	public abstract InputStream stream();

	public abstract String getPath();

	public abstract String getExtension();

}