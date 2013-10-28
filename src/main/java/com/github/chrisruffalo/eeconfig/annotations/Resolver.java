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

import com.github.chrisruffalo.eeconfig.strategy.property.DefaultPropertyResolver;
import com.github.chrisruffalo.eeconfig.strategy.property.PropertyResolver;

/**
 * Configures a resolver to allow for greater complexity to bootstrap
 * and deal with configuration state
 * 
 * @author Chris Ruffalo
 *
 */
@Inherited
@Retention(RUNTIME)
@Target({ METHOD, FIELD, PARAMETER, TYPE })
public @interface Resolver {
	
	/**
	 * The implementing class for the property resolver.  Allows the annotation
	 * to configure how the token properties found in the resource/file paths will
	 * be resolved.  
	 * 
	 * @return the class that implements the property resolver behavior
	 */
	@Nonbinding
	Class<? extends PropertyResolver> impl() default DefaultPropertyResolver.class;
	
	/**
	 * Uses Apache Commons Configuration to load more properties into the property resolver
	 * so that a bootstrap configuration file could be used to set an initial
	 * configuration state before configuring application state.
	 * 
	 * @return the configuration to use to bootstrap the property resolver
	 */
	@Nonbinding
	Bootstrap bootstrap() default @Bootstrap();
	
	/**
	 * Default properties in the property resolver. Allows a mechanism for specifying
	 * default values for property keys.
	 * 
	 * @return key/value pairs for property defaults
	 */
	@Nonbinding
	DefaultProperty[] properties() default {};
}
