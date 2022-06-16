package org.churuata.digital.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.churuata.digital.BasicApplication;
import org.condast.commons.parser.AbstractResourceParser;

public class ChuruataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//same as alias in plugin.xml
	public static final String S_CHURUATA = "churuata";

	public static final String S_LOGIN = "Login";

	public static final String S_PATH = "path";
	public static final String S_ACTIVE = "active";

	public static final String S_RESOURCE_FILE = "/resources/index.html";
	
	public ChuruataServlet() {/* NOTHING */ }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		FileParser parser = new FileParser();
		String str = parser.parse( this.getClass().getResourceAsStream(S_RESOURCE_FILE) );
		resp.getWriter().write( str );
	}

	private class FileParser extends AbstractResourceParser{

		@Override
		protected String getToken() {
			return String.valueOf(-1);
		}

		@Override
		protected String onHandleLabel(String id, Attributes attr) {
			return S_LOGIN;
		}

		@Override
		protected String onCreateLink(String link, String url, String arguments) {
			return S_CHURUATA + BasicApplication.Pages.LOGIN.toString();
		}
	}
}
