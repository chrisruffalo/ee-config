package com.github.chrisruffalo.eeconfig.support;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DeploymentFactory {

	/**
	 * Private constructor for utility class
	 */
	private DeploymentFactory() {
		
	}
	
	public static WebArchive createDeployment() {
		// create logger
		Logger logger = LoggerFactory.getLogger(DeploymentFactory.class);
		
		// open test properties
		Properties properties = new Properties();
		try {
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties"));
		} catch (IOException e) {
			logger.error("Test properties not loaded: {}", e.getMessage());
		}
		
		// create archive
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "ee-config-test-" + UUID.randomUUID() + ".war");
		
		// whole project
		archive.addPackages(true, "com.github.chrisruffalo.eeconfig");
		
		// as a bean-enabled archive
		archive.addAsWebInfResource("META-INF/beans.xml", "beans.xml");
		
		// load libraries
		MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class);
		
		// prevent from downloading additional resources
		// not found in the local repo
		resolver.configureFrom("pom.xml");
		resolver.useCentralRepo(false);
		resolver.goOffline();
		
		// load versions from properties file
		String commonsConfigurationVersion = properties.getProperty("commons.configuration.version", "1.9");
		
		// commons-configuration and dependencies
		archive.addAsLibraries(resolver.artifact("commons-configuration:commons-configuration:" + commonsConfigurationVersion).resolveAsFiles());
		
		// add resources
		archive.addAsResource("properties/priority1.properties")
			   .addAsResource("properties/priority2.properties")
			   .addAsResource("properties/priority3.properties")
			   .addAsResource("xml/priority4.xml")
			   .addAsResource("xml/priority5.xml")
		;
		
		// return archive
		return archive;		
	}
	
}
