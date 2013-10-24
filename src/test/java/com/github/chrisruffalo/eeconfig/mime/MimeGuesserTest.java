package com.github.chrisruffalo.eeconfig.mime;


import org.junit.Assert;
import org.junit.Test;

import com.github.chrisruffalo.eeconfig.source.ISource;
import com.github.chrisruffalo.eeconfig.source.impl.ResourceSource;
import com.github.chrisruffalo.eeconfig.source.impl.UnfoundSource;

/**
 * Test for 'guessing' MIME type of supported files against the
 * various {@link ISource} types
 * 
 * @author Chris Ruffalo
 *
 */
public class MimeGuesserTest {

	@Test
	public void testUnfoundSource() {
		UnfoundSource source = new UnfoundSource();
		SupportedType type = MimeGuesser.guess(source);
		Assert.assertEquals(SupportedType.TEXT, type);
	}
	
	@Test
	public void testPropertiesFile() {
		ResourceSource source = new ResourceSource("properties/priority1.properties");
		SupportedType type = MimeGuesser.guess(source);
		Assert.assertEquals(SupportedType.TEXT, type);
	}

	@Test
	public void testXmlFile() {
		ResourceSource source = new ResourceSource("xml/priority4.xml");
		SupportedType type = MimeGuesser.guess(source);
		Assert.assertEquals(SupportedType.XML, type);
	}
	
	@Test
	public void testYamlFile() {
		ResourceSource source = new ResourceSource("yaml/priority6.yaml");
		SupportedType type = MimeGuesser.guess(source);
		Assert.assertEquals(SupportedType.YAML, type);
	}
	
	@Test
	public void testJsonFile() {
		ResourceSource source = new ResourceSource("json/priority7.json");
		SupportedType type = MimeGuesser.guess(source);
		Assert.assertEquals(SupportedType.JSON, type);	
	}
	
}

