package org.churuata.digital.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = LegalResources.class,
scope=ServiceScope.PROTOTYPE,
	property= {LegalResources.S_OSGI_RESOURCE_PATTERN, LegalResources.S_OSGI_RESOURCE_PREFIX}
)
@HttpWhiteboardResource(pattern=LegalResources.S_WEB_PATTERN, prefix=LegalResources.S_WEB_RERSOURCE)
public class LegalResources {

	public static final String S_WEB_PATTERN = "/churuata/legal/*";
	public static final String S_WEB_RERSOURCE = "/legal";

	public static final String S_OSGI_RESOURCE_PATTERN = "osgi.http.whiteboard.resource.pattern=" + S_WEB_PATTERN;
	public static final String S_OSGI_RESOURCE_PREFIX = "osgi.http.whiteboard.resource.prefix=" + S_WEB_RERSOURCE;

	private Logger logger = Logger.getLogger(LegalResources.class.getName());

	public LegalResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
