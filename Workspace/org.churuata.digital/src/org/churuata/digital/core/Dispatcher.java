package org.churuata.digital.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.churuata.digital.session.SessionStore;
import org.condast.commons.authentication.core.AuthenticationEvent;
import org.condast.commons.authentication.core.IAuthenticationListener;
import org.condast.commons.authentication.http.AbstractDomainProvider;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.utils.RWTUtils;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;

public class Dispatcher {

	private static final String S_CHURUATA = "churuata";

	private static Dispatcher dispatcher = new Dispatcher();
	
	private Map<Long, IDomainProvider<SessionStore>> domains;
	
	private AuthenticationDispatcher auth = AuthenticationDispatcher.getInstance();
	private IAuthenticationListener listener = e -> onNotificationEvent(e);

	public Dispatcher() {
		super();
		domains = new HashMap<>();
		auth.addAuthenticationListener(listener);
	}

	public static Dispatcher getInstance() {
		return dispatcher;
	}
	
	private void onNotificationEvent( AuthenticationEvent event ) {
		try {
			ILoginUser user = event.getUser();
			IDomainProvider<SessionStore> domain = getDomain(user.getId(), user.getSecurity());
			switch( event.getEvent()) {
			case LOGIN:
				if( domain == null )
					domain = createDomain( user);
				break;
			case LOGOUT:
				removeDomain(user.getId());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a new domain and return the corresponding token
	 * @param userId
	 * @return
	 */
	public IDomainProvider<SessionStore> createDomain( String domain, long token, String path ) {
		IDomainProvider<SessionStore> provider = this.domains.get( token);
		if( provider == null ) {
			provider = new DomainProvider( domain, token, path );
			this.domains.put(token, provider);
		}
		return provider;
	}

	/**
	 * Get the domain for the given token
	 * @param token
	 * @return
	 */
	public IDomainProvider<SessionStore> getDomain( long token ) {
		if( token < 0 )
			return null;
		return this.domains.get(token);
	}

	/**
	 * Get the domain for the given token
	 * @param token
	 * @return
	 */
	public IDomainProvider<SessionStore> getDomain( long userId, long security) {
		if(( userId < 0 ) || ( security < 0 ))
			return null;
		for( IDomainProvider<SessionStore> domain: this.domains.values()) {
			SessionStore store = domain.getData();
			if( store == null )
				continue;
			ILoginUser user = store.getLoginUser();
			if( user == null )
				continue;
			if( user.isCorrect(userId, security))
				return domain;
		}
		return null;
	}

	public void removeDomain( long userId ) {
		Collection<Map.Entry<Long,IDomainProvider<SessionStore>>> entries = new ArrayList<Map.Entry<Long,IDomainProvider<SessionStore>>>( this.domains.entrySet());
		for(Map.Entry<Long,IDomainProvider<SessionStore>> entry: entries) {
			ILoginUser user = entry.getValue().getData().getLoginUser();
			if(( user != null ) && ( user.getId() == userId))
				this.domains.remove(entry.getKey());
		}
	}
	
	public static boolean redirect( Entries.Pages page, long token ) {
		return redirect( page.toPath(), token );
	}

	public static boolean redirect( String path, long token ) {
		if( token < 0 )
			return false;
		HttpSession session = RWT.getUISession().getHttpSession();
		session.setAttribute( IDomainProvider.Attributes.TOKEN.name(), token);
		path +="&token=" + token;
		return RWTUtils.redirect( path );
	}

	public static boolean jump( Entries.Pages page, long token ) {
		if( token < 0 )
			return false;
		HttpSession session = RWT.getUISession().getHttpSession();
		session.setAttribute( IDomainProvider.Attributes.TOKEN.name(), token);
		return RWTUtils.redirect( page.toPath()  + "?" + IDomainProvider.Attributes.TOKEN.name().toLowerCase() + "=" + token );
	}

	/**
	 * The return path is by definition: {context}/{domain}/{path}
	 * @param login
	 * @param domain
	 * @param token
	 * @param path
	 * @return
	 */
	public static String getPath( String login, String domain, long token, String path ) {
		return login.toLowerCase() + "?" + IDomainProvider.Attributes.TOKEN.name().toLowerCase() + "=" + token +
				 "&" +IDomainProvider.Attributes.DOMAIN.name().toLowerCase() + "=" + domain +
				 "&" + Entries.Pages.ACTIVE.name().toLowerCase() + "=" + path;
	}

	/**
	 * Create a domain for Arnac
	 * @param user
	 * @param token
	 * @param active
	 * @return
	 */
	public static IDomainProvider<SessionStore> createDomain( ILoginUser user ) {
		Random random = new Random();
		long token = Math.abs( random.nextLong() );
		IDomainProvider<SessionStore> domain = dispatcher.createDomain( Dispatcher.S_CHURUATA, token, Entries.Pages.ACTIVE.name().toLowerCase());
		domain.getData().setLoginUser(user);
		return domain;
	}

	/**
	 * Create the default domain for Arnac
	 * @param user
	 * @param token
	 * @param active
	 * @return
	 */
	public static IDomainProvider<SessionStore> createDomain() {	
		Random random = new Random();
		long token = Math.abs( random.nextLong() );
		return dispatcher.createDomain( S_CHURUATA , token, Entries.Pages.ACTIVE.name().toLowerCase());
	}

	public static IDomainProvider<SessionStore> getDomainProvider( StartupParameters service ) {
		String tokenstr = service.getParameter(StringStyler.xmlStyleString( IDomainProvider.Attributes.TOKEN.name()));
		long token = -1;
		if(!StringUtils.isEmpty(tokenstr)) { 
			token = Long.parseLong(tokenstr);
			return dispatcher.getDomain(token );
		}else {
			HttpSession session = RWT.getUISession().getHttpSession();
			Long arg = (Long) session.getAttribute( IDomainProvider.Attributes.TOKEN.name());
			if(( arg!= null ) && ( arg > 0))
				return dispatcher.getDomain(arg);
		}
		String userstr = service.getParameter( StringStyler.xmlStyleString( IDomainProvider.Attributes.USER_ID.name()));
		if(StringUtils.isEmpty(userstr))
			return null;
		long userId = Long.parseLong(userstr);
		
		String securitystr = service.getParameter(StringStyler.xmlStyleString( IDomainProvider.Attributes.SECURITY.name()));
		if(StringUtils.isEmpty(securitystr))
			return null;
		long security = Long.parseLong(securitystr);
		IDomainProvider<SessionStore> domain = dispatcher.getDomain(userId, security);
		return domain;
	}
	private class DomainProvider extends AbstractDomainProvider<SessionStore>{

		private long userId;
		
		public DomainProvider( String domain, long token, String path ) {
			super( domain, path, token );
			super.setData(new SessionStore( token ));
		}

		@Override
		public boolean accept(long userid, long token) {
			if(( this.userId - userid) != 0)
				return false;
			return super.accept( this.userId, token);
		}
	}
}
