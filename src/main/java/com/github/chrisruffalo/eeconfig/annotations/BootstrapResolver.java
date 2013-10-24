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
 * A special instance of a resolver that cannot be bootstrapped
 * but can still do other things a resolver can do.
 * 
 * @author Chris Ruffalo
 *
 */
@Inherited
@Retention(RUNTIME)
@Target({ METHOD, FIELD, PARAMETER, TYPE })
public @interface BootstrapResolver {
	
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
	 * Default properties in the property resolver. Allows a mechanism for specifying
	 * default values for property keys.
	 * 
	 * @return key/value pairs for property defaults
	 */
	@Nonbinding
	Property[] properties() default {};
}
