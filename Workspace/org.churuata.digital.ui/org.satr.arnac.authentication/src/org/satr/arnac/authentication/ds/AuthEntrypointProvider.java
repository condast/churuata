package org.satr.arnac.authentication.ds;

import org.condast.commons.authentication.ui.views.AuthenticationGroup;
import org.condast.commons.ui.def.ICompositeProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.annotations.Component;

@Component( name="org.satr.arnac.authentication.factory",
		immediate=true)
public class AuthEntrypointProvider implements ICompositeProvider {

	public static final String S_LOGIN = "login";
	public static final String S_TITLE = "Login Screen";
	
	@Override
	public String getName() {
		return S_LOGIN;
	}

	public String getTitle() {
		return S_TITLE;
	}

	
	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Composite getComposite(String id, Composite parent, int style) {
		AuthenticationGroup group = new AuthenticationGroup(parent, SWT.FULL_SELECTION | style );
		group.setConfirmation(true);
		return group;
	}
}
