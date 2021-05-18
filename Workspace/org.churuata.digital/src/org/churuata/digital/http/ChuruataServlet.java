package org.churuata.digital.http;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.churuata.digital.core.Dispatcher;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.strings.StringUtils;
import org.condast.js.commons.parser.AbstractResourceParser;

public class ChuruataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//same as alias in plugin.xml
	public static final String S_CHURUATA = "churuata/";
	public static final String S_CONTEXT_PATH = S_CHURUATA + "index";
	public static final String S_LOGIN = "Login";
	public static final String S_LOGOFF = "Logoff";

	public static final String S_RESOURCE_FILE = "/resources/index.html";
	
	private long token;

	public ChuruataServlet() {
		token = -1;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String tokenstr = req.getParameter(ILoginUser.Attributes.TOKEN.name().toLowerCase());
		if(!StringUtils.isEmpty(tokenstr))
			token = Long.parseLong(tokenstr);
		FileParser parser = new FileParser();
		String str = parser.parse( this.getClass().getResourceAsStream(S_RESOURCE_FILE) );
		resp.getWriter().write( str );
	}

	private class FileParser extends AbstractResourceParser{

		Dispatcher dispatcher = Dispatcher.getInstance();

		@Override
		protected String onHandleLabel(String id, Attributes attr) {
			return dispatcher.hasLoginUser( token )?S_LOGOFF: S_LOGIN;
		}

		@Override
		protected String onCreateLink(String link, String url, String arguments) {
			boolean user = dispatcher.hasLoginUser( token );
			String path = S_CHURUATA;
			if( !user ) {
				Random random = new Random();
				token = random.nextLong();
				path += S_LOGIN.toLowerCase() + "?" + ILoginUser.Attributes.TOKEN.name().toLowerCase() + "=" + token;
			}else {
				path += S_LOGOFF.toLowerCase() + "?" + ILoginUser.Attributes.TOKEN.name().toLowerCase() + "=" + token;				
			}
			return path;
		}		
	}
}
