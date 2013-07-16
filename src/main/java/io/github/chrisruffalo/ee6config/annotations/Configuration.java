package io.github.chrisruffalo.ee6config.annotations;

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
 * Annotation for specifying configuration file that
 * should be loaded.
 *  
 * @author Chris Ruffalo
 * 
 */
@Qualifier
@Retention(RUNTIME)
@Target({ METHOD, FIELD, PARAMETER, TYPE })
public @interface Configuration {
	
	/**
	 * List of paths to configuration file.  Given paths will be
	 * searched, <em>in order</em>, to find a configuration file
	 * whose name matches the given file.
	 * <br/>
	 * By default the path given indicates a location on 
	 * the filesystem.  To search a resource the string
	 * "resource:" should be prepended to the path string.
	 * <br/>
	 * Some examples:
	 * <ul>
	 * <li>"/tmp/app/configuration.xml" - looks on the filesystem</li>
	 * <li>"resource:META-INF/build.properties" - searches the classpath resources</li>
	 * </ul>
	 * <br/>
	 * It is important to realize that giving absolute paths to a file
	 * is important as giving relative paths may result in unexpected
	 * behavior on different virtual machines
	 * 
	 * @return a list of paths to search for the configuration file
	 */
	@Nonbinding
	String[] paths();
	
	/**
	 * Indicates (when true) that the list of directories to be searched
	 * has entries that should be interpreted as system properties.
	 * <br/>
	 * Strings that should be replaced with system properties 
	 * should start with "${" and end with "}".  Ex: "${java.io.tmpdir}" 
	 * should resolve to the temporary directory.
	 * <br/>
	 * Partial tokenization is possible as well, ex: "${java.io.tmpdir}/app"
	 * should resolve (on *nix) to "/tmp/app".
	 * <br/>
	 * Directories without system properties will be left alone.
	 * 
	 * @return true if system properties should be resolved in paths
	 */
	@Nonbinding
	boolean resolveSystemProperties() default false;
	
	/**
	 * If true it indicates that the files should be merged, in order 
	 * that they were found, if the underlying configuration system
	 * has any concept of merging.
	 * <br/>
	 * If the configuration list is returned directly then no merge
	 * would be performed. 
	 * 
	 * @return true if the results should be merged
	 */
	@Nonbinding
	boolean merge() default false;
}