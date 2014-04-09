package com.github.chrisruffalo.eeconfig.source.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Configuration source based on {@link File}
 * 
 * @author Chris Ruffalo
 */
public class FileSource extends BaseSource {

	// file holder/marker
	private File file;
	
	/**
	 * Create a new FileSource from a File
	 * 
	 * @param file
	 */
	public FileSource(File file) {
		this.file = file;
	}
	
	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean available() {
		return file != null && file.exists() && file.isFile();
	}
}
