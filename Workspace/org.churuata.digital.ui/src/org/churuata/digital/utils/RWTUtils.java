package org.churuata.digital.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;

public class RWTUtils {

	/**
	 * Redirect the 
	 * @param url
	 * @return
	 */
	public static boolean redirect( String url ){
		Client client = RWT.getClient();
		JavaScriptExecutor executor = client.getService( JavaScriptExecutor.class );
		if( executor == null  )
			return false;
		executor.execute( "window.location = \"" + url + "\";" );
		return true;
	}

	/**
	 * Redirect the 
	 * @param url
	 * @return
	 */
	public static <V extends Object> boolean forward( String url, String key, V value ){
		HttpServletRequest request = RWT.getRequest();
		request.getSession().setAttribute(key, value );
		try {
			RWT.getResponse().sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
