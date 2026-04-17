package org.churuata.digital.authentication;

import jakarta.servlet.Servlet;
import jakarta.ws.rs.ApplicationPath;

import org.churuata.digital.authentication.rest.AdminResource;
import org.churuata.digital.authentication.rest.AuthenticationResource;
import org.condast.commons.messaging.http.AbstractServletWrapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(service = Servlet.class, 
scope=ServiceScope.PROTOTYPE,
property= RestServlet.S_OSGI_SERVLET_PATTERN)
public class RestServlet extends AbstractServletWrapper {

	//Same as alias in plugin.xml
	public static final String S_CONTEXT_PATH = "churuatas/rest";
	public static final String S_OSGI_SERVLET_PATTERN = "osgi.http.whiteboard.servlet.pattern=" + S_CONTEXT_PATH;

	public RestServlet() {
		super( S_CONTEXT_PATH );
	}
	
	@Override
	protected Servlet onCreateServlet(String contextPath) {
		RestApplication resourceConfig = new RestApplication();
		return null;// new ServletContainer(resourceConfig);
	}

	@ApplicationPath(S_CONTEXT_PATH)
	private class RestApplication extends ResourceConfig {

		//Loading classes is the safest way...
		//in equinox the scanning of packages may not work
		private RestApplication() {
			register( AuthenticationResource.class );
			register( AdminResource.class );
		}
	}
}
