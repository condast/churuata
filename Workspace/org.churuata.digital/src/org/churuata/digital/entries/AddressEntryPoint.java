package org.churuata.digital.entries;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.views.ChuruataAddressComposite;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.config.Config;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.AddressData;
import org.condast.commons.na.profile.IProfileData;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.messaging.jump.JumpEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class AddressEntryPoint extends AbstractChuruataEntryPoint<AddressData>{
	private static final long serialVersionUID = 1L;

	public static final String S_PAGE = "page";

	public static final String S_CHURUATA = "churuata";

	private ChuruataAddressComposite addressComposite;
	private Button btnOk;

	private IEditListener<AddressData> listener = e->onOrganisationEvent(e);

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	protected SessionStore createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		return ( domain == null )? null: domain.getData();
	}

	@Override
	protected Composite createComposite(Composite parent) {
		parent.setLayout( new GridLayout(1,false));
		addressComposite = new ChuruataAddressComposite(parent, SWT.NONE );
		addressComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
		addressComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		addressComposite.addEditListener( listener);

		Group group = new Group( parent, SWT.NONE );
		group.setText("Add Churuata");
		group.setLayout( new GridLayout(5, false ));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		ChuruataImages images = ChuruataImages.getInstance();

		btnOk = new Button(group, SWT.NONE);
		btnOk.setEnabled(false);
		btnOk.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnOk.setImage( images.getImage( ChuruataImages.Images.CHECK));
		btnOk.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					SessionStore store = getSessionStore();
					if( store.getProfile() == null )
						return;
					IProfileData profile = store.getProfile();
					controller.setAddress( profile.getAddress());
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});
		return addressComposite;
	}

	@Override
	protected boolean postProcess(Composite parent) {
		if( !super.postProcess(parent))
			return false;
		Config config = Config.getInstance();
		String context = config.getServerContext();

		SessionStore store = getSessionStore();
		ILoginUser user = store.getLoginUser();
		controller = new WebController();
		controller.setInput(context, IRestPages.Pages.ORGANISATION.toPath());
		controller.user = user;

		JumpEvent<AddressData> event = super.getEvent();
		if( event != null ) {
			addressComposite.setInput(event.getData(), true);
		}else {
			IProfileData profile = store.getProfile();
			addressComposite.setInput(profile.getAddress(), true);
		}
		return true;
	}

	protected void onOrganisationEvent( EditEvent<AddressData> event ) {
		SessionStore store = getSessionStore();
		IProfileData profile = store.getProfile();
		switch( event.getType()) {
		case CHANGED:
			if( this.addressComposite.checkRequiredFields())
				btnOk.setEnabled(true);
			profile.setAddress(event.getData());
			break;
		case COMPLETE:
			AddressData data = event.getData();
			profile.setAddress(data);
			//store.setProfile(data);
			btnOk.setEnabled((data != null));
			break;
		default:
			break;
		}
	}

	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, rate);
	}

	@Override
	protected void handleTimer() {
		try {
			super.handleTimer();
			SessionStore store = getSessionStore();
			if(( store == null ) || ( store.getLoginUser() == null ))
				return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean handleSessionTimeout(boolean reload) {
		SessionStore store = super.getSessionStore();
		store.setLoginUser(null);
		return super.handleSessionTimeout(reload);
	}
	
	private class WebController extends AbstractHttpRequest<ChuruataOrganisationData.Requests>{
		
		private ILoginUser user;
		
		public WebController() {
			super();
		}

		public void setInput(String context, String path) {
			super.setContextPath(context + path);
		}

		public void setAddress( AddressData address) {
			Map<String, String> params = super.getParameters();
			try {
				params.put(ChuruataOrganisationData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
				params.put(ChuruataOrganisationData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity() ));
				Gson gson = new Gson();
				String data = gson.toJson(address, AddressData.class);
				sendPut(ChuruataOrganisationData.Requests.SET_ADDRESS, params, data );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ChuruataOrganisationData.Requests> event) throws IOException {
			try {
				SessionStore store = getSessionStore();
				switch( event.getRequest()){
				case SET_ADDRESS:
					Dispatcher.jump(Entries.Pages.ACTIVE, store.getToken());
					break;
				default:
					break;
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
			finally {
			}
			return null;
		}

		@Override
		protected void onHandleResponseFail(HttpStatus status, ResponseEvent<ChuruataOrganisationData.Requests> event) throws IOException {
			super.onHandleResponseFail(status, event);
		}
	
	}

}