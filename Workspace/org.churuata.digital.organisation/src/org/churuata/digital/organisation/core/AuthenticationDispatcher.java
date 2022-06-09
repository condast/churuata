package org.churuata.digital.organisation.core;

import org.condast.commons.authentication.core.AbstractLoginClient;

public class AuthenticationDispatcher extends AbstractLoginClient{

	private static AuthenticationDispatcher dispatcher = new AuthenticationDispatcher();
		
	public AuthenticationDispatcher() {
		super();
	}

	public static AuthenticationDispatcher getInstance(){
		return dispatcher;
	}
}