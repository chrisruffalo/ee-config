package com.github.chrisruffalo.eeconfig.resources;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Qualifier;

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
	
	@PostConstruct
	private void init() {

	}
	
	/**
	 * Resolve managed bean for given type, with an optional target type
	 * 
	 * @param rootTypeToResolve
	 * @param targetType
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
		    this.logger.trace("Attempting to find provided impl class: {}", targetType);
		    
		    // get qualifiers to look up exact impl
		    Annotation[] possibleQualifiers = targetType.getAnnotations();
		    Set<Annotation> qualifierSet = new HashSet<Annotation>();
		    for(Annotation p : possibleQualifiers) {
		        if(null != p.getClass().getAnnotation(Qualifier.class)) {
		            qualifierSet.add(p);
		        }
		    }
		    qualifierSet.add(new AnnotationLiteral<Any>(){});
		    Annotation[] qualifiers = qualifierSet.toArray(new Annotation[qualifierSet.size()]);
		    
		    // look for candidates with calculated qualifiers
		    candidates = this.manager.getBeans(targetType, qualifiers);
    		if(!candidates.isEmpty()) {
    		    for(Bean<?> candidate : candidates) {
    		        if(candidate.getBeanClass().equals(targetType)) {
    		            foundBean = candidate;
    		            break;
    		        }
    		    }    		    
    		    if(foundBean != null) {
    		        this.logger.trace("Found impl class: {}", foundBean.getBeanClass().getName());
    		    } else {
    		        this.logger.warn("Couldn't find non-null implementing bean");
    		    }
    		} else {
    		    this.logger.trace("Could not find impl class, proceeding...");
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
		        // log bad result
		        this.logger.warn("Couldn't find any implementing bean for {}", rootTypeToResolve.getName());
		        
		        // couldn't find anything!
		        return null;
		    }
		}
  		
        // log debug
		this.logger.trace("Requested resolution on: {}", rootTypeToResolve.getName());
		if(targetType != null) {
		    this.logger.trace("With provided impl: {}", targetType.getName());
		}
		
		// get/create candidate
		CreationalContext<?> context = this.manager.createCreationalContext(foundBean);
		Type type = targetType == null ? foundBean.getBeanClass() : targetType;
	    B result = (B)this.manager.getReference(foundBean, type, context);
		
	    // more debug logging
		this.logger.trace("Resolved to: {}", result.getClass().getName());		
		
		return result;
	}
	
}
