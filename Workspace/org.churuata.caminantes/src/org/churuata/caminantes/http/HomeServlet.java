package org.churuata.caminantes.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String S_HOME_PATH = "/caminantes/web/en/index.html";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Content-Disposition","inline; filename=Los Caminantes");
		resp.sendRedirect(req.getContextPath() + S_HOME_PATH);
	}
}
