package org.churuata.digital.authentication.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.churuata.digital.authentication.services.LoginService;
import org.churuata.digital.core.store.SessionStore;
import org.condast.commons.authentication.core.AuthenticationEvent;
import org.condast.commons.authentication.core.IAuthenticationListener;
import org.condast.commons.authentication.core.IAuthenticationListener.AuthenticationEvents;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.persistence.core.ISessionStoreFactory;
import org.condast.commons.persistence.service.AbstractPersistencyService;
import org.condast.commons.persistence.service.IPersistenceService;
import org.condast.commons.strings.StringUtils;

public class Dispatcher extends AbstractPersistencyService implements IPersistenceService{

	//Needs to be the same as in the persistence.xml file
	private static final String S_CHURUATA_SERVICE_ID = "org.churuata.digital.authentication"; 
	private static final String S_CHURUATA_SERVICE = "Churuata Digital Authentication Service"; 

	private static Dispatcher service = new Dispatcher();
	
	private  Set<ILoginUser> users;
	
	private Collection<IAuthenticationListener> listeners;
	
	private ISessionStoreFactory<HttpSession, SessionStore> store;

	private Dispatcher(  ) {
		super( S_CHURUATA_SERVICE_ID, S_CHURUATA_SERVICE );
		users = new TreeSet<ILoginUser>();
		listeners = new ArrayList<>();
	}

	public static Dispatcher getInstance(){
		return service;
	}

	public void addAuthenticationListener( IAuthenticationListener listener ) {
		this.listeners.add(listener);
	}

	public void removeAuthenticationListener( IAuthenticationListener listener ) {
		this.listeners.remove(listener);		
	}

	protected void notifyListeners( AuthenticationEvent event ) {
		for( IAuthenticationListener listener: this.listeners )
			listener.notifyLoginChanged( event );
	}

	@Override
	protected Map<String, String> onPrepareManager() {
		return null;
	}

	@Override
	protected void onManagerCreated(EntityManager manager) {
		// NOTHING
	}

	public boolean isRegistered( ILoginUser user ) {
		return this.users.contains( user );
	}

	public boolean addUser( ILoginUser user ){
		boolean found = this.users.contains(user);
		if( found ) {
			notifyListeners( new AuthenticationEvent( this, AuthenticationEvents.LOGIN, user ));
			return false;
		}
		this.users.add( user );
		notifyListeners( new AuthenticationEvent( this, AuthenticationEvents.LOGIN, user ));
		return true;
	}
	
	public boolean removeUser( ILoginUser user ) {
		boolean result = this.users.remove( user );
		notifyListeners( new AuthenticationEvent( this, AuthenticationEvents.LOGOUT, user ));
		return result;
	}

	public ILoginUser getUser( long id ) {
		for( ILoginUser user: this.users ) {
			if( user.getId() == id )
				return user;
		}
		return null;
	}

	public boolean isRegistered( long loginId ) {
		LoginService service = new LoginService( this );
		ILoginUser user = service.find( loginId );
		return ( user != null );
	}

	public boolean isLoggedIn(long loginId) {
		for( ILoginUser user: this.users ) {
			if( user.getId() == loginId )
				return true;
		}
		return ( loginId <= 0);
	}

	public ILoginUser getLoginUser(long loginId, long token ) {
		for( ILoginUser user: this.users) {
			if( user.getId() == loginId )
				return user;
		}
		return null;
	}

	public boolean hasLoginUser( String userName, long token ) {
		if( StringUtils.isEmpty(userName))
			return false;
		for( ILoginUser user: this.users) {
			if( userName.equals( user.getUserName()) && ( user.getSecurity() - token ) == 0) 
				return true;
		}
		return false;
	}

	public Map<Long, String> getUserNames( Collection<Long> userIds) {
		LoginService service = new LoginService( this );
		return service.getUserNames(userIds);
	}

	public void logout(long loginId, long token ) {
		ILoginUser user = getLoginUser(loginId, token );
		this.removeUser(user);
	}

	public void setSessionStore(ISessionStoreFactory<HttpSession, SessionStore> store) {
		this.store = store;
	}	

	public void removeSessionStore(ISessionStoreFactory<HttpSession, SessionStore> store) {
		this.store = null;
	}	

	public SessionStore getStore( HttpSession session ) {
		if(( session == null ) || ( this.store == null ))
			return null;
		return store.createSessionStore(session);
	}
}