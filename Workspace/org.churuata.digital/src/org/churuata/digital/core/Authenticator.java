package org.churuata.digital.core;

import org.condast.commons.authentication.service.IAuthenticator;
import org.osgi.service.component.annotations.Component;

@Component(
		name = "org.churuata.digital.authenticator",
		immediate=true
)
public class Authenticator implements IAuthenticator{

	private static final String S_CHURUATA = "churuata";
	private static final String S_WEB = S_CHURUATA + "/web";
	private static final String S_REST = S_CHURUATA + "/rest/";

	public Authenticator() {
	}

	@Override
	public boolean verify(String id, String token) {
		return S_CHURUATA.equals(id);
	}

	@Override
	public boolean supportPath(String path) {
		String page = "/" + S_CHURUATA; 
		return path.startsWith( page );
	}

	@Override
	public boolean filter(String path) {
		String page = "/" + S_CHURUATA; 
		return path.endsWith(page) || path.startsWith(S_WEB) || path.contains(S_REST);
	}	
}
