package org.churuata.digital.authentication;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.condast.commons.authentication.core.AbstractAuthenticationManager;
import org.condast.commons.authentication.core.IAuthenticationManager;
import org.condast.commons.authentication.core.ILoginProvider;
import org.condast.commons.authentication.user.ILoginUser;

public class AuthenticationManager extends AbstractAuthenticationManager<Object> {

	private static final String S_CALLBACK_ID = "COVAID";

	private static AuthenticationManager manager = new AuthenticationManager();
	
	private ILoginProvider provider = AuthenticationDispatcher.getInstance();

	private AuthenticationManager() {
		super(S_CALLBACK_ID);
	}

	public static IAuthenticationManager<Object> getInstance() {
		return manager;
	}
	
	@Override
	protected LoginModule getLoginModule(Subject arg0, LoginException arg1) {
		ChuruataLoginModule module = new ChuruataLoginModule();
		module.initialise();
		return module;
	}

	
	
	@Override
	protected void refresh(boolean logout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onLogout(Object userData) {
		// Check if the user is already logged off
		if( getData() == null )
			return;
		ILoginUser user = null;//getData().getUser();
		setData(userData);
		provider.logout( user.getId(), user.getToken() );
	}	
}
