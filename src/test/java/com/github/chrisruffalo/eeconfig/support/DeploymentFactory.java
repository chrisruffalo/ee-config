package com.github.chrisruffalo.eeconfig.support;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DeploymentFactory {

	/**
	 * Private constructor for utility class
	 */
	private DeploymentFactory() {
		
	}
	
	public static JavaArchive createDeployment() {
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
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "ee-config-test-" + UUID.randomUUID() + ".jar");
		
		// whole project
		archive.addPackages(true, "com.github.chrisruffalo.eeconfig");
		
		// as a bean-enabled archive
		archive.addAsResource("META-INF/beans.xml");
		
		// load libraries
		//File[] libs = Maven.resolver()  
		//	    .loadPomFromFile("pom.xml")
		//	    .resolve("commons-configuration:commons-configuration")
		//	    .withTransitivity().asFile();
		
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
