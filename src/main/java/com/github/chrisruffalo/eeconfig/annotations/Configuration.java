package com.github.chrisruffalo.eeconfig.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import com.github.chrisruffalo.eeconfig.strategy.property.DefaultPropertyResolver;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

/**
 * Annotation for specifying configuration file that
 * should be loaded.
 *  
 * @author Chris Ruffalo
 * 
 */
@Inherited
@Qualifier
@Retention(RUNTIME)
@Target({ METHOD, FIELD, PARAMETER, TYPE })
public @interface Configuration {
	
	/**
	 * List of configuration sources
	 * 
	 * @return a list of paths to search for the configuration file
	 */
	@Nonbinding
	Source[] sources() default {};
	
	/**
	 * Default configuration source
	 * 
	 * @return
	 */
	@Nonbinding
	Source defaultSource() default @Source("");
	
	/**
	 * If true it indicates that the files should be merged, in order 
	 * that they were found, <em>if</em> the underlying configuration system
	 * has any concept of merging.
	 * <br/>
	 * If the configuration list is returned directly then no merge
	 * would be performed. 
	 * 
	 * @return true if the results should be merged
	 */
	@Nonbinding
	boolean merge() default false;
	
	/**
	 * The implementing class for the property resolver.  Allows the annotation
	 * to configure how the token properties found in the resource/file paths will
	 * be resolved.  
	 * 
	 * @return the class that implements the property resolver behavior
	 */
	@Nonbinding
	Class<? extends PropertyResolver> resolver() default DefaultPropertyResolver.class;
	
}