package org.churuata.digital.authentication;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletException;
import jakarta.ws.rs.ApplicationPath;

import java.util.EventListener;

import org.churuata.digital.authentication.rest.AdminResource;
import org.churuata.digital.authentication.rest.AuthenticationResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;
import org.glassfish.jersey.servlet.ServletContainer;

@Component(scope=ServiceScope.PROTOTYPE,
property= RestApplication.S_OSGI_FILTER_PATTERN)
@HttpWhiteboardFilterPattern( "/*")
public class RestApplication extends ServletContainer implements Filter{
	private static final long serialVersionUID = 3395216202433258325L;

	//Same as alias in plugin.xml
	public static final String S_CONTEXT_PATH = "churuatas/auth/*";
	public static final String S_OSGI_FILTER_PATTERN = "osgi.http.whiteboard.filter.regex=" + S_CONTEXT_PATH;

	private EventListener listener = new EventListener() {
		
	};
	
	public RestApplication() {
		super();
	}
	
	@Override
	public void reload() {
		// TODO Auto-generated method stub
		super.reload();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		RestConfig resourceConfig = new RestConfig();
		super.getServletContext().addListener(listener);
		super.reload( resourceConfig);
	}


	@ApplicationPath(S_CONTEXT_PATH)
	private class RestConfig extends ResourceConfig {

		//Loading classes is the safest way...
		//in equinox the scanning of packages may not work
		private RestConfig() {
			try {
				register( AuthenticationResource.class );
				register( AdminResource.class );
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
		}
	}
}