package org.satr.arnac.authentication;

import javax.servlet.Servlet;
import javax.ws.rs.ApplicationPath;

import org.condast.commons.messaging.http.AbstractServletWrapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.satr.arnac.authentication.rest.AuthenticationResource;
import org.satr.arnac.authentication.rest.CorsFilter;

public class RestServlet extends AbstractServletWrapper {

	//Same as alias in plugin.xml
	public static final String S_CONTEXT_PATH = "arnac";

	public RestServlet() {
		super( S_CONTEXT_PATH );
	}
	
	@Override
	protected Servlet onCreateServlet(String contextPath) {
		RestApplication resourceConfig = new RestApplication();
		return new ServletContainer(resourceConfig);
	}

	@ApplicationPath(S_CONTEXT_PATH)
	private class RestApplication extends ResourceConfig {

		//Loading classes is the safest way...
		//in equinox the scanning of packages may not work
		private RestApplication() {
			try {
				register( AuthenticationResource.class );
				register( CorsFilter.class );
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
		}
	}
}