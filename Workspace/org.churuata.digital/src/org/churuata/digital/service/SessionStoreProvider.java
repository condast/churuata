package org.churuata.digital.service;

import javax.servlet.http.HttpSession;

import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.store.SessionStore;
import org.condast.commons.persistence.core.ISessionStoreFactory;
import org.osgi.service.component.annotations.Component;

@Component( name="org.churuata.digital.session.store.provider",
			immediate=true)
public class SessionStoreProvider implements ISessionStoreFactory<HttpSession, SessionStore>{

	private Dispatcher dispatcher=  Dispatcher.getInstance();
	public SessionStoreProvider() {
		super();
	}
	
	@Override
	public SessionStore createSessionStore(HttpSession session) {
		return dispatcher.createSessionStore(session);
	}
	
	@Override
	public SessionStore getSessionStore(HttpSession session) {
		return dispatcher.getSessionStore(session);
	}
}