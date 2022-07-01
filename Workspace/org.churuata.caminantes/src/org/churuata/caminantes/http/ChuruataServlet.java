package org.churuata.caminantes.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.condast.commons.parser.AbstractResourceParser;
import org.condast.commons.strings.StringStyler;

public class ChuruataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//same as alias in plugin.xml
	public static final String S_HOME = "home";
	public static final String S_CAMINANTES = "caminantes";
	public static final String S_LOS_CAMINANTES = "Los Caminantes";

	public static final String S_REGISTER_SERVICE = "Register Service";
	public static final String S_RESOURCE_FILE = "/resources/index.html";

	private enum Pages{
		HOME;
		
		public String getHref( ){
			return "/" + S_CAMINANTES + "/" + toString();
		}

		@Override
		public String toString() {
			return StringStyler.xmlStyleString(name());
		}				
	}

	public ChuruataServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		FileParser parser = new FileParser( Pages.HOME, -1);
		String str = parser.parse( this.getClass().getResourceAsStream(S_RESOURCE_FILE) );
		resp.getWriter().write( str );
	}

	private class FileParser extends AbstractResourceParser{

		private long token;
		private Pages active;
		
		public FileParser(Pages active, long token) {
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
				result = S_LOS_CAMINANTES;
				break;
			case PAGE:
				result = S_LOS_CAMINANTES;
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
				boolean activePage = active.equals(page); 
				switch( page ) {	
				default:
					builder.append(super.addLink(page.getHref(), StringStyler.prettyString( page.name()), activePage));
					break;
				}
			}
			return builder.toString();
		}

		@Override
		protected String onCreateFrame( Attributes attr, String[] arguments) {
			StringBuilder builder = new StringBuilder();
			builder.append("/"+ S_CAMINANTES + "/");
			switch( attr ) {
			case HOME:
				builder.append( S_HOME);
				break;				
			default:
				builder.append(attr.toAttribute());
				break;								
			}
			builder.append("'");
			return builder.toString();
		}
	}
}
