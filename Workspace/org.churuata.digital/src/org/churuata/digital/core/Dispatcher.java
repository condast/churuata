package org.churuata.digital.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import org.churuata.digital.core.location.Churuata;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruataCollection;
import org.churuata.digital.core.store.SessionStore;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.persistence.core.AbstractSessionPersistence;
import org.condast.commons.persistence.core.ISessionStoreFactory;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.provider.ICompositeProvider;
import org.eclipse.swt.widgets.Composite;

public class Dispatcher implements IChuruataCollection, ISessionStoreFactory<HttpSession, SessionStore>  {

	private static Dispatcher dispatcher = new Dispatcher();
	
	private Collection<IChuruata> churuatas;
	
	private SessionPersistence persistence;

	private Map<String,ICompositeProvider<Composite>> composites;
	
	public Dispatcher() {
		super();
		composites = new HashMap<>();
		churuatas = new ArrayList<>();
		persistence = new SessionPersistence();
	}

	public static Dispatcher getInstance() {
		return dispatcher;
	}
	
	public SessionPersistence getPersistence() {
		return persistence;
	}

	public boolean hasLoginUser( long token ) {
		return persistence.hasLoginUser(token);
	}

	public void addEntryPoint(ICompositeProvider<Composite> provider) {
		this.composites.put(provider.getName(), provider);
	}

	public void removeEntryPoint(ICompositeProvider<Composite> provider) {
		this.composites.remove(provider.getName());
	}

	public ICompositeProvider<Composite> getComposite( String name ) {
		if( StringUtils.isEmpty(name))
			return null;
		for( ICompositeProvider<Composite> provider: this.composites.values()) {
			if( name.equals(provider.getName()))
				return provider;
		}
		return null;
	}

	public boolean logoff( ILoginUser user ) {
		return persistence.logoff( user);
	}

	@Override
	public boolean contains( IChuruata churuata ) {
		return this.churuatas.contains(churuata);
	}
	
	@Override
	public boolean addChuruata(  IChuruata churuata ) {
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
		Collection<IChuruata> results = new ArrayList<>();
		for( IChuruata churuata: this.churuatas) {
			if( LatLngUtils.distance( churuata.getLocation(), latlng) < distance )
				results.add(churuata);
		}
		return results.toArray( new Churuata[ results.size() ]);
	}

	@Override
	public SessionStore createSessionStore(HttpSession session) {
		return persistence.createSessionStore(session);
	}

	@Override
	public SessionStore getSessionStore(HttpSession session) {
		return persistence.getSessionStore(session);
	}

	
	private class SessionPersistence extends AbstractSessionPersistence<SessionStore>{

		@Override
		protected SessionStore createPersistence(HttpSession session) {
			SessionStore store = new SessionStore();
			return store;
		}

		public boolean hasLoginUser( long token ) {
			for( SessionStore store: getSessions().values() ) {
				if(( store.getLoginUser() != null ) && ( store.getLoginUser().getToken() == token ))
					return true;
			}
			return false;
		}

		public boolean logoff( ILoginUser loginUser ) {
			return false;
		}
	}			
}
