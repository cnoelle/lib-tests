package de.cnoelle.websockets.server;

import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Configuration interface for OSGi config admin
 */
@ObjectClassDefinition
@ComponentPropertyType
public @interface ServerConfig {

	String host() default "localhost";
	int port() default 8448;
	long updateIntervalSeconds() default 10;
	
}
