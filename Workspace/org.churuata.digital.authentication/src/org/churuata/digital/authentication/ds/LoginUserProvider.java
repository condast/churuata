package org.churuata.digital.authentication.ds;

import org.churuata.digital.authentication.core.Dispatcher;
import org.condast.commons.data.user.IAdmin;
import org.condast.commons.data.user.ILoginUser;
import org.condast.commons.jpa.authentication.core.IAuthenticationListener;
import org.condast.commons.jpa.authentication.core.ILoginProvider;
import org.osgi.service.component.annotations.Component;

@Component( name="org.churuata.digital.authentication.login.factory",
		property={"type=churuata"})
public class LoginUserProvider implements ILoginProvider {

	private Dispatcher dispatcher = Dispatcher.getInstance();
	
	
	@Override
	public IAdmin getAdmin(String domain, ILoginUser user) {
		return dispatcher.getAdmin( user);
	}

	@Override
	public boolean isRegistered( long loginId) {
		return dispatcher.isRegistered( loginId );
	}
	
	@Override
	public boolean isLoggedIn(long loginId, long security) {
		return dispatcher.isLoggedIn(loginId);
	}

	@Override
	public ILoginUser getLoginUser(String domain,  long loginId, long token ) {
		return dispatcher.getLoginUser( loginId, token );
	}
	
	@Override
	public void addAuthenticationListener(IAuthenticationListener listener) {
		dispatcher.addAuthenticationListener(listener);
	}

	@Override
	public void removeAuthenticationListener(IAuthenticationListener listener) {
		dispatcher.removeAuthenticationListener(listener);
	}

	@Override
	public boolean hasLoginUser(String domain, String userName, long token) {
		return dispatcher.hasLoginUser(userName, token);
	}

	@Override
	public boolean logout(String domain, ILoginUser user) {
		return dispatcher.logout(user.getId(), user.getSecurity());
	}

	@Override
	public boolean logout(String domain, long loginId, long token) {
		return dispatcher.logout(loginId, token);
	}
}
