EE6-Config - Application, Configure Thyself
==========

## Introduction and Justification

So often, during the course of my work, I find that various projects have chosen, for whatever reason, to create configurations that are difficult to maintain per-environment.  A lot of these require one of the following:

* Configuration controlled by environment specific properties, each artifact built for a specific environment
* Properties embedded inside application war where the WAR must be cracked, exploded, or rebuilt for each environment
* Application configuration contained in the web.xml or other project artifact

About a year ago I was working on a project where I had to deploy 10 instances of an application each with a slightly different configuration.  This became massively irratating in light of the fact that there was no possibility for automated deployment and everything had to be done by hand.  Each application had to be deployed as an exploded WAR and each configuration file had to be edited.  This drove me *nuts*.

When version 2.0 of the application came along we were very excited to move to EE6 and we had a bit of a revalation.  We can **inject** configuration elements.  We can leverage the container to provide us with paths to the application.  We can do a lot of groundwork to make it easier for developers to create "no-worry" configuration situations.

## Requirements

Sadly, I don't want to code everything from scratch, so the EE6-Config library requires a few things to get started.  First it requires EE6.  It is developed using the JBoss flavor provided by the Maven dependency but there is no reason it shouldn't work on other EE6 implementations.

It also requires SLF4J 1.6.1, Apache Commons Configuration 1.6, and Apache Commons Utilities 1.6.  These utilities provide logging, configuration, and other shared parts that would be difficult to do without.

Commons Configuration also provides one of the injectable configuration types to allow you to **directly inject Commons Configuration** right into your application!

## Building and Testing

This application uses Maven to build.  It uses Arquillian for testing.  In order to test and build and install you should just execute the command 'mvn clean install' in the root of the project.  That's it!

## Use

### Including in your Maven Project

Adding this dependency to your project should be as simple as:

```xml
<dependency>
	<groupId>com.github.chrisruffalo</groupId>
	<artifactId>ee6-config</artifactId>
	<version>1.0-SNAPSHOT</version>	
</dependency>
```

Of course, this depends on you building and installing it yourself into your Maven repo.  It cannot be found in Maven central or another repository just yet.

### Logging

Because we find it useful and because I use it all the time this library includes a method injecting a SLF4J logger.

``` java
public class INeedALogger {
	
	@Inject
	private Logger logger;
	
	@PostConstruct
	public void init() {
		this.logger.info("Log!");
	}
	
}
```

The created logger is created using the class name of the injection target.

### System Properties

One of the first things we thought to inject was system properties.  This means that you can inject either the usual Java properties (like 'java.io.tmpdir') or system properties set by your container (like 'jboss.server.config.dir').  

``` java
public class INeedSystemProperties {
	
	@Inject
	@SystemProperty(key="java.io.tmpdir", defaultValue="/tmp")
	private String tmpDirPath;

	@Inject
	@SystemProperty(key="jboss.server.config.dir")
	private String configDirPath;

	@PostConstruct
	public void init() {
		// logic goes here
	}
}
```

This example shows, simply, the ability to inject system properties into your application and use them directly.  You will not need to do anything more.  This example also demonstrates the use of the 'defaultValue' annotation property which will be returned in the event that the system property is not defined.


### Configuration

Configuration is tricky, so we've tried to make it easier.  Injecting the configuration file is dead simple but there are some basic guidelines you'll need to remember.

* A *null object is never injected*.  There may be an empty property file or an empty input stream that is injected but it will **never** be null.
* In the non-merge case the **first** configuration file found is used for the injection.
* In the merge case the *first* configuration file has the highest prority, other found configuration files will have lower priority.
* When injecting an InputStream the merge flag has no effect.

Keeping in mind those things it is important to realize, too, that the configuration injection will inject the following types:

* java.util.Properties
* org.apache.commons.configuration.Configuration
* java.io.InputStream
* java.util.List<java.io.InputStream>

No other types are implemented yet.

### Examples

It might be nice if we just shut up and showed you how to use it.  Below you'll see some examples that use the @Configuration annotation.  The annotation is from the package 'io.github.chrisruffalo.ee6config.annotations'.

#### Example Java Properties

