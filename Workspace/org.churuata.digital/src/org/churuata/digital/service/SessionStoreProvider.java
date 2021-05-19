package org.churuata.digital.service;

import javax.servlet.http.HttpSession;

import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.store.SessionStore;
import org.condast.commons.persistence.core.ISessionStoreFactory;
import org.osgi.service.component.annotations.Component;

@Component( name="org.churuata.digital.session.store.provider",
			immediate=true)
public class SessionStoreProvider implements ISessionStoreFactory<HttpSession, SessionStore>{

	private static final String S_CHURUATA_ID = "org.churuata.digital"; 

	private Dispatcher dispatcher=  Dispatcher.getInstance();
	
	public SessionStoreProvider() {
		super();
	}
	
	@Override
	public String getId() {
		return S_CHURUATA_ID;
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