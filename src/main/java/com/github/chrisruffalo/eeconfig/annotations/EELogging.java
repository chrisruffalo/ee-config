package com.github.chrisruffalo.eeconfig.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Configure the logger that will be injected.
 * 
 * @author Chris Ruffalo
 * 
 */
@Qualifier
@Retention(RUNTIME)
@Target({ METHOD, FIELD, PARAMETER, TYPE })
public @interface EELogging {

	/**
	 * Name of the logger to use
	 * 
	 * @return
	 */
	@Nonbinding
	String name() default "";
	
	/**
	 * Resolver to use when resolving properties in
	 * the name of the resolver 
	 * 
	 * @return
	 */
	@Nonbinding
	Resolver resolver() default @Resolver();
}