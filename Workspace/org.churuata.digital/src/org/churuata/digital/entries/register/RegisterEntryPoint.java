package org.churuata.digital.entries.register;

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
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.ContactPersonData;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.profile.IProfileData;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.messaging.jump.JumpController;
import org.condast.commons.ui.messaging.jump.JumpEvent;
import org.condast.commons.ui.na.person.ContactPersonComposite;
import org.condast.commons.ui.session.SessionEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class RegisterEntryPoint extends AbstractWizardEntryPoint<ContactPersonComposite, ChuruataOrganisationData>{
	private static final long serialVersionUID = 1L;

	public static final String S_TITLE = "Register Organisation";

	private ContactPersonComposite personComposite;

	private IEditListener<ContactPersonData> listener = e->onPersonEvent(e);
	private IEditListener<IContact> contactListener = e->onContactEvent(e);

	private WebController controller;
	
	private ContactPersonData data;

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public RegisterEntryPoint() {
		super(S_TITLE);
		this.data = null;
	}

	@Override
	protected SessionStore createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		String tokenStr = service.getParameter(IDomainProvider.Attributes.TOKEN.toAttribute());
		IDomainProvider<SessionStore> domain = null;
		if( StringUtils.isEmpty(tokenStr))
			domain = Dispatcher.createDomain();
		else
			domain = Dispatcher.getDomainProvider( service );	
		return ( domain == null )?null:domain.getData();
	}

	@Override
	protected boolean onPrepare(SessionStore store) {
		return true;//always true, because this does not require login
	}

	@Override
	protected void onButtonPressed(ChuruataOrganisationData org, SessionStore store) {
		try{
			ProfileData person = store.getProfile();
			if(( person == null ) || ( person.getId() <0))
				controller.register( this.data );
			else
				controller.update(person);
			
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	@Override
	protected ContactPersonComposite onCreateComposite(Composite parent, int style) {
		personComposite = new ContactPersonComposite(parent, SWT.NONE );
		personComposite.setData( RWT.CUSTOM_VARIANT, Entries.S_CHURUATA );
		personComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		personComposite.addEditListener( listener);
		personComposite.addContactsListener( contactListener);
		return personComposite;
	}

	@Override
	protected boolean onPostProcess(String context, ChuruataOrganisationData data, SessionStore store) {
		controller = new WebController();
		controller.setInput(context, IRestPages.Pages.CONTACT.toPath());
		ProfileData profile = store.getProfile();
		if( profile != null ) {
			personComposite.setInput(profile, true);
			Button btnNext = super.getBtnNext();
			btnNext.setEnabled(personComposite.checkRequiredFields());
		}
		return true;
	}

	protected void onPersonEvent( EditEvent<ContactPersonData> event ) {
		this.data = event.getData();
		SessionStore store = super.getSessionStore();
		controller.type = event.getType();
		Button btnNext = super.getBtnNext();
		switch( event.getType()) {
		case CHANGED:
			btnNext.setEnabled( personComposite.checkRequiredFields());
			break;
		case ADDED:
			IProfileData person = store.getProfile();
			if( person == null ) 
				controller.register( event.getData());
			else {
				JumpController<ContactPersonData> jc = new JumpController<>();
				jc.jump( new JumpEvent<ContactPersonData>( this, store.getToken(), Pages.CONTACTS.toPath(), JumpController.Operations.UPDATE, data));			
			}
				
			break;
		case COMPLETE:
			btnNext.setEnabled( data != null );
			break;
		default:
			break;
		}
	}

	protected void onContactEvent( EditEvent<IContact> event ) {
		SessionStore store = super.getSessionStore();
		controller.type = event.getType();
		switch( event.getType()) {
		case DELETE:
			IProfileData person = store.getProfile();
			person.removeContact(event.getData());
			controller.remove( person, ContactPersonData.getIds( event.getBatch() ));
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onHandleSyncTimer(SessionEvent<SessionStore> sevent) {
		try {
			personComposite.refresh();
			super.handleTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	@Override
	public void close() {
		personComposite.removeEditListener(listener);
		personComposite.removeContactsListener(contactListener);
		super.close();
	}

	private class WebController extends AbstractHttpRequest<ProfileData.Requests>{
		
		private EditEvent.EditTypes type;
		
		public WebController() {
			super();
		}

		public void setInput(String context, String path) {
			super.setContextPath(context + path);
		}

		public void register( ContactPersonData person ) {
			Map<String, String> params = super.getParameters();
			params.put(ProfileData.Parameters.NAME.toString(), person.getName());
			params.put(ProfileData.Parameters.PREFIX.toString(), person.getPrefix());
			params.put(ProfileData.Parameters.SURNAME.toString(), person.getSurname());
			params.put(ProfileData.Parameters.EMAIL.toString(), person.getEmail());
			try {
				sendGet(ProfileData.Requests.REGISTER, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		public void update( ContactPersonData person ) {
			Map<String, String> params = super.getParameters();
			params.put(ProfileData.Parameters.PERSON_ID.toString(), String.valueOf( person.getId()));
			try {
				Gson gson = new Gson();
				String data = gson.toJson(person, ContactPersonData.class);
				sendPut(ProfileData.Requests.UPDATE_PERSON, params, data );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		public void remove(IProfileData person, long[] batch) {
			Map<String, String> params = super.getParameters();
			params.put( ProfileData.Parameters.PERSON_ID.toString(), String.valueOf( person.getId()));
			Gson gson = new Gson();
			String data = gson.toJson(batch, long[].class);
			try {
				sendDelete(ProfileData.Requests.REMOVE_CONTACTS, params, data );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ProfileData.Requests> event) throws IOException {
			try {
				SessionStore store = getSessionStore();
				Gson gson = new Gson();
				ProfileData profile = null;
				ContactPersonData data = null;
				JumpController<ProfileData> jc = new JumpController<>();
				switch( event.getRequest()){
				case REGISTER:
					data = gson.fromJson(event.getResponse(), ContactPersonData.class);
					profile = new ProfileData( data );
					store.setProfile(profile);
					switch( type ) {
					case ADDED:
						jc.jump( new JumpEvent<ProfileData>( this, store.getToken(), Pages.CONTACTS.toPath(), JumpController.Operations.UPDATE, profile));			
						break;
					case COMPLETE:
						jc.jump( new JumpEvent<ProfileData>( this, store.getToken(), Pages.ORGANISATION.toPath(), JumpController.Operations.UPDATE, profile));			
						break;
					default:
						break;
					}
					break;
				case UPDATE_PERSON:
					data = gson.fromJson(event.getResponse(), ContactPersonData.class);
					profile = new ProfileData( data );
					store.setProfile(profile);
					jc.jump( new JumpEvent<ProfileData>( this, store.getToken(), Pages.ORGANISATION.toPath(), JumpController.Operations.UPDATE, profile));			
					break;
				case GET_PROFILE:					
					profile = gson.fromJson(event.getResponse(), ProfileData.class);
					store.setProfile(profile);
					break;
				default:
					break;
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onHandleResponseFail(HttpStatus status, ResponseEvent<ProfileData.Requests> event) throws IOException {
			super.onHandleResponseFail(status, event);
		}	
	}
}