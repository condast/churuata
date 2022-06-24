package org.churuata.digital.entries;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.na.data.ContactPersonData;
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

public class ContactsEntryPoint extends AbstractChuruataEntryPoint {
	private static final long serialVersionUID = 1L;

	private ContactWidget contactWidget;
	private Button btnOk;

	private SessionHandler handler;
	
	private IContact data = null;

	private IEditListener<IContact> listener = e->onContactEvent(e);

	@Override
	protected boolean prepare(Composite parent) {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		if( domain == null )
			return false;
		SessionStore store = domain.getData();
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
					SessionStore store = getSessionStore();
					ContactPersonData person = store.getContactPersonData();
					person.addContact( data );
					Dispatcher.jump( Pages.REGISTER, store.getToken());
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
		ContactWidget.createContactTypes( this.contactWidget, EnumSet.allOf(ContactTypes.class ));
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
}
