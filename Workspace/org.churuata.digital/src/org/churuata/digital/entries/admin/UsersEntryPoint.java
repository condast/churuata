package org.churuata.digital.entries.admin;

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
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.ContactPersonData;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.profile.IProfileData;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
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

public class UsersEntryPoint extends AbstractWizardEntryPoint<ContactPersonComposite, ChuruataOrganisationData>{
	private static final long serialVersionUID = 1L;

	public static final String S_TITLE = "Users";

	private ContactPersonComposite personComposite;

	private IEditListener<ContactPersonData> listener = e->onPersonEvent(e);

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public UsersEntryPoint() {
		super(S_TITLE);
	}

	@Override
	protected SessionStore createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		return ( domain == null )? null: domain.getData();
	}

	@Override
	protected boolean prepare(Composite parent) {
		if( !super.prepare(parent))
			return false;
		SessionStore store = super.getSessionStore();
		ILoginUser user = store.getLoginUser();
		return ( user != null );
	}
	
	@Override
	protected void onButtonPressed(ChuruataOrganisationData data, SessionStore store) {
		try{
			if( store.getData() == null )
				return;
			ProfileData person = (ProfileData) store.getData();
			if( person == null ) 				
				controller.register( person );
			else
				Dispatcher.jump( Pages.ORGANISATION, store.getToken());						
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
		return personComposite;
	}

	@Override
	protected boolean onPostProcess(String context, ChuruataOrganisationData data, SessionStore store) {
		controller = new WebController();
		controller.setInput(context, IRestPages.Pages.CONTACT.toPath());
		ProfileData person = store.getData();
		if( person != null ) {
			person.clearContacts();
			for( IContact contact: person.getContacts() )
				person.addContact(contact);
		}
		if( person != null )
			personComposite.setInput(person, true);
		return true;
	}

	protected void onPersonEvent( EditEvent<ContactPersonData> event ) {
		ContactPersonData data = null;
		SessionStore store = super.getSessionStore();
		controller.type = event.getType();
		switch( event.getType()) {
		case ADDED:
			IProfileData person = store.getData();
			if( person == null ) 
				controller.register( event.getData());
			else
				Dispatcher.jump( Pages.CONTACTS, store.getToken());
				
			break;
		case COMPLETE:
			data = event.getData();
			Button btnNext = super.getBtnNext();
			btnNext.setEnabled(( data != null ));
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onHandleSyncTimer(SessionEvent<SessionStore> sevent) {
		try {
			personComposite.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		@Override
		protected String onHandleResponse(ResponseEvent<ProfileData.Requests> event) throws IOException {
			try {
				SessionStore store = getSessionStore();
				Gson gson = new Gson();
				ProfileData profile = null;
				switch( event.getRequest()){
				case REGISTER:
					ContactPersonData data = gson.fromJson(event.getResponse(), ContactPersonData.class);
					profile = new ProfileData( data );
					store.setData(profile);
					switch( type ) {
					case ADDED:
						Dispatcher.jump( Pages.CONTACTS, store.getToken());
						break;
					case COMPLETE:
						Dispatcher.jump( Pages.ORGANISATION, store.getToken());
						break;
					default:
						break;
					}
					break;
				case UPDATE_PERSON:
					Dispatcher.redirect(Entries.Pages.ACTIVE, store.getToken());
					break;
				case GET_PROFILE:					
					profile = gson.fromJson(event.getResponse(), ProfileData.class);
					//editComposite.setInput(profile, true);
					store.setData(profile);
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
		protected void onHandleResponseFail(HttpStatus status, ResponseEvent<ProfileData.Requests> event) throws IOException {
			super.onHandleResponseFail(status, event);
		}
	
	}
}