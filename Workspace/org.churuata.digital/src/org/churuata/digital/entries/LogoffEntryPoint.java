package org.churuata.digital.entries;

import org.churuata.digital.core.AuthenticationDispatcher;
import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.session.SessionStore;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.ui.entry.IDataEntryPoint;
import org.condast.commons.ui.utils.RWTUtils;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.widgets.Composite;

public class LogoffEntryPoint extends AbstractEntryPoint implements IDataEntryPoint<SessionStore<OrganisationData>>{
	private static final long serialVersionUID = 1L;

	private static final String S_CHURUATA_HOME = "/churuata";
	
	private AuthenticationDispatcher dispatcher = AuthenticationDispatcher.getInstance();

	private SessionStore<OrganisationData> store;
	
	public LogoffEntryPoint() {
		super();
	}
	
	@Override
	public void setData(SessionStore<OrganisationData> store) {
		this.store = store;
	}

	@Override
	protected void createContents(Composite parent) {
		try {
			ILoginUser user = store.getLoginUser();
			store.setLoginUser(null);
			if( user != null )
				dispatcher.logout(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		RWTUtils.redirect(S_CHURUATA_HOME);
	}
}