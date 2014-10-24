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
	public FileSource(final File file) {
	    // store file
		this.file = file;
		
		// store path
		this.setPath(file.getAbsolutePath());
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
			return new ByteArrayInputStream(new byte[0]);
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
		return this.file != null && this.file.exists() && this.file.isFile();
	}
}
