package org.churuata.digital.http;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.messaging.http.IHttpRequest.HttpStatus;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.parser.AbstractResourceParser;
import org.condast.commons.parser.AbstractResourceParser.Attributes;

public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//same as alias in plugin.xml
	public static final String S_CHURUATA = "churuata/";
	public static final String S_CONTEXT_PATH = S_CHURUATA + "index";

	public static final String S_RESOURCE_FILE = "/resources/active.html";

	private enum Pages{
		ENTRY,
		CONTACT,
		ORGANISATION,
		SERVICES,
		LEGAL,
		CONFIRMATION;

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

	public RegisterServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String tokenstr = req.getParameter(IDomainProvider.Attributes.TOKEN.toAttribute());
		String selectstr = req.getParameter(Attributes.SELECT.toAttribute());
		Pages active = Pages.ENTRY;
		if(StringUtils.isEmpty(selectstr)) {
			resp.sendError(HttpStatus.UNAUTHORISED.getStatus());
			return;
		}
		long token = -1;
		active = Pages.getPage(selectstr);		
		String str =null;
		try{
			HttpSession session = req.getSession( true);
			switch( active ) {
			case ENTRY:
				Random random = new Random();
				token = Math.abs( random.nextLong() );	
				session.setAttribute(IDomainProvider.Attributes.TOKEN.toAttribute(), token);
				break;
			default:
				token = Long.parseLong(tokenstr);
				long check = (long) session.getAttribute(IDomainProvider.Attributes.TOKEN.toAttribute());
				if(token != check ) {
					resp.sendError(HttpStatus.UNAUTHORISED.getStatus());
					return;
				}
				break;
			}

			FileParser parser = new FileParser( token, active );
			str = parser.parse( this.getClass().getResourceAsStream(S_RESOURCE_FILE) );
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
		resp.getWriter().write( str );
	}

	private class FileParser extends AbstractResourceParser{

		private Pages active;

		private long token;
		
		public FileParser( long token, Pages active) {
			super();
			this.token = token;
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
				result = "Churuata Digital; Register a Service";
				break;
			case PAGE:
				result = "Churuata Digital; Register a Service";
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
				String href = "/churuata/register?token=" + token + "&select=" + page.toString();
				builder.append(super.addLink(href, StringStyler.prettyString( page.name())));
			}
			return builder.toString();
		}
	
		@Override
		protected String onCreateFrame(Attributes attr, String[] arguments) {
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

			String result = Pages.ENTRY.toString();
			if( !IDomainProvider.Attributes.isValid(id))
				return result;
			
			switch( IDomainProvider.Attributes.getAttribute(id)) {
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
			case ENTRY:
				result = StringStyler.xmlStyleString( this.active.name()) + "?"  + 
			IDomainProvider.Attributes.TOKEN.name().toLowerCase() + "=" + token;
				break;
			default:
				break;
			}
			return result;
		}
	}
}
