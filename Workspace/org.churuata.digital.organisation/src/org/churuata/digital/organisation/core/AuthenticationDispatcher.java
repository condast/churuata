package org.churuata.digital.organisation.core;

import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.data.user.IAdmin;
import org.condast.commons.data.user.ILoginUser;
import org.condast.commons.jpa.authentication.core.AbstractLoginClient;
import org.condast.commons.jpa.authentication.core.AuthenticationEvent;
import org.condast.commons.jpa.authentication.core.IAuthenticationListener;
import org.condast.commons.jpa.authentication.core.ILoginProvider;

public class AuthenticationDispatcher extends AbstractLoginClient{

	private Collection<ILoginUser> users;
	
	private static AuthenticationDispatcher dispatcher = new AuthenticationDispatcher();
	
	private IAuthenticationListener listener = e -> onNotificationEvent(e);

	public AuthenticationDispatcher() {
		super();
		this.users = new ArrayList<>();
	}

	public static AuthenticationDispatcher getInstance(){
		return dispatcher;
	}

	private void onNotificationEvent( AuthenticationEvent event ) {
		try {
			switch( event.getEvent()) {
			case LOGIN:
				login(event.getUser());
				break;
			case LOGOFF:
				logout(event.getUser());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setLoginProvider(ILoginProvider provider) {
		provider.addAuthenticationListener(listener);
	}

	public void unsetLoginProvider(ILoginProvider provider) {
		provider.removeAuthenticationListener(listener);
	}

	public boolean addUser( ILoginUser user) {
		return this.users.add(user);
	}

	private boolean hasLoginUser( ILoginUser user) {
		return this.users.contains(user);
	}

	public ILoginUser getLoginUser( long security) {
		for( ILoginUser user: users) {
			if( user.getSecurity() == security )
				return user;
		}
		return null;
	}

	public void logout(ILoginUser user) {
		this.users.remove(user);
	}
	
	public static void login( ILoginUser user ) {
		if( dispatcher.hasLoginUser(user))
			return;
		dispatcher.addUser(user);
	}

	public IAdmin getAdmin(ILoginUser user) {
		// TODO Auto-generated method stub
		return null;
	}

}