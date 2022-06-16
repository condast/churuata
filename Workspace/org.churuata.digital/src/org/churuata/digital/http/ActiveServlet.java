package org.churuata.digital.http;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.churuata.digital.BasicApplication;
import org.churuata.digital.core.AuthenticationDispatcher;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.session.SessionStore;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
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
		PROFILE,
		SYSTEM;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString(name());
		}
		
		public static boolean isValid( String str ) {
			String test = StringStyler.styleToEnum(str);
			for( Pages attr: values()) {
				if( attr.name().equals(test))
					return true;
			}
			return false;
		}
		
		public static Pages getPage( String str ){
			return Pages.valueOf( StringStyler.styleToEnum(str));
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
		String tokenstr = req.getParameter(IDomainProvider.Attributes.TOKEN.toAttribute());
		AuthenticationDispatcher authentication = AuthenticationDispatcher.getInstance();
		ILoginUser user = null;
		
		//either enter through the login entry point, or see if login is attempted through a REST call
		if( !StringUtils.isEmpty(tokenstr)) {
			token = Long.parseLong(tokenstr);
			Dispatcher dispatcher = Dispatcher.getInstance();
			IDomainProvider<SessionStore> provider = dispatcher.getDomain(token);
			if(( provider != null ) && ( provider.getData() != null ))
				user = provider.getData().getLoginUser();
		}
		if( user == null ) {
			String userstr = req.getParameter( IDomainProvider.Attributes.USER_ID.toAttribute() );
			String securitystr = req.getParameter( IDomainProvider.Attributes.SECURITY.toAttribute());
			if( !StringUtils.isEmpty(userstr) && !StringUtils.isEmpty( securitystr )) {
				long userId = Long.parseLong(userstr);
				long security = Long.parseLong( securitystr);
				Random random = new Random();
				token = Math.abs( random.nextLong() );
				user = authentication.getLoginUser(userId, security);
				if( user != null ) {
					Dispatcher.createDomain(user, token, BasicApplication.Pages.ACTIVE.name().toLowerCase());
				}
			}
		}
		if( user == null ) {
			resp.setStatus( HttpStatus.UNAUTHORISED.getStatus());
			return;
		}
		userId = user.getId();
		FileParser parser = new FileParser( token );
		String str =null;
		try{
			str = parser.parse( this.getClass().getResourceAsStream(S_RESOURCE_FILE) );
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
		resp.getWriter().write( str );
	}

	private class FileParser extends AbstractResourceParser{

		AuthenticationDispatcher dispatcher = AuthenticationDispatcher.getInstance();
		private long token;
		
		public FileParser(long token) {
			super();
			this.token = token;
		}

		@Override
		protected String getToken() {
			return String.valueOf(token);
		}

		@Override
		protected String onHandleLabel(String id, Attributes attr) {
			String result = S_LOGOFF;
			if( Pages.isValid(id)) {
				Pages page = Pages.getPage(id);
				switch( page) {	
				case LOGIN:
					result = S_LOGOFF;
					break;
				default:
					result = StringStyler.prettyString(page.name());
					break;
				}
			}

			if( !IDomainProvider.Attributes.isValid(id))
				return result;
			switch( IDomainProvider.Attributes.getAttribute(id)) {
			case USER_ID:
				result = "user-id=" + userId;
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
			ILoginUser user = dispatcher.getLoginUser(token);
			switch( Pages.valueOf(StringStyler.styleToEnum(page))) {
			case LOGIN:
				result = S_LOGOFF.toLowerCase() + "?"  +
						ILoginUser.Attributes.USERNAME.name().toLowerCase() + "=" + user.getUserName() + 
						IDomainProvider.Attributes.TOKEN.name().toLowerCase() + "=" + token;
				break;
			case PROFILE:
				result = page.toLowerCase() + "?"  +
						ILoginUser.Attributes.USERNAME.name().toLowerCase() + "=" + user.getUserName() + 
						IDomainProvider.Attributes.TOKEN.name().toLowerCase() + "=" + token;
				break;
			default:
				break;
			}
			return result;
		}
	}
}
