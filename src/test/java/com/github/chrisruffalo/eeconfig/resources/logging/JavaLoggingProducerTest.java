package com.github.chrisruffalo.eeconfig.resources.logging;

import java.util.logging.Logger;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.chrisruffalo.eeconfig.annotations.Bootstrap;
import com.github.chrisruffalo.eeconfig.annotations.Logging;
import com.github.chrisruffalo.eeconfig.annotations.DefaultProperty;
import com.github.chrisruffalo.eeconfig.annotations.Resolver;
import com.github.chrisruffalo.eeconfig.annotations.Source;
import com.github.chrisruffalo.eeconfig.support.DeploymentFactory;

/**
 * Test various injection scenarios with the logging mechanism
 * 
 * @author Chris Ruffalo
 *
 */
@RunWith(Arquillian.class)
public class JavaLoggingProducerTest {

	@Deployment
	public static JavaArchive deployment() {
		JavaArchive archive = DeploymentFactory.createDeployment();
		return archive;
	}

	@Test
	@Inject
	public void testClassInjection(@Logging Logger logger) {
		Assert.assertEquals(this.getClass().getName(), logger.getName());
	}
	
	@Test
	@Inject
	public void testNameInjection(@Logging(name="testLogger") Logger logger) {
		Assert.assertEquals("testLogger", logger.getName());
	}
	
	@Test
	@Inject
	public void testResolvedInjection(
		@Logging(name="${rootLogger}-${local}-${loggerExt}", resolver = 
			@Resolver(
				bootstrap=@Bootstrap(
					sources=@Source("resource:properties/bootstrap.properties")
				),
				properties={
					@DefaultProperty(key="local", value="bridge")
				}
			)
		)			
		Logger logger
	) {
		Assert.assertEquals("wub-bridge-dub", logger.getName());
	}
}
