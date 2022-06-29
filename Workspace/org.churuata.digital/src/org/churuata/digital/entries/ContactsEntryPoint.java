package org.churuata.digital.entries;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.config.Config;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.ContactData;
import org.condast.commons.na.data.PersonData;
import org.condast.commons.na.model.IContact;
import org.condast.commons.na.model.IContact.ContactTypes;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
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

public class ContactsEntryPoint extends AbstractChuruataEntryPoint<OrganisationData> {
	private static final long serialVersionUID = 1L;

	private ContactWidget contactWidget;
	private Button btnOk;

	private SessionHandler handler;
	
	private IContact data = null;

	private IEditListener<IContact> listener = e->onContactEvent(e);

	private WebController controller;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	protected boolean prepare(Composite parent) {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore<OrganisationData>> domain = Dispatcher.getDomainProvider( service );
		if( domain == null )
			return false;
		SessionStore<OrganisationData> store = domain.getData();
		if( store == null )
			return false;
		setData(store);
		handler = new SessionHandler( parent.getDisplay());
		return true;
	}

	@Override
    protected Composite createComposite(Composite parent) {
        parent.setLayout(new GridLayout( 1, false ));
        contactWidget = new ContactWidget( parent, SWT.NONE);
 		contactWidget.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
 		contactWidget.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false));
		Group group = new Group( parent, SWT.NONE );
		group.setText("Add Churuata Service");
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
					SessionStore<OrganisationData> store = getSessionStore();
					PersonData person = store.getPersonData();
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
		Config config = new Config();
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
			SessionStore<OrganisationData> store = getSessionStore();
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
	
	private class SessionHandler extends AbstractSessionHandler<SessionStore<OrganisationData>>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<SessionStore<OrganisationData>> sevent) {
			/* NOTHING */
		}
	}
	
	private class WebController extends AbstractHttpRequest<ProfileData.Requests>{
		
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
				sendGet(ProfileData.Requests.ADD_CONTACT_TYPE, params );
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
				case ADD_CONTACT_TYPE:
					PersonData data = gson.fromJson(event.getResponse(), PersonData.class);
					store.setPersonData(data);
					Dispatcher.jump( Pages.REGISTER, store.getToken());
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
