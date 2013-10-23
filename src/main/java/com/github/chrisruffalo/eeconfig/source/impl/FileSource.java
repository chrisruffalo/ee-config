package com.github.chrisruffalo.eeconfig.source.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileSource extends BaseSource {

private File file;
	
	public FileSource(File file) {
		this.file = file;
	}
	
	@Override
	public InputStream stream() {
		InputStream stream;
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			stream = new ByteArrayInputStream(new byte[0]);
		}

		// create buffered stream
		stream = new BufferedInputStream(stream);
		
		return stream;
	}

	@Override
	public boolean available() {
		return file != null && file.exists() && file.isFile();
	}

	public File getFile() {
		return this.file;
	}
}
