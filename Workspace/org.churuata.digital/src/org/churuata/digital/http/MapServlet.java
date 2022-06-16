package org.churuata.digital.http;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.churuata.digital.core.AuthenticationDispatcher;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.parser.AbstractResourceParser;

public class MapServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//same as alias in plugin.xml
	public static final String S_CHURUATA = "churuata";
	public static final String S_CONTEXT_PATH = S_CHURUATA + "/index";

	public static final String S_CONDAST = "condast/";
	public static final String S_CONDAST_CONTEXT = S_CONDAST + "auth/";

	public static final String S_LOGIN = "Login";
	public static final String S_LOGOFF = "Logoff";

	public static final String S_PATH = "path";
	public static final String S_ACTIVE = "active";

	public static final String S_RESOURCE_FILE = "/resources/map.html";
	
	private long token;

	public MapServlet() {
		token = -1;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		FileParser parser = new FileParser();
		String str = parser.parse( this.getClass().getResourceAsStream(S_RESOURCE_FILE) );
		resp.getWriter().write( str );
	}

	private class FileParser extends AbstractResourceParser{

		AuthenticationDispatcher dispatcher = AuthenticationDispatcher.getInstance();

		@Override
		protected String onHandleLabel(String id, Attributes attr) {
			return null;//dispatcher.hasLoginUser( token )?S_LOGOFF: S_LOGIN;
		}

		@Override
		protected String onCreateLink(String link, String url, String arguments) {
			boolean user = false;// dispatcher.hasLoginUser( token );
			String path = S_CONDAST_CONTEXT;
			if( !user ) {
				Random random = new Random();
				token = Math.abs( random.nextLong() );
				path += getPath( S_LOGIN, S_CHURUATA, token, S_ACTIVE);
			}else {
				path += getPath( S_LOGOFF, S_CHURUATA, token, S_ACTIVE);				
			}
			return path;
		}

		@Override
		protected String getToken() {
			// TODO Auto-generated method stub
			return null;
		}		
	}
	
	/**
	 * The return path is by definition: {context}/{domain}/{path}
	 * @param login
	 * @param domain
	 * @param token
	 * @param path
	 * @return
	 */
	private static String getPath( String login, String domain, long token, String path ) {
		return login.toLowerCase() + "?" + IDomainProvider.Attributes.TOKEN.name().toLowerCase() + "=" + token +
				 "&" +IDomainProvider.Attributes.DOMAIN.name().toLowerCase() + "=" + domain +
				 "&" + S_PATH + "=" + path;
	}
}
