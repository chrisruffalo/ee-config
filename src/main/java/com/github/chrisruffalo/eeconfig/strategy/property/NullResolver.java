package com.github.chrisruffalo.eeconfig.strategy.property;

import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import com.github.chrisruffalo.eeconfig.annotations.EEFallbackComponent;
import com.github.chrisruffalo.eeconfig.annotations.EEPlaceholderComponent;

/**
 * Signifies that no implementation has been selected and
 * that we should try and load the default with CDI or
 * use the {@link EEFallbackComponent} annotated one.
 * 
 * @author Chris Ruffalo
 *
 */
@EEPlaceholderComponent
public final class NullResolver implements PropertyResolver {

    public NullResolver() {
        throw new NotImplementedException("This class should not be constructed.");
    }
    
    @Override
    public String resolveProperties(String fullString) {
        return null;
    }

    @Override
    public String resolveProperties(String fullString,
            Map<Object, Object> bootstrapProperties) {
        return null;
    }

    @Override
    public String resolveProperties(String fullString,
            Map<Object, Object> bootstrapProperties,
            Map<Object, Object> defaultProperties) {
        return null;
    }

}
