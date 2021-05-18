package org.churuata.digital;

import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.store.SessionStore;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.ui.utils.RWTUtils;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.widgets.Composite;

public class LogoffEntryPoint extends AbstractEntryPoint{
	private static final long serialVersionUID = 1L;

	private static final String S_CHURUATA_HOME = "/churuata";
	
	private Dispatcher dispatcher = Dispatcher.getInstance();

	@Override
	protected void createContents(Composite parent) {
		try {
			SessionStore store = dispatcher.getSessionStore(RWT.getUISession().getHttpSession());
			ILoginUser user = store.getLoginUser();
			store.setLoginUser(null);
			if( user != null )
				dispatcher.logoff(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		RWTUtils.redirect(S_CHURUATA_HOME);
	}
}