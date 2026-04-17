package org.churuata.rest;

import jakarta.servlet.Filter;
import jakarta.ws.rs.ApplicationPath;

import org.churuata.rest.resources.CaminantesResource;
import org.churuata.rest.resources.ChuruataResource;
import org.churuata.rest.resources.PushResource;
import org.condast.commons.messaging.http.AbstractFilterWrapper;
import org.condast.commons.messaging.rest.CorsFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;

@Component(scope=ServiceScope.PROTOTYPE,
property= RestFilter.S_OSGI_FILTER_PATTERN)
@HttpWhiteboardFilterPattern( RestFilter.S_CONTEXT_PATH)
public class RestFilter extends AbstractFilterWrapper implements Filter{

	//Same as alias in plugin.xml
	public static final String S_CONTEXT_PATH = "/churuatas/rest/*";
	public static final String S_OSGI_FILTER_PATTERN = "osgi.http.whiteboard.filter.pattern=" + S_CONTEXT_PATH;

	public RestFilter() {
		super( S_CONTEXT_PATH );
	}
	
	@Override
	protected Filter onCreateFilter(String contextPath) {
		RestApplication resourceConfig = new RestApplication();
		return new ServletContainer(resourceConfig);
	}

	@ApplicationPath(S_CONTEXT_PATH)
	private class RestApplication extends ResourceConfig {

		//Loading classes is the safest way...
		//in equinox the scanning of packages may not work
		private RestApplication() {
			register( CorsFilter.class );
			register( ChuruataResource.class );
			register( CaminantesResource.class );
			register( PushResource.class );
		}
	}
}
