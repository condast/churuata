package org.churuata.digital.entries;

import java.util.concurrent.TimeUnit;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.views.OrganisationComposite;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
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


public class OrganisationEntryPoint extends AbstractChuruataEntryPoint {
	private static final long serialVersionUID = 1L;

	public static final String S_CHURUATA = "Churuata-Digital";

	public static final String S_CHURUATA_PAGE = "/churuata";

	private OrganisationComposite servicesComposite;
	private Button btnAdd;

	private SessionHandler handler;
	
	private OrganisationData data = null;

	private IEditListener<OrganisationData> listener = e->onServiceEvent(e);

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
		ILoginUser user = store.getLoginUser();
		handler = new SessionHandler( parent.getDisplay());
		return ( user != null );
	}

	@Override
    protected Composite createComposite(Composite parent) {
        parent.setLayout(new GridLayout( 1, false ));
        servicesComposite = new OrganisationComposite( parent, SWT.NONE);
 		servicesComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
 		servicesComposite.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false));
		Group group = new Group( parent, SWT.NONE );
		group.setText("Add Churuata Service");
		group.setLayout( new GridLayout(5, false ));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		ChuruataImages images = ChuruataImages.getInstance();

		btnAdd = new Button(group, SWT.NONE);
		btnAdd.setEnabled(false);
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnAdd.setImage( images.getImage( ChuruataImages.Images.ADD));
		btnAdd.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					if( data == null )
						return;
					SessionStore store = getSessionStore();
					OrganisationData organisation = store.getOrganisation();
					//organisation.addChuruataType(data);
					Dispatcher.jump(Entries.Pages.CREATE, store.getToken());
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

 		return servicesComposite;
    }

	@Override
	protected boolean postProcess(Composite parent) {
		this.servicesComposite.addEditListener(listener);
		return super.postProcess(parent);
	}

	protected void onServiceEvent( EditEvent<OrganisationData> event ) {
		switch( event.getType()) {
		case COMPLETE:
			data = event.getData();
			btnAdd.setEnabled( data != null);
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
		this.servicesComposite.removeEditListener(listener);
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
