package org.churuata.digital.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.messaging.http.IHttpRequest.HttpStatus;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.parser.AbstractResourceParser;

public class ActiveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//same as alias in plugin.xml
	public static final String S_CHURUATA = "churuata/";
	public static final String S_CONTEXT_PATH = S_CHURUATA + "index";
	public static final String S_LOGIN = "Login";
	public static final String S_LOGOFF = "Logoff";

	public static final String S_RESOURCE_FILE = "/resources/active.html";

	private enum Pages{
		ACTIVE,
		LOG,
		LOGIN,
		OVERVIEW,
		SYSTEM;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString(name());
		}
	}

	private long userId;
	private long token;

	public ActiveServlet() {
		super();
		token = -1;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userstr = req.getParameter(IDomainProvider.Attributes.USER_ID.name().toLowerCase());
		String tokenstr = req.getParameter(IDomainProvider.Attributes.TOKEN.name().toLowerCase());
		if(StringUtils.isEmpty(userstr) || StringUtils.isEmpty(tokenstr)) {
			resp.setStatus( HttpStatus.BAD_REQUEST.getStatus());
			return;
		}
		token = Long.parseLong(tokenstr);
		userId = Long.parseLong(userstr);
		
		FileParser parser = new FileParser();
		String str = parser.parse( this.getClass().getResourceAsStream(S_RESOURCE_FILE) );
		resp.getWriter().write( str );
	}

	private class FileParser extends AbstractResourceParser{

		@Override
		protected String onHandleLabel(String id, Attributes attr) {
			String result = S_LOGOFF;
			if( !IDomainProvider.Attributes.isValid(id))
				return result;
			
			switch( IDomainProvider.Attributes.getAttribute(id)) {
			case USER_ID:
				result = "userid=" + userId;
				break;
			case TOKEN:
				result = "token=" + token;
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		protected String onCreateLink(String link, String page, String arguments) {
			String result = null;
			switch( Pages.valueOf(StringStyler.styleToEnum(page))) {
			case LOGIN:
				result = S_LOGOFF.toLowerCase() + "?"  + 
						IDomainProvider.Attributes.USER_ID.name().toLowerCase() + "=" + userId + IDomainProvider.Attributes.TOKEN.name().toLowerCase() + "=" + token;				
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		protected String getToken() {
			// TODO Auto-generated method stub
			return null;
		}		
	}
}
