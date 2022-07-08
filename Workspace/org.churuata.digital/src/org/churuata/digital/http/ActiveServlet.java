package org.churuata.digital.http;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.churuata.digital.core.AuthenticationDispatcher;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.session.SessionStore;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.IAdmin;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.messaging.http.IHttpRequest.HttpStatus;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.parser.AbstractResourceParser;

public class ActiveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String S_RESOURCE_FILE = "/resources/active.html";

	private static final String S_ACCOUNT = "account";

	private enum Pages{
		LOGOFF,
		PROFILE,
		ACCEPTANCE,
		ADMIN;

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


	public ActiveServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String tokenstr = req.getParameter(IDomainProvider.Attributes.TOKEN.toAttribute());
		AuthenticationDispatcher authentication = AuthenticationDispatcher.getInstance();
		ILoginUser user = null;
		
		//either enter through the login entry point, or see if login is attempted through a REST call
		long token = -1;
		if( !StringUtils.isEmpty(tokenstr)) {
			token = Long.parseLong(tokenstr);
			Dispatcher dispatcher = Dispatcher.getInstance();
			IDomainProvider<SessionStore<OrganisationData>> provider = dispatcher.getDomain(token);
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
					Dispatcher.createDomain(user);
				}
			}
		}
		if( user == null ) {
			resp.setStatus( HttpStatus.UNAUTHORISED.getStatus());
			return;
		}
		Pages active = Pages.PROFILE;
		FileParser parser = new FileParser( active, token, user );
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
		
		private ILoginUser user;
		private long token;
		private Pages active;
		
		public FileParser(Pages active, long token, ILoginUser user) {
			super();
			this.token = token;
			this.user = user;
			this.active = active;
		}

		@Override
		protected String getToken() {
			return String.valueOf(token);
		}

		@Override
		protected String onHandleTitle(String subject, Attributes attr) {
			String result = null;
			switch( attr ){
			case HTML:
				result = "Churuata Digital";
				break;
			case PAGE:
				result = "Churuata Digital";
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		protected String onCreateList(String[] arguments) {
			IAdmin admin = dispatcher.getAdmin(user);
			StringBuilder builder = new StringBuilder();
			for( Pages page: Pages.values()) {
				StringBuilder href = new StringBuilder();
				href.append("/churuata/");
				boolean activePage = active.equals(page); 
				switch( page ) {
				case ACCEPTANCE:
					if( admin == null )
						break;
					href.append( page.toString());
					href.append("?token=" + token + "&select=" + page.toString());
					builder.append(super.addLink(href.toString(), StringStyler.prettyString( page.name()), activePage));
					break;
				case ADMIN:
					if( admin == null )
						break;
					href.append( page.toString());
					href.append("?token=" + token + "&select=" + page.toString());
					builder.append(super.addLink(href.toString(), StringStyler.prettyString( page.name()), activePage));
					break;
				case LOGOFF:
					href.append(page.toString());
					builder.append(super.addLink(href.toString(), StringStyler.prettyString( page.name()), activePage));
					break;
				default:
					href.append( page.toString());
					href.append("?token=" + token + "&select=" + S_ACCOUNT);
					builder.append(super.addLink(href.toString(), StringStyler.prettyString( page.name()), activePage));
					break;
				}
			}
			return builder.toString();
		}

		@Override
		protected String onCreateFrame(Attributes attr, String[] arguments) {
			StringBuilder builder = new StringBuilder();
			builder.append("/churuata/ready?token=" + token + "&user-id=" + user.getId() + "'");
			return builder.toString();
		}

		@Override
		protected String onHandleLabel(String id, Attributes attr) {
			String result = null;
			if( Pages.isValid(id)) {
				Pages page = Pages.getPage(id);
				switch( page) {	
				case LOGOFF:
					result = page.toString();
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
				result = "user-id=" + user.getId();
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
			case LOGOFF:
				result = page.toString() + "?"  +
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
