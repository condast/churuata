package org.churuata.digital.authentication.ds;

import java.util.Collection;
import java.util.Map;

import org.churuata.digital.authentication.core.Dispatcher;
import org.churuata.digital.authentication.services.LoginService;
import org.condast.commons.authentication.core.IAuthenticationListener;
import org.condast.commons.authentication.core.ILoginProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.osgi.service.component.annotations.Component;

@Component( name="org.churuata.digital.authentication.login.factory",
		property={"type=churuata"},
		immediate=true)
public class LoginUserProvider implements ILoginProvider {

	private Dispatcher dispatcher = Dispatcher.getInstance();
	
	@Override
	public boolean isRegistered( long loginId) {
		return dispatcher.isRegistered( loginId );
	}
	
	@Override
	public ILoginUser getLoginUser( long loginId, long token ) {
		return dispatcher.getLoginUser( loginId, token );
	}
	
	@Override
	public Map<Long, String> getUserNames( Collection<Long> userIds ){
		LoginService service = new LoginService( dispatcher );
		return service.getUserNames(userIds);
	}

	@Override
	public boolean isLoggedIn(long loginId, long security) {
		return dispatcher.isLoggedIn(loginId, security);
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
	public boolean hasLoginUser(String userName, long token) {
		return dispatcher.hasLoginUser(userName, token);
	}

	@Override
	public void logout(ILoginUser user) {
		dispatcher.logout(user.getId(), user.getSecurity());
	}

	@Override
	public void logout(long loginId, long token) {
		dispatcher.logout(loginId, token);
	}
}
