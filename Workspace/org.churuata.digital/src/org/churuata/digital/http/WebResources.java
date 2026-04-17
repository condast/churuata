package org.churuata.digital.http;

import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;

@Component( service = WebResources.class,
scope=ServiceScope.PROTOTYPE,
	property= {WebResources.S_OSGI_RESOURCE_PATTERN, WebResources.S_OSGI_RESOURCE_PREFIX}
)
@HttpWhiteboardResource(pattern=WebResources.S_WEB_PATTERN, prefix=WebResources.S_WEB_RERSOURCE)
public class WebResources {

	public static final String S_WEB_PATTERN = "/churuata/web/*";
	public static final String S_WEB_RERSOURCE = "/WEB-INF";

	public static final String S_OSGI_RESOURCE_PATTERN = "osgi.http.whiteboard.resource.pattern=" + S_WEB_PATTERN;
	public static final String S_OSGI_RESOURCE_PREFIX = "osgi.http.whiteboard.resource.prefix=" + S_WEB_RERSOURCE;

	private Logger logger = Logger.getLogger(WebResources.class.getName());

	public WebResources() {
		logger.info("**** RESOURCES LOADED: " + this.getClass().getName()); 
	}
}
