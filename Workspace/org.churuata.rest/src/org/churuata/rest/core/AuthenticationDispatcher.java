package org.churuata.rest.core;

import org.condast.commons.data.user.IAdmin;
import org.condast.commons.data.user.ILoginUser;
import org.condast.commons.jpa.authentication.core.AbstractLoginClient;
import org.condast.commons.jpa.authentication.core.ILoginProvider;

public class AuthenticationDispatcher extends AbstractLoginClient{

	private static AuthenticationDispatcher dispatcher = new AuthenticationDispatcher();
	
	public static AuthenticationDispatcher getInstance(){
		return dispatcher;
	}

	public IAdmin getAdmin(ILoginUser user) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setLoginProvider(ILoginProvider provider) {
		// TODO Auto-generated method stub
		
	}

	public void unsetLoginProvider(ILoginProvider provider) {
		// TODO Auto-generated method stub
		
	}
}