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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

@Component( immediate=true,
	    property = HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN + "=/*",
	    scope=ServiceScope.SINGLETON)
public class MapFilter implements Filter {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		logger.info("filtering");
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
