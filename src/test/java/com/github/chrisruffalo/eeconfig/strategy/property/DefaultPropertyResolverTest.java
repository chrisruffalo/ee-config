package com.github.chrisruffalo.eeconfig.strategy.property;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test default property resolution strategy
 * 
 * @author Chris Ruffalo
 *
 */
public class DefaultPropertyResolverTest {

	/**
	 * Test simple resolution of a system property
	 * 
	 */
	@Test
	public void testSimpleResolution() {
		PropertyResolver resolver = new DefaultPropertyResolver();
		String resolved = resolver.resolveProperties("${java.io.tmpdir}");
		
		String tmpDir = System.getProperty("java.io.tmpdir");
		Assert.assertEquals(tmpDir, resolved);
	}
	
	/**
	 * Test resolving against provided properties
	 * 
	 */
	@Test
	public void testSeededResolution() {
		Map<Object, Object> seeds = new HashMap<Object, Object>();
		seeds.put("token1", "value1");
		seeds.put("token2", "value2");
		
		PropertyResolver resolver = new DefaultPropertyResolver();
		
		String resolved = resolver.resolveProperties("${token1} ${token2}", seeds);
		Assert.assertEquals("value1 value2", resolved);
	}

	/**
	 * Test resolving a string with keys that resolve to other keys
	 * 
	 */
	@Test
	public void testMultiStepResolution() {
		Map<Object, Object> seeds = new HashMap<Object, Object>();
		seeds.put("token1", "${token2}-${token3}");
		seeds.put("token2", "value2");
		seeds.put("token3", "${token4}");
		seeds.put("token4", "value4");
		
		PropertyResolver resolver = new DefaultPropertyResolver();
		
		String resolved = resolver.resolveProperties("${token1} ${token2} | ${{token4}} | ${token4} | ${token5}", seeds);
	
		Assert.assertEquals("value2-value4 value2 | ${{token4}} | value4 | ${token5}", resolved);
	}
	
	/**
	 * Test resolving a key that resolves to itself
	 * 
	 */
	@Test
	public void testRecursiveResolution() {
		Map<Object, Object> seeds = new HashMap<Object, Object>();
		seeds.put("token1", "${token1}");
		
		PropertyResolver resolver = new DefaultPropertyResolver();
		
		String resolved = resolver.resolveProperties("${token1}", seeds);
	
		Assert.assertEquals("${token1}", resolved);
	}
	
	/**
	 * Rest resolving a token that resolves to a chain of
	 * tokens that resolve back to the starting token
	 * 
	 */
	@Test
	public void testCyclicResolution() {
		Map<Object, Object> seeds = new HashMap<Object, Object>();
		seeds.put("token1", "${token2}");
		seeds.put("token2", "${token3}");
		seeds.put("token3", "${token1}");
		
		PropertyResolver resolver = new DefaultPropertyResolver();
		
		String resolved = resolver.resolveProperties("${token1}", seeds);
	
		Assert.assertEquals("${token1}", resolved);
	}
}
