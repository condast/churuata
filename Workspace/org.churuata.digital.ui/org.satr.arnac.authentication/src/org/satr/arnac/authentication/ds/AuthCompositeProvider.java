package org.satr.arnac.authentication.ds;

import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.session.DefaultSessionStore;
import org.condast.commons.authentication.ui.views.AuthenticationGroup;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.provider.ICompositeProvider;
import org.condast.commons.ui.utils.RWTUtils;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.annotations.Component;
import org.satr.arnac.authentication.core.Dispatcher;
import org.satr.arnac.authentication.services.LoginService;

@Component( name="org.satr.arnac.authentication.composite.provider",
		immediate=true)
public class AuthCompositeProvider implements ICompositeProvider<AuthenticationGroup> {

	public static final String S_LOGIN_PAGE = "/arnac/route?page=route";

	public static final String S_LOGIN = "login";
	public static final String S_TITLE = "Login Screen";
	
	@Override
	public String getName() {
		return S_LOGIN;
	}

	@Override
	public String getTitle() {
		return S_TITLE;
	}

	@Override
	public AuthenticationGroup getComposite(Composite parent, int style) {
		AuthenticationGroup group = new AuthenticationGroup(parent, SWT.FULL_SELECTION | style );
		group.setConfirmation(false);
		group.addEditListener(e->onEditEvent(e));
		return group;
	}
	
	protected void onEditEvent( EditEvent<LoginData> event ) {
		Dispatcher service = Dispatcher.getInstance();
		LoginService login = new LoginService( service );
		login.open();
		try {
			switch( event.getType()){			
			case COMPLETE: 
				LoginData data = event.getData();
				ILoginUser user;
				if( event.getData().isRegister()) {
					user = login.create( data.getNickName(), data.getPassword(), data.getEmail());
				}else {
					user = login.login(data.getNickName(), data.getPassword());
				}
				if( user == null )
					return;
				DefaultSessionStore store = service.getStore( RWT.getUISession().getHttpSession());
				store.setLoginUser(user);
				service.addUser(user);
				RWTUtils.redirect( S_LOGIN_PAGE );
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			login.close();
		}
	}
}