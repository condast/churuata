package org.churuata.digital.authentication.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.TypedQuery;

import org.churuata.digital.authentication.core.Dispatcher;
import org.churuata.digital.authentication.model.Login;
import org.condast.commons.Utils;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.persistence.service.AbstractEntityService;
import org.condast.commons.persistence.service.IPersistenceService;

public class LoginService extends AbstractEntityService<Login>{

	private static final String SELECT = "SELECT l FROM Login l ";
	private static final String SELECT_USER = SELECT + "WHERE l.userName=:userName AND l.password=:password";

	private static final String S_IDS = "ids"; 
	private static final String SQL_LOGIN_ID_QUERY = SELECT + "WHERE l.id in :" + S_IDS;

	public LoginService( IPersistenceService service) {
		super( Login.class, service);
	}

	public ILoginUser create( String name, String password, String email ) {
		Login login = new Login();
		login.setUserName(name);
		login.setPassword(password);
		login.setEmail(email);
		super.create(login);
		return login;
	}
	
	public ILoginUser login( String user, String password ) {
		if( !super.isConnected())
			return null;
		TypedQuery<ILoginUser> query = super.getManager().createQuery( SELECT_USER, ILoginUser.class);
		query.setParameter("userName", user);
		query.setParameter("password", password);
		Collection<ILoginUser> users = query.getResultList();
		if( Utils.assertNull( users))
			return null;
		return users.iterator().next();
	}

	public ILoginUser[] getAll() {
		Collection<Login> users = super.findAll("");
		if( Utils.assertNull( users))
			return null;
		return users.toArray( new ILoginUser[users.size()]);
	}
	
	/**
	 * Get the user names corresponding with the given user ids.
	 * @param userIds
	 * @return
	 */
	public Map<Long, String> getUserNames( Collection<Long> userIds ){
		if( Utils.assertNull(userIds ))
			return null;
		Map<Long, String> results = new HashMap<Long, String>();
		try{
			TypedQuery<Login> query = super.getTypedQuery( SQL_LOGIN_ID_QUERY );
			query.setParameter( S_IDS, userIds );
			Collection<Login> qresults = query.getResultList();
			for( Login login: qresults ) {
				results.put(login.getId(), login.getUserName());
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return results;
	}
	
	public static ILoginUser register( LoginData loginData ) {
		Dispatcher dispatcher = Dispatcher.getInstance();
		LoginService service = new LoginService( dispatcher ); 
		ILoginUser user = null;
		try{
			service.open();
			user = service.create( loginData.getNickName(), loginData.getPassword(), loginData.getEmail());
			dispatcher.addUser(user);
			return user;
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		finally {
			service.close();
		}
		return null;
	}

	public static ILoginUser login( LoginData loginData ) {
		Dispatcher dispatcher = Dispatcher.getInstance();
		LoginService service = new LoginService( dispatcher ); 
		ILoginUser user = null;
		try{
			service.open();
			user = service.login( loginData.getNickName(), loginData.getPassword());
			dispatcher.addUser(user);
			return user;
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		finally {
			service.close();
		}
		return null;
	}
}
