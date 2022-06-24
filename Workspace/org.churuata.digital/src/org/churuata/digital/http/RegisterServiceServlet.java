package org.churuata.digital.http;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.churuata.digital.core.Entries;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.messaging.http.IHttpRequest.HttpStatus;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.parser.AbstractResourceParser;
import org.condast.commons.parser.AbstractResourceParser.Attributes;

public class RegisterServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//same as alias in plugin.xml
	public static final String S_CHURUATA = "churuata/";
	public static final String S_CONTEXT_PATH = S_CHURUATA + "index";

	public static final String S_RESOURCE_FILE = "/resources/active.html";

	public enum Pages{
		HOME,
		REGISTER,
		CONTACT,
		ORGANISATION,
		SERVICES,
		LEGAL,
		CONFIRMATION;

		public Pages next(){
			int ordinal = (ordinal()+1)%values().length;
			return values()[ ordinal ];
		}

		@Override
		public String toString() {
			return StringStyler.xmlStyleString(name());
		}
				
		public static Pages getPage( String str ){
			return Pages.valueOf( StringStyler.styleToEnum(str));
		}
		
		public static String toHref( Pages page ) {
			return Entries.Pages.REGISTER_SERVICE.toPath() + "?session=" + page.toString();
		}
	}

	public RegisterServiceServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String tokenstr = req.getParameter(IDomainProvider.Attributes.TOKEN.toAttribute());
		String selectstr = req.getParameter(Attributes.SELECT.toAttribute());
		Pages active = Pages.REGISTER;
		if(StringUtils.isEmpty(selectstr)) {
			resp.sendError(HttpStatus.UNAUTHORISED.getStatus());
			return;
		}
		long token = -1;
		String str =null;
		try{
			active = Pages.getPage(selectstr);		
			HttpSession session = req.getSession( true);
			switch( active ) {
			case REGISTER:
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
			String href = "/churuata";
			for( Pages page: Pages.values()) {
				switch( page ) {
				case HOME:
					break;
				default:
					href += "/register?token=" + token + "&select=" + page.toString();
					break;
				}
				builder.append(super.addLink(href, StringStyler.prettyString( page.name())));
			}
			return builder.toString();
		}
	
		@Override
		protected String onCreateFrame(Attributes attr, String[] arguments) {
			StringBuilder builder = new StringBuilder();
			builder.append("/churuata/");
			switch( active ) {
			case REGISTER:
				builder.append( Pages.REGISTER.toString() );
				break;
			default:
				builder.append( active );
				break;
			}
			builder.append("?token='" + token);
			return builder.toString();
		}
	}
}
