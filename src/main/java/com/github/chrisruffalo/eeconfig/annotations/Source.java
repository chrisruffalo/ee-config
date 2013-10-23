package com.github.chrisruffalo.eeconfig.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import com.github.chrisruffalo.eeconfig.strategy.locator.FileSourceLocator;
import com.github.chrisruffalo.eeconfig.strategy.locator.ISourceLocator;

@Inherited
@Qualifier
@Target({ METHOD, FIELD, PARAMETER, TYPE })
public @interface Source {

	@Nonbinding
	String value();
	
	@Nonbinding
	boolean resolve() default false;
	
	@Nonbinding
	Class<? extends ISourceLocator> locator() default FileSourceLocator.class;
}
