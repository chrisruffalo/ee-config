EE-Config - Application, Configure Thyself
==========

## Introduction and Justification

So often, during the course of my work, I find that various projects have chosen, for whatever reason, to create configurations that are difficult to maintain per-environment.  A lot of these require one of the following:

* Configuration controlled by build-specific properties, each artifact built for a specific environment
* Properties embedded inside application war where the WAR must be cracked, exploded, or rebuilt for each environment
* Application configuration contained in the web.xml or other project artifact

About a year ago I was working on a project where I had to deploy 10 instances of an application each with a slightly different configuration.  This became massively irratating in light of the fact that there was no possibility for automated deployment and everything had to be done by hand.  Each application had to be deployed as an exploded WAR and each configuration file had to be edited.  This drove me *nuts*.

When version 2.0 of the application came along we were very excited to move to EE6 and we had a bit of a revalation.  We can **inject** configuration elements.  We can leverage the container to provide us with paths to the application.  We can do a lot of groundwork to make it easier for developers to create "no-worry" configuration situations... but we had to design our own way.  EE-Config is an outgrowth of the lessons learned on that project.

## Requirements

Sadly, I don't want to code everything from scratch, so the EE-Config library requires a few things to get started.  First it requires at least EE6.  It is developed using the JBoss flavor provided by the Maven dependency but there is no reason it shouldn't work on other EE implementations.

It also requires 

* SLF4J 1.6.1
* Apache Commons Configuration 1.9
* Apache Commons Utilities 1.9

These utilities provide logging, configuration, and other shared parts that would be difficult to do without.

Commons Configuration also provides one of the injectable configuration types to allow you to **directly inject Commons Configuration** right into your application!

## Building and Testing

This application uses Maven to build.  It uses Arquillian (Weld SE) for testing.  In order to test and build and install you should just execute the command 'mvn clean install' in the root of the project.  That's it!

## Use

### Including in your Maven Project

Adding this dependency to your project should be as simple as:

```xml
<dependency>
  <groupId>com.github.chrisruffalo</groupId>
  <artifactId>ee-config</artifactId>
  <version>1.4</version>
</dependency>
```

The project 'ee-config' has been in maven central since version 1.0.

### Logging

Because we find it useful and because I use it all the time this library includes a method injecting a SLF4J logger.  The '@AutoLogger' qualifier is uesd so that the included Logger producer can easily be ignored or overriden.

``` java
public class INeedALogger {
	
	@Inject
	@AutoLogger
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
* In the merge case the *first* configuration file has the highest priority, other found configuration files will have lower priority.
* When injecting an InputStream, InputStreams, or raw ISources the merge flag has no effect.

Keeping in mind those things it is important to realize, too, that the configuration injection will inject the following types:

* java.util.Properties
* org.apache.commons.configuration.Configuration
* java.io.InputStream
* java.util.List\<java.io.InputStream\>
* java.util.List\<com.github.chrisruffalo.eeconfig.source.ISource\> (so called "raw" source injection)

The Commons Configuraiton supports the following subtypes:

* PropertiesConfiguration
* XMLConfiguraiton

No other types or subtypes are implemented yet.  *(To request other types, jump over to the issues page!)*

### Examples

It might be nice if we just shut up and showed you how to use it.  Below you'll see some examples that use the @Configuration annotation.  The annotation is from the package 'com.github.chrisruffalo.eeconfig.annotations'.

#### Example Java Properties

``` java
public class ConfigureMeWithProperties {
	@Inject
	@Configuration(
		sources = {
			// main configuration
			@Source(
			    value="${jboss.server.config.dir}/application/main.properties",  
			    resolve=true // resolves system properties for this source
			),  
			// will look on classpath for default properties
			@Source(value="resource:default.properties") 
		},
		merge = true // merges results
	)
	private Properties config; 
	
}
```
The result of this annotation is that a java.util.Properties object will be injected and the contents of that injection will be first populated from the file found at "${jboss.server.config.dir}/application/main.properties" and then the classpath resource with the path "default.properties" will be loaded to provide the rest of the values.  The resolution is done in order.  The resolve flag must be set to 'true' in order to resolve the token '${jboss.server.config.dir}'.

If merge was not set or set to false then **only** the values of the first configuration file would be loaded.  Since the value is set to true each file that is found is merged with the others.  The first file that is found has the highest priority.

#### Example 2: Commons Configuration

``` java
public class ConfigureMeWithCommonConfiguration {
	@Inject
	@Configuration(
		sources = {
		    // main configuration
			@Source(
                "${jboss.server.config.dir}/application/main.properties", 
                resolve=true
            ),
			// will look on classpath for default properties 
			@Source("resource:default.properties")
		},
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
		sources = {
			// main configuration
            @Source(
                "${jboss.server.config.dir}/application/main.properties", 
                resolve=true
            ),
			// will look on classpath for default properties 
			@Source("resource:default.properties") 
		}
	)
	private InputStream configStream; 
	
