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
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.messaging.http.IHttpRequest.HttpStatus;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.parser.AbstractResourceParser;
import org.condast.commons.parser.AbstractResourceParser.Attributes;

public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//same as alias in plugin.xml
	public static final String S_CHURUATA = "churuata/";
	public static final String S_CONTEXT_PATH = S_CHURUATA + "index";
	public static final String S_LOGOFF = "Logoff";

	public static final String S_RESOURCE_FILE = "/resources/active.html";

	private enum Pages{
		ACTIVE,
		LOGOFF,
		ACCOUNT,
		ADDRESS,
		ORGANISATION,
		SERVICES;

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

	public ProfileServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String tokenstr = req.getParameter(IDomainProvider.Attributes.TOKEN.toAttribute());
		String selectstr = req.getParameter(Attributes.SELECT.toAttribute());
		Pages active = Pages.ACCOUNT;
		if(!StringUtils.isEmpty(selectstr)) {
			active = Pages.getPage(selectstr);
			
		}
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
		FileParser parser = new FileParser( user, active, token );
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

		private ILoginUser user;
		private long token;
		private Pages active;

		public FileParser( ILoginUser user, Pages active, long token) {
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
				result = "Churuata Digital: Profile";
				break;
			case PAGE:
				result = "Churuata Digital: Edit Profile";
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		protected String onCreateList(String[] arguments) {
			StringBuilder builder = new StringBuilder();
			for( Pages page: Pages.values()) {
				StringBuilder href = new StringBuilder();
				href.append("/churuata/");
				switch( page ) {
				case LOGOFF:
					href.append(page.toString());
					break;
				default:
					href.append("profile");
					break;
				}
				href.append("?token=" + token + "&select=" + page.toString());
				builder.append(super.addLink(href.toString(), StringStyler.prettyString( page.name())));
			}
			return builder.toString();
		}
	
		@Override
		protected String onCreateFrame( Attributes attr, String[] arguments) {
			StringBuilder builder = new StringBuilder();
			builder.append("/churuata/" + active + "?token='" + token);
			return builder.toString();
		}
			
		@Override
		protected String onHandleLabel(String id, Attributes attr) {
			String result = null;
			switch( attr ) {
			case ACTIVE:
				if( !Pages.isValid(id))
					return null;
				
				Pages page = Pages.getPage(id);
				result = page.equals(active)?id:"";
				break;
			case KEY:
				result = handleKeyLabel(id);
				break;
			default:
				result = handleKeyLabel(id);
				break;
			}
			return result;
		}

		protected String handleKeyLabel(String id) {
			if( Pages.isValid(id)) {
				Pages page = Pages.getPage(id);
				return StringStyler.prettyString(page.name());
			}

			String result = S_LOGOFF;
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
		protected String onCreateLink(String link, String id, String arguments) {
			if( !Pages.isValid(id))
				return null;
			Pages page = Pages.getPage(id);
			String result = null;
			switch( page ) {
			case ACTIVE:
				result = StringStyler.xmlStyleString( this.active.name()) + "?"  +
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
