package org.satr.arnac.authentication.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.security.auth.callback.CallbackHandler;
import javax.servlet.http.HttpSession;

import org.condast.commons.authentication.core.AuthenticationEvent;
import org.condast.commons.authentication.core.IAuthenticationListener;
import org.condast.commons.authentication.core.IAuthenticationListener.AuthenticationEvents;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.authentication.core.ILoginProvider;
import org.condast.commons.authentication.session.DefaultSessionStore;
import org.condast.commons.persistence.core.ISessionStoreFactory;
import org.condast.commons.persistence.service.AbstractPersistencyService;
import org.condast.commons.persistence.service.IPersistenceService;
import org.satr.arnac.authentication.services.LoginService;

public class Dispatcher extends AbstractPersistencyService implements ILoginProvider, IPersistenceService{

	//Needs to be the same as in the persistence.xml file
	private static final String S_ARNAC_SERVICE_ID = "org.satr.arnac.authentication"; 
	private static final String S_ARNAC_SERVICE = "ARNAC Authentication Service"; 

	private static Dispatcher service = new Dispatcher();
	
	private  Set<ILoginUser> users;
	
	private Collection<IAuthenticationListener> listeners;
	
	private ISessionStoreFactory<HttpSession, DefaultSessionStore> store;

	private Dispatcher(  ) {
		super( S_ARNAC_SERVICE_ID, S_ARNAC_SERVICE );
		users = new TreeSet<ILoginUser>();
		listeners = new ArrayList<>();
	}

	public static Dispatcher getInstance(){
		return service;
	}
	
	@Override
	public void addAuthenticationListener( IAuthenticationListener listener ) {
		this.listeners.add(listener);
	}

	@Override
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
		// TODO Auto-generated method stub
		
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

	@Override
	public boolean isRegistered( long loginId ) {
		LoginService service = new LoginService( this );
		ILoginUser user = service.find( loginId );
		return ( user != null );
	}

	@Override
	public boolean isLoggedIn(long loginId) {
		for( ILoginUser user: this.users ) {
			if( user.getId() == loginId )
				return true;
		}
		return ( loginId <= 0);
	}

	@Override
	public ILoginUser getLoginUser(long loginId, long token ) {
		for( ILoginUser user: this.users) {
			if( user.getId() == loginId )
				return user;
		}
		return null;
	}

	@Override
	public Map<Long, String> getUserNames( Collection<Long> userIds) {
		LoginService service = new LoginService( this );
		return service.getUserNames(userIds);
	}

	@Override
	public void logout(long loginId, long token ) {
		ILoginUser user = getLoginUser(loginId, token );
		this.removeUser(user);
	}

	@Override
	public void logoutRequest() {
		//NOTHING
	}

	@Override
	public CallbackHandler createCallbackHandler() {
		return new ArnacCallbackHandler();
	}

	public void setSessionStore(ISessionStoreFactory<HttpSession, DefaultSessionStore> store) {
		this.store = store;
	}	

	public void removeSessionStore(ISessionStoreFactory<HttpSession, DefaultSessionStore> store) {
		this.store = null;
	}	

	public DefaultSessionStore getStore( HttpSession session ) {
		if(( session == null ) || ( this.store == null ))
			return null;
		return store.createSessionStore(session);
	}
}