package org.churuata.rest.core;

import org.condast.commons.authentication.core.AbstractLoginClient;
import org.condast.commons.authentication.user.IAdmin;
import org.condast.commons.authentication.user.ILoginUser;

public class AuthenticationDispatcher extends AbstractLoginClient{

	private static AuthenticationDispatcher dispatcher = new AuthenticationDispatcher();
	
	public static AuthenticationDispatcher getInstance(){
		return dispatcher;
	}

	@Override
	public IAdmin getAdmin(ILoginUser user) {
		// TODO Auto-generated method stub
		return null;
	}
}