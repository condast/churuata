package org.churuata.digital.entries.register;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.core.data.simple.SimpleOrganisationData;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.map.OrganisationMapBrowser;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.messaging.core.util.NodeData;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.messaging.jump.JumpController;
import org.condast.commons.ui.messaging.jump.JumpEvent;
import org.condast.commons.ui.messaging.jump.NodeJumpEvent;
import org.condast.commons.ui.session.SessionEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class LocationEntryPoint extends AbstractWizardEntryPoint<OrganisationMapBrowser, ChuruataOrganisationData> {
	private static final long serialVersionUID = 1L;

	public static final String S_TITLE = "Enter location";
	
	private OrganisationMapBrowser mapComposite;

	private IEditListener<LatLng> listener = e->onEditEvent( e );

	private JumpEvent<NodeData<ChuruataOrganisationData, IChuruataService>> event;

	public LocationEntryPoint() {
		super(S_TITLE);
	}

	@Override
	protected SessionStore createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		return ( domain == null )? null: domain.getData();
	}

	@Override
	protected OrganisationMapBrowser onCreateComposite(Composite parent, int style) {
        mapComposite = new OrganisationMapBrowser( parent, SWT.NONE);
 		mapComposite.setData( RWT.CUSTOM_VARIANT, Entries.S_CHURUATA );
		mapComposite.addEditListener(listener);
 		return mapComposite;
	}

	@Override
	protected boolean onPostProcess(String context, SessionStore store) {
		mapComposite.locate();
		mapComposite.setInput(context);

		JumpController<NodeData<ChuruataOrganisationData, IChuruataService>> jc = new JumpController<>();
		event = jc.getEvent( Pages.LOCATION.toPath());
		NodeData<ChuruataOrganisationData, IChuruataService> node = event.getData();
		ChuruataOrganisationData organisation = node.getData();
		setCache(organisation);

		IChuruataService service = (IChuruataService) node.getChild();
		mapComposite.setInput(new SimpleOrganisationData( organisation ), service);			
		return true;
	}

	@Override
	protected void onSetupButtonBar(Group buttonBar) {
		ChuruataImages images = ChuruataImages.getInstance();
		Button button = getBtnNext();
		button.setEnabled(false);
		button.setImage( images.getImage( ChuruataImages.Images.CHECK));
		super.onSetupButtonBar(buttonBar);
	}

	private Object onEditEvent(EditEvent<LatLng> e) {
		switch( e.getType()) {
		case SELECTED:
			if( e.getData() == null )
				return null;
			SessionStore store = getSessionStore();
			ProfileData profile= store.getData();
			ChuruataOrganisationData organisation = (ChuruataOrganisationData) profile.getOrganisation()[0];
			organisation.setLocation(e.getData());
			setCache(organisation);
			Button btnNext = getBtnNext();
			btnNext.setEnabled(true);
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	protected void onButtonPressed(ChuruataOrganisationData data, SessionStore store) {
		JumpController<NodeData<ChuruataOrganisationData, IChuruataService>> jc = new JumpController<>();
		Pages page = ( event == null )? Pages.ORGANISATION: Pages.valueOf(event.getIdentifier());
		NodeData<ChuruataOrganisationData, IChuruataService> node = event.getData();
		jc.jump( new NodeJumpEvent<ChuruataOrganisationData, IChuruataService>( this, Pages.LOCATION.name(), store.getToken(), page.toPath(), JumpController.Operations.DONE, node.getData(), node.getChild()));			
	}

	@Override
	protected void onHandleSyncTimer(SessionEvent<SessionStore> sevent) {
		this.mapComposite.refresh();
		super.onHandleSyncTimer(sevent);
	}

	@Override
	public void close() {
		this.mapComposite.removeEditListener(listener);
		super.close();
	}
}
