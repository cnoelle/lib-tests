package de.cnoelle.websockets.client;

import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Configuration interface for OSGi config admin
 */
@ObjectClassDefinition
@ComponentPropertyType
public @interface ClientConfig {

	String server() default "ws://localhost:8448";
	long updateIntervalSeconds() default 10;
	
}
