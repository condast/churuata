package org.churuata.digital.core;

import java.util.HashMap;
import java.util.Map;

import org.churuata.digital.session.SessionStore;
import org.condast.commons.authentication.core.AbstractLoginClient;
import org.condast.commons.authentication.user.ILoginUser;

public class AuthenticationDispatcher extends AbstractLoginClient{

	private Map<ILoginUser, SessionStore> stores;
	
	private static AuthenticationDispatcher dispatcher = new AuthenticationDispatcher();
		
	public AuthenticationDispatcher() {
		super();
		this.stores = new HashMap<>();
	}

	public static AuthenticationDispatcher getInstance(){
		return dispatcher;
	}
	
	public void putUser( ILoginUser user, SessionStore store) {
		this.stores.put(user, store);
	}

	public SessionStore getSessionStore( ILoginUser user) {
		return this.stores.get(user);
	}

	public boolean hasLoginUser( long token) {
		for( ILoginUser user: stores.keySet()) {
			if( user.getSecurity() == token )
				return true;
		}
		return false;
	}

	public ILoginUser getLoginUser( long token) {
		for( ILoginUser user: stores.keySet()) {
			if( user.getSecurity() == token )
				return user;
		}
		return null;
	}

	@Override
	public void logout(ILoginUser user) {
		this.stores.remove(user);
		super.logout(user);
	}
}