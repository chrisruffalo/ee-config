package com.github.chrisruffalo.eeconfig.resources;

import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.chrisruffalo.eeconfig.annotations.EEDefaultComponent;
import com.github.chrisruffalo.eeconfig.annotations.EEFallbackComponent;
import com.github.chrisruffalo.eeconfig.annotations.EELogging;

@ApplicationScoped
public class BeanResolver {

	@Inject
	@EELogging
	private Logger logger;
	
	@Inject
	private BeanManager manager;
	
	/**
	 * Resolve managed bean for given type
	 * 
	 * @param rootTypeToResolve
	 * @param defaultType
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "serial" })
	public <B, T extends B, D extends B> B resolveBean(Class<T> rootTypeToResolve, Class<D> targetType) {

		// if type to resolve is null, do nothing, not even the default
		if(rootTypeToResolve == null) {
			return null;
		}
		
		// used for marking a bean as found
		Bean<?> foundBean = null;
		
		// candidates place-holder
		Set<Bean<?>> candidates = null;

	      // try and directly resolve the target type if one exists
		if(targetType != null) {
		    candidates = this.manager.getBeans(targetType);
    		if(!candidates.isEmpty()) {
    		    foundBean = candidates.iterator().next();
    		}
		}		
		
		// if no bean (of target type) was found
		// then we need to start searching for
		// others
		if(foundBean == null) {
		    // look for default implementation
		    candidates = this.manager.getBeans(rootTypeToResolve, new AnnotationLiteral<Any>(){}, new AnnotationLiteral<EEDefaultComponent>(){});
		    // if no default implementation is found, look for the fallback implementation
            if(candidates.isEmpty()) {
                candidates = this.manager.getBeans(rootTypeToResolve, new AnnotationLiteral<Any>(){}, new AnnotationLiteral<EEFallbackComponent>(){});
            }
		    if(!candidates.isEmpty()) {
		        foundBean = candidates.iterator().next();
		    } else {
		        // couldn't find anything!
		        return null;
		    }
		}		 
		
		this.logger.trace("Requesting resolution on: {}", rootTypeToResolve.getName());
		
		// get/create candidate
		Bean<?> bean = foundBean;
		CreationalContext<?> context = this.manager.createCreationalContext(bean);
		Type type = (Type) bean.getTypes().iterator().next();
	    B result = (B)this.manager.getReference(bean, type, context);
		
		this.logger.trace("Resolved to: {}", result.getClass().getName());
		
		return result;
	}
	
}