``` java
public class ConfigureMeWithProperties {
	@Inject
	@Configuration(
		paths = {
			"${jboss.server.config.dir}/application/main.properties" // main configuration
			"resource:default.properties" // will look on classpath for default properties
		},
		resolveSystemProperties = true, // resolves system properties in paths
		merge = true // merges results
	)
	private Properties config; 
	
}
```
The result of this annotation is that a java.util.Properties object will be injected and the contents of that injection will be first populated from the file found at "${jboss.server.config.dir}/application/main.properties" and then the classpath resource with the path "default.properties" will be loaded to provide the rest of the values.  The resolution is done in order.  The resolveSystemProperties flag must be set to 'true' in order to resolve the token '${jboss.server.config.dir}'.

If merge was not set or set to false then **only** the values of the first configuration file would be loaded.  Since the value is set to true each file that is found is merged with the others.  The first file that is found has the highest priority.

#### Example 2: Commons Configuration

``` java
public class ConfigureMeWithCommonConfiguration {
	@Inject
	@Configuration(
		paths = {
			"${jboss.server.config.dir}/application/main.properties" // main configuration
			"resource:default.properties" // will look on classpath for default properties
		},
		resolveSystemProperties = true, // resolves system properties in paths
		merge = true // merges results
	)
	private org.apache.commons.configuration.Configuration config; 
}
```

This example is *almost exactly* the same as the above with one minor change... the injection target is of the commons configuration type.  That is the only difference.  The behavior with respect to merging is still the same but it uses [the 'OverrideCombiner' behavior of the Commons Configuration API](http://commons.apache.org/proper/commons-configuration//apidocs/org/apache/commons/configuration/tree/OverrideCombiner.html).

#### Example 3: InputStream

Say that you *don't* need a fancy configuration object and you'd like to do something yourself.  One of the ways that you can implement your own behavior is by injecting an InputStream instead.

``` java
public class ConfigureMeAnInputStream {
	@Inject
	@Configuration(
		paths = {
			"${jboss.server.config.dir}/application/main.properties" // main configuration
			"resource:default.properties" // will look on classpath for default properties
		},
		resolveSystemProperties = true, // resolves system properties in paths
	)
	private InputStream configStream; 
	
	@PostConstruct
	private void init() {
		// load configuration from injected input stream
	}
}
```

In this case you'll need to take an extra step to get your configuration file by loading it into whatever mechanism you choose.  You'll also notice that the 'merge' flag is missing.  When injecting a plain stream the merge flag does nothing.

#### Example 4: InputStream - The List-ening

So, let's say you want to go one step farther and implement your own merge behavior.  Sure, you can do that... just inject a List of InputStream objects.

``` java
public class ConfigureMeAnInputStream {
	@Inject
	@Configuration(
		paths = {
			"${jboss.server.config.dir}/application/main.properties" // main configuration
			"resource:default.properties" // will look on classpath for default properties
		},
		resolveSystemProperties = true, // resolves system properties in paths
	)
	private List<InputStream> configStream; 
	
	@PostConstruct
	private void init() {
		// load configuration from injected input stream
	}
}
```

You should also note that the 'merge' flag has no effect in this injection context.

### But... I don't like any of those examples!

Well, man, I don't know what to tell you... maybe you need to *implement your own custom injection type*.

```java
public class MyCustomConfigurationProducer extends AbstractConfigurationProducer {

	@Inject
	private Logger logger;
	
	@Produces
	@Configuration(paths={})
	public MyCustomConfigurationType getConfiguration(InjectionPoint injectionPoint) {
		// first use a utility method to get configuration annotation from the injection point
		Configuration annotation = this.getAnnotation(injectionPoint);
		
		// next get the input streams that match the configuration paths given
		// this method will ALWAYS return a non-null list with AT LEAST ONE
		// input stream.  (the InputStream may have no content.)  
		List<InputStream> streams = this.locate(annotation);
		
		// implement custom logic here
		MyCustomConfigurationType config = MyCustomConfigurationType.load(streams);
		
		// return configuration
		return config;
	}
```

Using this method you'll be able to implement whatever crazy scheme you can come up with to create your applications configuration.

## Other

This application is released under the Apache License v2.

