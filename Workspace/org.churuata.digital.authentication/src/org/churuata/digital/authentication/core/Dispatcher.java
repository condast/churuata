package org.churuata.digital.authentication.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.churuata.digital.authentication.services.LoginService;
import org.condast.commons.authentication.core.AuthenticationEvent;
import org.condast.commons.authentication.core.IAuthenticationListener;
import org.condast.commons.authentication.core.IAuthenticationListener.AuthenticationEvents;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.persistence.service.AbstractPersistencyService;
import org.condast.commons.persistence.service.IPersistenceService;
import org.condast.commons.strings.StringUtils;

public class Dispatcher extends AbstractPersistencyService implements IPersistenceService{

	public static final String S_CHURUATA = "churuata";

	//Needs to be the same as in the persistence.xml file
	private static final String S_CHURUATA_SERVICE_ID = "org.churuata.digital.authentication"; 
	private static final String S_CHURUATA_SERVICE = "Churuata Digital Authentication Service"; 

	private static Dispatcher service = new Dispatcher();
	
	private  Set<ILoginUser> users;
	
	private Collection<IAuthenticationListener> listeners;
	
	private Map<Long, LoginData> confirmation;

	private Dispatcher(  ) {
		super( S_CHURUATA_SERVICE_ID, S_CHURUATA_SERVICE );
		users = new TreeSet<ILoginUser>();
		this.confirmation = new HashMap<>();
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

	public ILoginUser getUserFromSecurity( long security) {
		for( ILoginUser user: this.users ) {
			if( user.getSecurity() == security)
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

	public boolean isLoggedIn(long loginId, long security) {
		for( ILoginUser user: this.users ) {
			if(( user.getId() == loginId ) && ( user.getSecurity() == security ))
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

	public long addConfirmRegistration( LoginData login ) {
		long registration = Math.abs( new Random().nextLong());
		this.confirmation.put(registration, login);
		return registration;
	}

	public long addForgotPassword( ILoginUser login ) {
		this.confirmation.put( login.getSecurity(),  new LoginData( login ));
		return login.getSecurity();
	}

	public LoginData getStoredUser( long confirmation ) {
		if( confirmation < 0)
			return null;
		return this.confirmation.remove(confirmation);
	}

	public boolean logout(long loginId, long token ) {
		ILoginUser user = getLoginUser(loginId, token );
		if( user == null )
			return false;
		this.removeUser(user);
		return true;
	}
}