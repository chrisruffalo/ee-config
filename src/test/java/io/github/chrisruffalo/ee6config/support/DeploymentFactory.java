package io.github.chrisruffalo.ee6config.support;

import java.util.UUID;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

public final class DeploymentFactory {

	/**
	 * Private constructor for utility class
	 */
	private DeploymentFactory() {
		
	}
	
	public static WebArchive createDeployment() {
		// create archive
		WebArchive archive = ShrinkWrap.create(WebArchive.class, "ee6config-" + UUID.randomUUID() + ".war");
		
		// whole project
		archive.addPackages(true, "io.github.chrisruffalo.ee6config");
		
		// as a bean-enabled archive
		archive.addAsResource("META-INF/beans.xml");
		
		// load libraries
		MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class);
		
		// prevent from downloading additional resources
		// not found in the local repo
		resolver.configureFrom("pom.xml");
		resolver.useCentralRepo(false);
		resolver.goOffline();
		
		// commons-configuration and dependencies
		archive.addAsLibraries(resolver.artifact("commons-configuration:commons-configuration:1.6").resolveAsFiles());
		
		// return archive
		return archive;		
	}
	
}
