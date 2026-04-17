package org.churuata.digital.organisation;

import jakarta.servlet.Servlet;
import jakarta.ws.rs.ApplicationPath;

import org.churuata.digital.organisation.rest.ChatResource;
import org.churuata.digital.organisation.rest.ContactPersonResource;
import org.churuata.digital.organisation.rest.OrganisationResource;
import org.condast.commons.messaging.http.AbstractServletWrapper;
import org.condast.commons.messaging.rest.CorsFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(service = Servlet.class, 
scope=ServiceScope.PROTOTYPE,
property= RestServlet.S_OSGI_SERVLET_PATTERN)
public class RestServlet extends AbstractServletWrapper {

	public static final String S_CONTEXT_PATH = "churuatas/organisation";
	public static final String S_OSGI_SERVLET_PATTERN = "osgi.http.whiteboard.servlet.pattern=" + S_CONTEXT_PATH;

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
				register( CorsFilter.class );
				register( OrganisationResource.class );
				register( ContactPersonResource.class );
				register( ChatResource.class );
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
		}
	}
}