package org.churuata.digital.authentication.ds;

import org.churuata.digital.authentication.core.Dispatcher;
import org.churuata.digital.authentication.services.LoginService;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.ui.views.AuthenticationGroup;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.persistence.service.TransactionManager;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.provider.ICompositeProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.annotations.Component;

@Component( name="org.churuata.digital.authentication.composite.provider",
		property={"type=churuata"},
		immediate=true)
public class AuthCompositeProvider implements ICompositeProvider<AuthenticationGroup> {

	public static final String S_REDIRECT_PAGE = "/churuata/active";

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
		TransactionManager t = new TransactionManager( service );
		try {
			t.open();
			LoginService login = new LoginService( service );
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
				service.addUser(user);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			t.close();
		}
	}
}