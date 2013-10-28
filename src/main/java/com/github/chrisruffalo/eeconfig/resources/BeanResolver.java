package com.github.chrisruffalo.eeconfig.resources;

import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.chrisruffalo.eeconfig.annotations.Logging;

@ApplicationScoped
public class BeanResolver {

	@Inject
	@Logging
	private Logger logger;
	
	@Inject
	private BeanManager manager;
	
	/**
	 * Resolve managed bean for given type
	 * 
	 * @param typeToResolve
	 * @param defaultType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <B, T extends B, D extends B> B resolveBeanWithDefaultClass(Class<T> typeToResolve, Class<D> defaultType) {

		// if type to resolve is null, do nothing, not even the default
		if(typeToResolve == null) {
			return null;
		}
		
		// get candidate resolve types
		Set<Bean<?>> candidates = this.manager.getBeans(typeToResolve);
		
		// if no candidates are available, resolve
		// using next class up
		if(!candidates.iterator().hasNext()) {
			this.logger.trace("No candidates for: {}", typeToResolve.getName());
			// try and resolve only the default type
			return resolveBeanWithDefaultClass(defaultType, null);
		} 
		
		this.logger.trace("Requesting resolution on: {}", typeToResolve.getName());
		
		// get candidate
		Bean<?> bean = candidates.iterator().next();
		CreationalContext<?> context = this.manager.createCreationalContext(bean);
		Type type = (Type) bean.getTypes().iterator().next();
	    B result = (B)this.manager.getReference(bean, type, context);
		
		this.logger.trace("Resolved to: {}", result.getClass().getName());
		
		return result;
	}
	
}
