package org.churuata.digital.authentication.ds;

import javax.servlet.http.HttpSession;

import org.churuata.digital.authentication.core.Dispatcher;
import org.churuata.digital.core.store.SessionStore;
import org.condast.commons.persistence.core.ISessionStoreFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component
public class SessionStoreClient {

	private static final String S_CHURUATA_ID = "org.churuata.digital"; 

	private Dispatcher dispatcher = Dispatcher.getInstance();
	
	public SessionStoreClient() {}

	@Reference( cardinality = ReferenceCardinality.AT_LEAST_ONE,
			policy=ReferencePolicy.DYNAMIC)
	public synchronized void setProvider(ISessionStoreFactory<HttpSession, SessionStore>  provider) {
		if( S_CHURUATA_ID.equals(provider.getId()))
			dispatcher.setSessionStore(provider);
	}

	public synchronized void unsetProvider(ISessionStoreFactory<HttpSession, SessionStore> provider) {
		dispatcher.removeSessionStore( null);
	}
}
