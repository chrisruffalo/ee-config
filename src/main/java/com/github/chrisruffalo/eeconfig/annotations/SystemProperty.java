package com.github.chrisruffalo.eeconfig.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Annotation used for Injecting System Properties
 * <br/>
 * see also: https://community.jboss.org/wiki/JBossProperties
 * 
 * @author Chris Ruffalo
 * 
 */
@Qualifier
@Retention(RUNTIME)
@Target({ METHOD, FIELD, PARAMETER, TYPE })
public @interface SystemProperty {

	/**
	 * The key to use to look up the system property
	 * 
	 * @return Value of the key that will be 
	 * 		   used to look up the system
	 * 		   property
	 */
	@Nonbinding
	String key();
	
	/**
	 * The value to use if the system property is null
	 * 
	 * @return
	 */
	@Nonbinding
	String defaultValue() default "";
	
}