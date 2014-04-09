package com.github.chrisruffalo.eeconfig.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;

import com.github.chrisruffalo.eeconfig.strategy.locator.Locator;
import com.github.chrisruffalo.eeconfig.strategy.locator.MultiLocator;

/**
 * A source is an annotation that describes a unit of configuration.
 * It consists of a value element, a locator, and an option to resolve
 * properties within the value.  
 * <br/>
 * In general the 'value' element represents the path that the
 * resource is located on but could, conceptually, be used to
 * find resources stored using less conventional methods.
 * 
 * @author Chris Ruffalo
 *
 */
@Inherited
@Target({ METHOD, FIELD, PARAMETER, TYPE })
public @interface Source {

	/**
	 * The value of (conventionally the path to) the 
	 * desired configuration resource.
	 *  
	 * @return
	 */
	@Nonbinding
	String value();
	
	/**
	 * True if properties in the value/path should be
	 * resolved into their values and false if otherwise.
	 * <br/>
	 * Defaults to <code>false</code>.
	 * 
	 * @return
	 */
	@Nonbinding
	boolean resolve() default false;
	
	/**
	 * The {@link Locator} implementation used for finding
	 * the resource as given by the path/value for this
	 * source.
	 * <br/>
	 * {@link MultiLocator} is the default implementation.
	 * 
	 * @return
	 */
	@Nonbinding
	Class<? extends Locator> locator() default MultiLocator.class;
}
