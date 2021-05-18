package org.satr.arnac.authentication.ds;

import javax.servlet.http.HttpSession;

import org.condast.commons.authentication.session.DefaultSessionStore;
import org.condast.commons.persistence.core.ISessionStoreFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.satr.arnac.authentication.core.Dispatcher;

@Component
public class SessionStoreClient {

	private Dispatcher dispatcher = Dispatcher.getInstance();
	
	public SessionStoreClient() {}

	@Reference( cardinality = ReferenceCardinality.AT_LEAST_ONE,
			policy=ReferencePolicy.DYNAMIC)
	public synchronized void setProvider(ISessionStoreFactory<HttpSession, DefaultSessionStore>  provider) {
		dispatcher.setSessionStore(provider);
	}

	public synchronized void unsetProvider(ISessionStoreFactory<HttpSession, DefaultSessionStore> provider) {
		dispatcher.removeSessionStore( null);
	}
}
