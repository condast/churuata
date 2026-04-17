package org.churuata.digital.entries;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.views.ChuruataAddressComposite;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.messaging.core.util.NodeData;
import org.condast.commons.na.data.AddressData;
import org.condast.commons.na.data.OrganisationData;
import org.condast.commons.na.profile.IProfileData;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.image.DashboardImages;
import org.condast.commons.ui.messaging.jump.JumpController;
import org.condast.commons.ui.messaging.jump.JumpEvent;
import org.condast.commons.ui.messaging.jump.NodeJumpEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class AddressEntryPoint extends AbstractWizardEntryPoint<ChuruataAddressComposite, AddressData>{
	private static final long serialVersionUID = 1L;

	public static final String S_ADD_ADDRESS = "Add Address";

	private ChuruataAddressComposite addressComposite;

	private IEditListener<AddressData> listener = e->onOrganisationEvent(e);
	
	private JumpEvent<NodeData<?,AddressData>> event;

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
		Button btnOk = getBtnNext();
		btnOk.setImage(DashboardImages.getImage( DashboardImages.Images.CHECK, 32));
		super.onSetupButtonBar(buttonBar);
	}

	@Override
	protected boolean onPostProcess(String context, SessionStore store) {
		JumpController<NodeData<?,AddressData>> jc = new JumpController<>();
		event = jc.getEvent(Pages.ADDRESS.toPath());
		if( event != null ) {
			NodeData<?,AddressData> node = event.getData();
			AddressData address = ( node == null )?null: node.getChild();
			addressComposite.setInput(address, true);
		}else{
			IProfileData profile = store.getData();
			addressComposite.setInput(profile.getAddress(), true);
		}
		return true;
	}
	
	@Override
	protected void onButtonPressed(AddressData data, SessionStore store) {
		try{
			if( store.getData() == null )
				return;
			IProfileData profile = store.getData();
			OrganisationData organisation = profile.getOrganisation()[0];
			if( organisation == null ) {
				organisation = (OrganisationData) profile.getOrganisation()[0];
				profile.getAddress().setPrincipal(true);
			}
			JumpController<NodeData<Object,AddressData>> jc = new JumpController<>();
			Pages page = ( event == null )? Pages.ORGANISATION: Pages.valueOf(event.getIdentifier());
			NodeData<?,AddressData> node = ( event == null )? null: event.getData();
			Object parent = ( node ==null )?null: node.getData();
			jc.jump( new NodeJumpEvent<Object, AddressData>( this, Pages.ADDRESS.name(), store.getToken(), page.toPath(), JumpController.Operations.DONE, parent, data ));							
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	protected void onOrganisationEvent( EditEvent<AddressData> event ) {
		SessionStore store = getSessionStore();
		IProfileData profile = store.getData();
		setCache(event.getData());
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
}