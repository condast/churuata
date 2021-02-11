package org.churuata.digital.core;

import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.http.HttpSession;
import org.churuata.digital.core.location.Churuata;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruataCollection;
import org.churuata.digital.core.store.SessionStore;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.persistence.core.AbstractSessionPersistence;
import org.condast.commons.persistence.core.ISessionStoreFactory;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.UISession;

public class Dispatcher implements IChuruataCollection, ISessionStoreFactory<HttpSession, SessionStore>  {

	private static Dispatcher dispatcher = new Dispatcher();
	
	private Collection<Churuata> churuatas;
	
	private SessionPersistence persistence;

	public Dispatcher() {
		super();
		churuatas = new ArrayList<>();
		persistence = new SessionPersistence();
	}

	public static Dispatcher getInstance() {
		return dispatcher;
	}
	
	public SessionPersistence getPersistence() {
		return persistence;
	}

	@Override
	public boolean contains( IChuruata churuata ) {
		return this.churuatas.contains(churuata);
	}
	
	@Override
	public boolean addChuruata(  Churuata churuata ) {
		return this.churuatas.add(churuata);
	}

	@Override
	public boolean removeChuruata(  IChuruata churuata ) {
		return this.churuatas.remove(churuata);
	}
	
	@Override
	public Churuata[] getChuruatas() {
		return this.churuatas.toArray( new Churuata[ this.churuatas.size()]);
	}

	@Override
	public IChuruata[] getChuruatas(LatLng latlng, int distance) {
		Collection<Churuata> results = new ArrayList<>();
		for( Churuata churuata: this.churuatas) {
			if( LatLngUtils.distance( churuata.getLocation(), latlng) < distance )
				results.add(churuata);
		}
		return results.toArray( new Churuata[ results.size() ]);
	}

	@Override
	public SessionStore createSessionStore(HttpSession session) {
		return persistence.createSessionStore(session);
	}

	public SessionStore initSessionStore( UISession uiSession ) {
		return persistence.initStore(uiSession);
	}
	
	private class SessionPersistence extends AbstractSessionPersistence<SessionStore>{

		@Override
		protected SessionStore createPersistence(HttpSession session) {
			SessionStore store = new SessionStore();
			return store;
		}

		public SessionStore initStore( UISession uiSession ) {
			HttpSession session = uiSession.getHttpSession();
			uiSession.addUISessionListener(e->updateSession(session));
			return super.createSessionStore(session);			
		}	
	}			
}
