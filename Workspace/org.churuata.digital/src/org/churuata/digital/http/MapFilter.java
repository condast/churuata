//@See: http://blog.vogella.com/2017/04/20/access-osgi-services-via-web-interface/
package org.churuata.digital.http;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.churuata.digital.core.rest.IRestPages;
import org.condast.commons.strings.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;

@Component(scope=ServiceScope.PROTOTYPE,
property= MapFilter.S_OSGI_FILTER_PATTERN)
@HttpWhiteboardFilterPattern( MapFilter.S_CONTEXT_PATH)
public class MapFilter implements Filter {

	public static final String S_CONTEXT_PATH = "/churuata/*";
	public static final String S_OSGI_FILTER_PATTERN = "osgi.http.whiteboard.filter.pattern=" + S_CONTEXT_PATH;

	private static final String S_LOCAL_HOST = "127.0.0.1";
	protected static final String S_CHURUATA = "/churuata";
	private static final String S_REST_SERVICE = S_CHURUATA + "/rest/";
	
	private static final String S_REFUGEE_MAP = S_CHURUATA + "/map";
	private static final String S_REFUGEE_BANNER = S_CHURUATA + "/banner";

	private static final String S_ERR_ILLEGAL_ACCESS = "This page cannot be accessed.";

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private Map<String, HttpSession> addresses;

	public MapFilter() {
		super();
		addresses = new HashMap<>();
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) arg0;
		String path = req.getRequestURI();
		if( StringUtils.isEmpty(path))
			return;
		
		//Pass calls that are not for this application
		if(!path.contains(S_CHURUATA)) {
			arg2.doFilter(arg0, arg1);
			return;			
		}

		//Pass the map and the banner
		if(path.contains( S_REFUGEE_MAP) || path.contains( S_REFUGEE_BANNER )) {
			arg2.doFilter(arg0, arg1);
			return;			
		}


		//Pass the REST services
		if(path.contains( IRestPages.S_CHURUATA_CONTEXT_PATH)) {
			arg2.doFilter(arg0, arg1);
			return;			
		}

		//Filter local calls or a call to index
		String remote = req.getRemoteAddr();
		HttpSession session = null;
		if( S_LOCAL_HOST.equals(req.getRemoteAddr()) || path.endsWith(S_CHURUATA) || path.contains(S_REST_SERVICE)) {
			if(!addresses.containsKey(remote)) {
				session = req.getSession(true);
				addresses.put(remote, session);
			}
			arg2.doFilter(arg0, arg1);
			return;
		}

		if( path.startsWith(S_CHURUATA) && addresses.containsKey(remote)) {
			arg2.doFilter(arg0, arg1);
			return;
		}
		

		//The request fails on the conditions described above. See if these are follow up calls,
		//and allow them if so
		session = req.getSession(false);
		if( session != null ) {
			arg2.doFilter(arg0, arg1);
			return;			
		}
		Writer writer = arg1.getWriter();
		writer.write(S_ERR_ILLEGAL_ACCESS + "=> " + remote + ": " + path);
		logger.info(path);
		arg2.doFilter(arg0, arg1);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
