package com.github.chrisruffalo.eeconfig.source.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class UnfoundSource extends BaseSource {

	public UnfoundSource() {
		this("");
	}
	
	public UnfoundSource(String path) {
		this.setPath(path);
	}

	@Override
	public boolean available() {
		return false;
	}

	@Override
	public InputStream stream() {
		return new ByteArrayInputStream(new byte[0]);
	}

}
