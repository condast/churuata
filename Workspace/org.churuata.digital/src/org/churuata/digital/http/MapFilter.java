//@See: http://blog.vogella.com/2017/04/20/access-osgi-services-via-web-interface/
package org.churuata.digital.http;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.condast.commons.strings.StringUtils;

public class MapFilter implements Filter {

	private static final String S_LOCAL_HOST = "127.0.0.1";
	private static final String S_CHURUATA = "/churuata";
	private static final String S_REST_SERVICE = S_CHURUATA + "/rest/";

	private static final String S_ERR_ILLEGAL_ACCESS = "This page cannot be accessed.";

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
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
		
		//Filter local calls or a call to index
		if( S_LOCAL_HOST.equals(req.getRemoteAddr()) || path.endsWith(S_CHURUATA) || path.contains(S_REST_SERVICE)) {
			arg2.doFilter(arg0, arg1);
			return;
		}
		
		//The request fails on the conditions described above. See if these are follow up calls,
		//and allow them if so
		HttpSession session = req.getSession(false);
		if( session != null ) {
			arg2.doFilter(arg0, arg1);
			return;			
		}
		Writer writer = arg1.getWriter();
		writer.write(S_ERR_ILLEGAL_ACCESS);
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
