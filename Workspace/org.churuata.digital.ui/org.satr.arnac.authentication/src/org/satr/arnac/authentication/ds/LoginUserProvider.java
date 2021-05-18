package org.satr.arnac.authentication.ds;

import java.util.Collection;
import java.util.Map;

import javax.security.auth.callback.CallbackHandler;

import org.condast.commons.authentication.core.IAuthenticationListener;
import org.condast.commons.authentication.core.ILoginProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.osgi.service.component.annotations.Component;
import org.satr.arnac.authentication.core.ArnacCallbackHandler;
import org.satr.arnac.authentication.core.Dispatcher;
import org.satr.arnac.authentication.services.LoginService;

@Component( name="org.satr.arnac.authentication.login.factory",
		immediate=true)
public class LoginUserProvider implements ILoginProvider {

	private Dispatcher dispatcher = Dispatcher.getInstance();
	
	@Override
	public boolean isRegistered( long loginId) {
		return dispatcher.isRegistered( loginId );
	}
	
	@Override
	public boolean isLoggedIn(long loginId) {
		return dispatcher.isLoggedIn(loginId);
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
	public void addAuthenticationListener(IAuthenticationListener listener) {
		dispatcher.addAuthenticationListener(listener);
	}

	@Override
	public void removeAuthenticationListener(IAuthenticationListener listener) {
		dispatcher.addAuthenticationListener(listener);
	}

	@Override
	public void logout(long loginId, long token) {
		dispatcher.logout(loginId, token);
	}

	@Override
	public void logoutRequest() {
		dispatcher.logoutRequest();
	}

	@Override
	public CallbackHandler createCallbackHandler() {
		return new ArnacCallbackHandler();
	}
}
