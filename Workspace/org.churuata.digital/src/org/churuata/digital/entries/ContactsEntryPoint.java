package org.churuata.digital.entries;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ChuruataProfileData;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.config.Config;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.ContactData;
import org.condast.commons.na.data.PersonData;
import org.condast.commons.na.data.ProfileData;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.model.IContact.ContactTypes;
import org.condast.commons.na.profile.IProfileData;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.messaging.jump.JumpController;
import org.condast.commons.ui.messaging.jump.JumpEvent;
import org.condast.commons.ui.na.contacts.ContactWidget;
import org.condast.commons.ui.session.AbstractSessionHandler;
import org.condast.commons.ui.session.SessionEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ContactsEntryPoint extends AbstractChuruataEntryPoint<ChuruataOrganisationData> {
	private static final long serialVersionUID = 1L;

	private ContactWidget contactWidget;
	private Button btnOk;

	private SessionHandler handler;
	
	private IContact data = null;

	private IEditListener<IContact> listener = e->onContactEvent(e);

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	protected SessionStore createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		return ( domain == null )? null: domain.getData();
	}

	@Override
	protected boolean onPrepare(SessionStore store) {
		return true;
	}
	
	@Override
    protected Composite createComposite(Composite parent) {
        parent.setLayout(new GridLayout( 1, false ));
        contactWidget = new ContactWidget( parent, SWT.NONE);
 		contactWidget.setData( RWT.CUSTOM_VARIANT, Entries.S_CHURUATA );
 		contactWidget.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false));
		Group group = new Group( parent, SWT.NONE );
		group.setText("Add Contact");
		group.setLayout( new GridLayout(5, false ));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		ChuruataImages images = ChuruataImages.getInstance();

		btnOk = new Button(group, SWT.NONE);
		btnOk.setEnabled(false);
		btnOk.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnOk.setImage( images.getImage( ChuruataImages.Images.ADD));
		btnOk.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					if( data == null )
						return;
					SessionStore store = getSessionStore();
					IProfileData person = store.getProfile();
					controller.addContact(data, person.getId());
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

 		return contactWidget;
    }

	@Override
	protected boolean postProcess(Composite parent) {
		if( !super.postProcess(parent))
			return false;
		Config config = Config.getInstance();
		String context = config.getServerContext();
		controller = new WebController( context, IRestPages.Pages.CONTACT.toPath());
		ContactWidget.createContactTypes( this.contactWidget, EnumSet.allOf(ContactTypes.class ));
		this.contactWidget.select( ContactTypes.MOBILE.ordinal());
		this.contactWidget.addEditListener(listener);
		return super.postProcess(parent);
	}

	protected void onContactEvent( EditEvent<IContact> event ) {
		switch( event.getType()) {
		case COMPLETE:
			SessionStore store = getSessionStore();
			if( store.getContactPersonData() == null )
				return;
			data = event.getData();
			btnOk.setEnabled( data != null);
			break;
		default:
			break;
		}
	}

	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, 10000);
	}

	@Override
	protected void handleTimer() {
		handler.addData(getSessionStore());
		super.handleTimer();
	}

	@Override
	public void close() {
		this.contactWidget.removeEditListener(listener);
		super.close();
	}
	
	private class SessionHandler extends AbstractSessionHandler<SessionStore>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<SessionStore> sevent) {
			/* NOTHING */
		}
	}
	
	private class WebController extends AbstractHttpRequest<ChuruataProfileData.Requests>{
		
		public WebController(String context, String path) {
			super();
			super.setContextPath(context + path);
		}

		public void addContact( IContact contact, long personId ) {
			Map<String, String> params = super.getParameters();
			params.put(ContactData.Parameters.PERSON_ID.toString(), String.valueOf( personId));
			params.put(ContactData.Parameters.CONTACT_TYPE.toString(), contact.getContactType().name());
			params.put(ContactData.Parameters.VALUE.toString(), contact.getValue());
			params.put(ContactData.Parameters.RESTRICTED.toString(), String.valueOf( contact.isRestricted()));
			try {
				sendGet(ChuruataProfileData.Requests.ADD_CONTACT_TYPE, params );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ChuruataProfileData.Requests> event) throws IOException {
			try {
				SessionStore store = getSessionStore();
				Gson gson = new Gson();
				JumpController<PersonData> jc = new JumpController<>();
				switch( event.getRequest()){
				case ADD_CONTACT_TYPE:
					PersonData data = gson.fromJson(event.getResponse(), PersonData.class);
					ProfileData profile = new ProfileData( data );
					store.setProfile(profile);
					jc.jump( new JumpEvent<PersonData>( this, store.getToken(), Pages.REGISTER.toPath(), JumpController.Operations.DONE, data));			
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
		protected void onHandleResponseFail(HttpStatus status, ResponseEvent<ChuruataProfileData.Requests> event) throws IOException {
			super.onHandleResponseFail(status, event);
		}
	
	}

}
