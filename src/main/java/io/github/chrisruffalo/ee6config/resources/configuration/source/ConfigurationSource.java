package io.github.chrisruffalo.ee6config.resources.configuration.source;

import java.io.InputStream;

public abstract class ConfigurationSource implements IConfigurationSource {
	
	private String path;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract boolean available();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract InputStream stream();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() {
		// protect against bad paths
		if(path == null || path.isEmpty()) {
			return "";
		}
		
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getExtension() {
		// some strings you just can't help
		if(this.path == null || this.path.isEmpty()) {
			return "";
		}
		// chop string
		String ext = this.path.substring(this.path.lastIndexOf('.')+1);
		// return
		return ext;
	}
	
}
