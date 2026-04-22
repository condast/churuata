package org.churuata.rest;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.ws.rs.ApplicationPath;

import java.io.IOException;

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
property= RestFilter.S_OSGI_FILTER_REGEX)
@HttpWhiteboardFilterPattern( RestFilter.S_CONTEXT_PATH)
public class RestFilter extends AbstractFilterWrapper implements Filter{

	//Same as alias in plugin.xml
	public static final String S_CONTEXT_PATH = "/churuatas/rest/*";
	public static final String S_OSGI_FILTER_REGEX = "osgi.http.whiteboard.filter.regex=" + S_CONTEXT_PATH;

	public RestFilter() {
		super( S_CONTEXT_PATH );
	}

	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		super.doFilter(request, response, chain);
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
