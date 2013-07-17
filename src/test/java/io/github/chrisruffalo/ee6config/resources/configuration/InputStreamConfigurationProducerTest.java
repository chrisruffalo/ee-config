package io.github.chrisruffalo.ee6config.resources.configuration;

import io.github.chrisruffalo.ee6config.annotations.Configuration;
import io.github.chrisruffalo.ee6config.support.DeploymentFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InputStreamConfigurationProducerTest {

	@Deployment
	public static WebArchive deployment() {
		WebArchive archive = DeploymentFactory.createDeployment();

		return archive;
	}
	
	/**
	 * Test that no paths produce an empty input stream
	 * 
	 * @param properties
	 * @throws IOException 
	 */
	@Test
	@Inject
	public void testEmptyPaths(@Configuration(paths={}) InputStream stream) throws IOException {
		Assert.assertNotNull(stream);
		Assert.assertEquals(0, stream.available());
	}
	
	/**
	 * Test that non-existent properties produce an empty input stream
	 * 
	 * @param properties
	 * @throws IOException 
	 */
	@Test
	@Inject
	public void testNonexistantPaths(
		@Configuration(
			paths={"resource:no/path/here.properties","/bad/path/file.properties"}
		) 
		InputStream stream
	) throws IOException {
		Assert.assertNotNull(stream);
		Assert.assertEquals(0, stream.available());
	}
	
	/**
	 * Test that a stream can be used to load the same properties
	 * that an injected Properties object would have
	 * 
	 * @param stream
	 * @param properties
	 * @throws IOException 
	 */
	@Test
	@Inject
	public void testStreamCanBeLoaded(
		@Configuration(
			paths={"resource:properties/priority1.properties"}
		) 
		InputStream stream,
		@Configuration(
			paths={"resource:properties/priority1.properties"}
		) 
		Properties properties
	) throws IOException {
		// make sure stream is ok
		Assert.assertNotNull(stream);
		Assert.assertTrue(0 <= stream.available());
		
		// load stream into properties object
		Properties loaded = new Properties();
		loaded.load(stream);
		
		// compare against other injection from same path
		for(Entry<Object,Object> entry : loaded.entrySet()) {
			// get loaded values
			Object key = entry.getKey();
			Object value = entry.getValue();
			
			// get injected property value
			Object target = properties.get(key);
			
			// they should be equal
			Assert.assertEquals(target, value);
		}
	}
	
	/**
	 * Test that multiple input streams can be injected
	 * 
	 * @param streams
	 */
	@Test
	@Inject
	public void loadInputStreams(@Configuration(
		paths = {
			"resource:properties/priority1.properties",
			"resource:properties/priority2.properties",
			"resource:properties/priority3.properties"
		}
	) List<InputStream> streams) {
		Assert.assertNotNull(streams);
		// this one has content
		Assert.assertFalse(streams.isEmpty());
		// and has 3 items
		Assert.assertEquals(3, streams.size());
	}
	
}
