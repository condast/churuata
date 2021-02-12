//@See: http://blog.vogella.com/2017/04/20/access-osgi-services-via-web-interface/
package org.churuata.digital.http;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

//@Component( immediate=true,
//	    property = "osgi.http.whiteboard.filter.pattern=*churuata*",
//	    scope=ServiceScope.SINGLETON)
public class MapFilter implements Filter {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) arg0;
		HttpServletResponse res = (HttpServletResponse) arg1;
		logger.info("filtering: " + req.getPathInfo());
		arg2.doFilter(arg0, arg1);
	}

	@Override
	public void destroy() {
		logger.info("destroying");
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		logger.info("init filter");
	}
}
