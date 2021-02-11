package org.churuata.digital.core;

import javax.servlet.http.HttpSession;

import org.churuata.digital.core.store.SessionStore;
import org.condast.commons.persistence.core.ISessionStoreFactory;

public class Dispatcher {

	private ISessionStoreFactory<HttpSession, SessionStore> provider;
	
	private static Dispatcher dispatcher = new Dispatcher();
	
	public Dispatcher() {
		super();
	}

	public static Dispatcher getInstance() {
		return dispatcher;
	}

	
	public ISessionStoreFactory<HttpSession, SessionStore> getProvider() {
		return provider;
	}

	public void setSessionStore(ISessionStoreFactory<HttpSession, SessionStore> provider) {
		this.provider = provider;
	}

	public void removeSessionStore(Object object) {
		this.provider = null;
	}
}