	@PostConstruct
	private void init() {
		// load configuration from injected input stream
		// ... logic here ...

		// close input stream
		configStream.close();
	}
}
```

In this case you'll need to take an extra step to get your configuration file by loading it into whatever mechanism you choose.  You'll also notice that the 'merge' flag is missing.  When injecting a plain stream the merge flag does nothing.

**It is important to note** that you must, MUST, close the InputStream yourself when you are done with them.

#### Example 4: InputStream - The List-ening

So, let's say you want to go one step farther and implement your own merge behavior.  Sure, you can do that... just inject a List of InputStream objects.

``` java
public class ConfigureMeAnInputStreamList {
	@Inject
	@Configuration(
		sources = {
		    // main configuration
			@Source(
			    "${jboss.server.config.dir}/application/main.properties", 
			    resolve=true
			),
			// will look on classpath for default properties
			@Source("resource:default.properties") 
		}
	)
	private List<InputStream> configStreams; 
	
	@PostConstruct
	private void init() {
		// load configuration from injected input stream
		// ... logic here ...

		// close streams
		for(InputStream stream : configStreams) {
			stream.close();
		}
	}
}
```

You should also note that the 'merge' flag has no effect in this injection context.

#### Example 5: Raw Input Sources

For something a little more advanced you can inject [ISource](src/main/java/com/github/chrisruffalo/eeconfig/source/ISource.java) objects directly!  This gives fairly fine grained control over how the streams are loaded and handled.

``` java
public class ConfigureFromRawSources {
	@Inject
	@Configuration(
		sources = {
		    // main configuration
			@Source(
                "${jboss.server.config.dir}/application/main.properties", 
                resolve=true
            ), 
			// will look on classpath for default properties
			@Source("resource:default.properties")
		}
	)
	private List<IConfigurationSource> configSources; 
	
	@PostConstruct
	private void init() {
		// loop through available sources
		for(IConfigurationSource source : this.configSources) {
			// if a source is not available, move on
			if(!source.available()) {
				continue;
			}

			// if the source is available, get the stream
			InputStream stream = source.stream();

			// implement reading the stream where you want it to go...
			
			/* ... <snip> ... */
			
			// close the stream
			stream.close();
		}
	}
}
```

### But... I don't like any of those examples!

Well, man, I don't know what to tell you... maybe you need to *implement your own custom injection producer*.

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
		// input source.  (The configuration source may have no content.)
		// see IConfigurationSource for more details  
		List<ISource> sources = this.locate(annotation);		
		
		// implement custom logic to load the input stream here
		MyCustomConfigurationType config = MyCustomConfigurationType.load(sources);

		// return configuration
		return config;
	}
```

Using this method you'll be able to implement whatever crazy scheme you can come up with to create your application's configuration.

## Extending EE-Configuration default behavior

There are two extendable behaviors in EE-Config.  Each of these is governed by a strategy.  These strategies can be overriden to produce different behaviors for finding resources and files.

**Each of these strategies *must* have a public no-arg constructor.**  If they do not then they will not be usable and the default implementation of each will be used instead.

*It should be noted* that these classes will be loaded as **beans** by the CDI container.  This means that they are free to have CDI features of their own.  It would be possible to read from a database or otherwise inject other behavior into your bean implementation.  The intent is to make these strategies as flexible as possible.

### Configuration source and resource location

The [Locator](src/main/java/com/github/chrisruffalo/eeconfig/strategy/locator/Locator.java) is the interface for creating custom locators for the configuration sources.

The following implementations of Locator are provided by default

* [MultiLocator](src/main/java/com/github/chrisruffalo/eeconfig/strategy/locator/MultiLocator.java) - locates ISource elements by looking at the file system and classpath, this is the default locator
* [FileLocator](src/main/java/com/github/chrisruffalo/eeconfig/strategy/locator/FileLocator.java) - locates files on the local filesystem
* [ResourceLocator](src/main/java/com/github/chrisruffalo/eeconfig/strategy/locator/ResourceLocator.java) - locates resources on the classpath

### Property token resolution

You can implement your own [PropertyResolver](src/main/java/com/github/chrisruffalo/eeconfig/strategy/property/PropertyResolver.java).  There is also a [default implementation](src/main/java/com/github/chrisruffalo/eeconfig/strategy/property/DefaultPropertyResolver.java) to handle the resolution of properties within the resource paths.  This could be overriden to provide different token types or possibly even a pre-seeded property set.  You can load properties from the database, filesystem, or just about anywhere you need to in order to get the base values for your application.

### Putting it to work

Let's say you *do* want some form of custom resolution.

``` java
public class ConfigureMeWithCustomBehavior {
	@Inject
	@Configuration(
	    // main configuration
		sources = {
			@Source(
			    "@@jboss.server.config.dir@@/application/main.properties", 
			    resolve=true, 
			    locator=CustomLocator.class
			) 
		},
		resolver = com.example.CustomTokenResolver.class // custom property resolver
	)
	private Properties properties; 
	
	@PostConstruct
	private void init() {
		
	}
}
```

This example shows the possibility of using a custom resolver to resolve a different token type (@@jboss.server.config.dir@@) and a custom locator that could possibly resolve files a little differently.  Overriding the default behavior is that simple.

## Issues

Please feel to open issues if you have problems with EE-Config.

## License

This application is released under the Apache License v2.

