package org.churuata.digital.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.churuata.digital.core.AuthenticationDispatcher;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.js.commons.parser.AbstractResourceParser;

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
		MANUAL,
		NMEA,
		OVERVIEW,
		SYSTEM;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString(name());
		}
	}

	private String userName;
	private long token;

	public ActiveServlet() {
		super();
		token = -1;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.userName = req.getParameter(ILoginUser.Attributes.USERNAME.name().toLowerCase());
		String tokenstr = req.getParameter(ILoginUser.Attributes.TOKEN.name().toLowerCase());
		if(!StringUtils.isEmpty(tokenstr))
			token = Long.parseLong(tokenstr);
		FileParser parser = new FileParser();
		String str = parser.parse( this.getClass().getResourceAsStream(S_RESOURCE_FILE) );
		resp.getWriter().write( str );
	}

	private class FileParser extends AbstractResourceParser{

		AuthenticationDispatcher dispatcher = AuthenticationDispatcher.getInstance();

		@Override
		protected String onHandleLabel(String id, Attributes attr) {
			return dispatcher.hasLoginUser( token )?S_LOGOFF: S_LOGIN;
		}

		@Override
		protected String onCreateLink(String link, String page, String arguments) {
			String result = null;
			switch( Pages.valueOf(StringStyler.styleToEnum(page))) {
			case LOGIN:
				result = S_LOGOFF.toLowerCase() + "?"  + 
						ILoginUser.Attributes.USERNAME.name().toLowerCase() + "=" + userName + ILoginUser.Attributes.TOKEN.name().toLowerCase() + "=" + token;				
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
