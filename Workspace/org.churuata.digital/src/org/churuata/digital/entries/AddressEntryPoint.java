package org.churuata.digital.entries;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.views.ChuruataAddressComposite;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.AddressData;
import org.condast.commons.na.profile.IProfileData;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.messaging.jump.JumpController;
import org.condast.commons.ui.messaging.jump.JumpEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class AddressEntryPoint extends AbstractWizardEntryPoint<ChuruataAddressComposite,AddressData>{
	private static final long serialVersionUID = 1L;

	public static final String S_ADD_ADDRESS = "Add Address";

	private ChuruataAddressComposite addressComposite;

	private IEditListener<AddressData> listener = e->onOrganisationEvent(e);

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	
	public AddressEntryPoint() {
		super(S_ADD_ADDRESS);
	}

	@Override
	protected SessionStore createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		return ( domain == null )? null: domain.getData();
	}

	@Override
	protected boolean onPrepare(SessionStore store) {
		return true;//always true, because this does not require login
	}

	@Override
	protected ChuruataAddressComposite onCreateComposite(Composite parent, int style) {
		parent.setLayout( new GridLayout(1,false));
		addressComposite = new ChuruataAddressComposite(parent, SWT.NONE );
		addressComposite.setData( RWT.CUSTOM_VARIANT, Entries.S_CHURUATA );
		addressComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		addressComposite.addEditListener( listener);
		return addressComposite;
	}

	@Override
	protected void onSetupButtonBar(Group buttonBar) {
		ChuruataImages images = ChuruataImages.getInstance();
		Button btnOk = getBtnNext();
		btnOk.setImage( images.getImage( ChuruataImages.Images.CHECK));
		super.onSetupButtonBar(buttonBar);
	}

	@Override
	protected boolean onPostProcess(String context, AddressData data, SessionStore store) {
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

	
	@Override
	protected void onButtonPressed(AddressData data, SessionStore store) {
		try{
			if( store.getProfile() == null )
				return;
			IProfileData profile = store.getProfile();
			controller.setAddress( profile.getAddress());
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	protected void onOrganisationEvent( EditEvent<AddressData> event ) {
		SessionStore store = getSessionStore();
		IProfileData profile = store.getProfile();
		Button btnOk = getBtnNext();
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
					JumpController<ProfileData> jc = new JumpController<>();
					jc.jump( new JumpEvent<ProfileData>( this, store.getToken(), Pages.ORGANISATION.toPath(), JumpController.Operations.DONE, store.getProfile()));							
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