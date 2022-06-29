package org.churuata.digital.entries.register;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.config.Config;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.ContactPersonData;
import org.condast.commons.na.data.PersonData;
import org.condast.commons.na.model.IContact;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.na.person.ContactPersonComposite;
import org.condast.commons.ui.player.PlayerImages;
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

public class RegisterEntryPoint extends AbstractChuruataEntryPoint<OrganisationData>{
	private static final long serialVersionUID = 1L;

	public static final String S_ADD_ACCOUNT = "Add Account";

	private ContactPersonComposite personComposite;
	private Button btnNext;

	private IEditListener<ContactPersonData> listener = e->onPersonEvent(e);

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	protected boolean prepare(Composite parent) {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		String tokenStr = service.getParameter(IDomainProvider.Attributes.TOKEN.toAttribute());
		IDomainProvider<SessionStore<OrganisationData>> provider = null;
		if( StringUtils.isEmpty(tokenStr)) {
			provider = Dispatcher.createDomain();
		}else
			provider = Dispatcher.getDomainProvider(service);
		if( provider == null )
			return false;
		setData(provider.getData());
		return true;
	}
	
	@Override
	protected Composite createComposite(Composite parent) {
		parent.setLayout( new GridLayout(1,false));
		personComposite = new ContactPersonComposite(parent, SWT.NONE );
		personComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
		personComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		personComposite.addEditListener( listener);

		Group group = new Group( parent, SWT.NONE );
		group.setText( S_ADD_ACCOUNT);
		group.setLayout( new GridLayout(5, false ));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		btnNext = new Button(group, SWT.NONE);
		btnNext.setEnabled(false);
		btnNext.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnNext.setImage( PlayerImages.getImage( PlayerImages.Images.NEXT));
		btnNext.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					SessionStore<OrganisationData> store = getSessionStore();
					if( store.getContactPersonData() == null )
						return;
					PersonData person = store.getPersonData();
					if( person == null ) 				
						controller.register( store.getContactPersonData());
					else
						Dispatcher.jump( Pages.ORGANISATION, store.getToken());						
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

		return personComposite;
	}

	@Override
	protected boolean postProcess(Composite parent) {
		Config config = new Config();
		String context = config.getServerContext();
		controller = new WebController();
		controller.setInput(context, IRestPages.Pages.CONTACT.toPath());
		SessionStore<OrganisationData> store = getSessionStore();
		PersonData personData = store.getPersonData();
		ContactPersonData person = store.getContactPersonData();
		if( personData != null ) {
			person = new ContactPersonData( personData );
			person.clearContacts();
			for( IContact contact: personData.getContacts() )
				person.addContact(contact);
			store.setContactPersonData(person);
		}
		if( person != null )
			personComposite.setInput(store.getContactPersonData(), true);
		return true;
	}

	protected void onPersonEvent( EditEvent<ContactPersonData> event ) {
		ContactPersonData data = null;
		SessionStore<OrganisationData> store = super.getSessionStore();
		controller.type = event.getType();
		switch( event.getType()) {
		case ADDED:
			store.setContactPersonData( this.personComposite.getInput());
			PersonData person = store.getPersonData();
			if( person == null ) 
				controller.register( store.getContactPersonData());
			else
				Dispatcher.jump( Pages.CONTACTS, store.getToken());
				
			break;
		case COMPLETE:
			data = event.getData();
			store.setContactPersonData(data);
			btnNext.setEnabled(( data != null ));
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean handleSessionTimeout(boolean reload) {
		SessionStore<OrganisationData> store = super.getSessionStore();
		store.setLoginUser(null);
		return super.handleSessionTimeout(reload);
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
				SessionStore<OrganisationData> store = getSessionStore();
				Gson gson = new Gson();
				switch( event.getRequest()){
				case REGISTER:
					PersonData data = gson.fromJson(event.getResponse(), PersonData.class);
					store.setPersonData(data);
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
					ProfileData profile = gson.fromJson(event.getResponse(), ProfileData.class);
					//editComposite.setInput(profile, true);
					store.setProfile(profile);
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