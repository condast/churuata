package org.churuata.digital.entries;

import java.util.concurrent.TimeUnit;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.map.OrganisationMapBrowser;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.config.Config;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.utils.RWTUtils;
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

public class ActiveEntryPoint extends AbstractChuruataEntryPoint<ChuruataOrganisationData>{
	private static final long serialVersionUID = 1L;

	public static final String S_PAGE = "page";

	public static final String S_CHURUATA = "churuata";
	public static final String S_CHURUATA_CREATE = "/" + S_CHURUATA + "/create";	
	public static final String S_CHURUATA_EDIT =  "/" + S_CHURUATA + "/edit";	

	public static final String S_ERR_NO_VESSEL = "No Vessel has been found.";

	private OrganisationMapBrowser mapComposite;
	private Button btnLocate;
	private Button btnCreate;
	private Button btnEdit;
	
	private IEditListener<LatLng> listener = e->onLocationChanged(e);
		
	/**
	 * Slow down start time a bit in order to let the browser find the location
	 * @param startTime
	 * @param rate
	 */
	public ActiveEntryPoint() {
		super( 3*DEFAULT_SCHEDULE, DEFAULT_SCHEDULE);
	}

	@Override
	protected boolean prepare(Composite parent) {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		String tokenstr = service.getParameter( IDomainProvider.Attributes.TOKEN.name().toLowerCase());
		if(StringUtils.isEmpty(tokenstr)) 
			return false;
		
		long token = Long.parseLong(tokenstr);
		Dispatcher dispatcher = Dispatcher.getInstance();
		IDomainProvider<SessionStore<ChuruataOrganisationData>> provider = dispatcher.getDomain(token );
		if( provider == null )
			return false;

		SessionStore<ChuruataOrganisationData> store = provider.getData();
		if( store == null )
			return false;
		store.setToken(token);
		setData(store);
		ILoginUser user = store.getLoginUser();
		return ( user != null );
	}
	
	@Override
	protected Composite createComposite(Composite parent) {
		GridLayout layout = new GridLayout(1,true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);
		mapComposite = new OrganisationMapBrowser(parent, SWT.NONE );
		mapComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
		mapComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mapComposite.addEditListener( listener );
		
		Group group = new Group( parent, SWT.NONE );
		group.setText("Edit Churuata");
		group.setLayout( new GridLayout(5, false ));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		ChuruataImages images = ChuruataImages.getInstance();

		btnLocate = new Button(group, SWT.NONE);
		btnLocate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnLocate.setImage( images.getImage( ChuruataImages.Images.LOCATE));
		btnLocate.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					mapComposite.locate();
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

		btnCreate = new Button(group, SWT.NONE);
		btnCreate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnCreate.setImage( images.getImage( ChuruataImages.Images.CHURUATA));
		btnCreate.setEnabled(false);
		btnCreate.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					RWTUtils.redirect( S_CHURUATA_CREATE);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

		btnEdit = new Button(group, SWT.NONE);
		btnEdit.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnEdit.setImage( images.getImage( ChuruataImages.Images.BUILDER));
		btnEdit.setEnabled(false);
		btnEdit.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					RWTUtils.redirect( S_CHURUATA_EDIT);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

		return mapComposite;
	}

	@Override
	protected boolean postProcess(Composite parent) {
		Config config = Config.getInstance();
		mapComposite.setInput(config.getServerContext());

		mapComposite.locate();
		SessionStore<ChuruataOrganisationData> store = super.getSessionStore();
		LatLng selected = store.getSelected();
		this.btnCreate.setEnabled( selected != null );
		this.btnEdit.setEnabled( selected != null );
		return true;
	}
	
	protected void onLocationChanged( EditEvent<LatLng> event ) {
		LatLng data = event.getData();
		SessionStore<ChuruataOrganisationData> store = super.getSessionStore();
		switch( event.getType()) {
		case INITIALISED:
			break;
		case CHANGED:
			store.setSelected( data);
			this.btnCreate.setEnabled( data != null );
			this.btnEdit.setEnabled( data != null );
			break;
		case SELECTED:
			store.setSelected( data);
			Dispatcher.jump(Entries.Pages.CREATE, store.getToken());
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
			//mapComposite.refresh(null);
			SessionStore<ChuruataOrganisationData> store = super.getSessionStore();
			if(( store == null ) || ( store.getLoginUser() == null ))
				return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean handleSessionTimeout(boolean reload) {
		SessionStore<ChuruataOrganisationData> store = super.getSessionStore();
		store.setLoginUser(null);
		return super.handleSessionTimeout(reload);
	}

	@Override
	public void close() {
		mapComposite.removeEditListener( listener);
		super.close();
	}
}