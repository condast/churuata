package org.churuata.digital.entries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractChuruataEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.organisation.OrganisationsTableViewer;
import org.condast.commons.Utils;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.config.Config;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.na.data.OrganisationData;
import org.condast.commons.na.profile.IProfileData;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.messaging.jump.JumpController;
import org.condast.commons.ui.messaging.jump.JumpEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class OrganisationsEntryPoint extends AbstractChuruataEntryPoint<ChuruataOrganisationData> {
	private static final long serialVersionUID = 1L;

	private OrganisationsTableViewer tableViewer;
	
	private WebController controller;

	private IEditListener<ChuruataOrganisationData> listener = e->onServiceEvent(e);

	private Logger logger = Logger.getLogger(this.getClass().getName());

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
    protected Composite createComposite(Composite parent) {
        parent.setLayout(new GridLayout( 1, false ));
        tableViewer = new OrganisationsTableViewer( parent, SWT.NONE);
 		tableViewer.setData( RWT.CUSTOM_VARIANT, Entries.S_CHURUATA_DIGITAL );
 		tableViewer.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false));
		Group group = new Group( parent, SWT.NONE );
		group.setText("Show Organisations");
		group.setLayout( new GridLayout(5, false ));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
 		return tableViewer;
    }

	@Override
	protected boolean postProcess(Composite parent) {
		Config config = Config.getInstance();
		String context = config.getServerContext();
		controller = new WebController( context, IRestPages.Pages.ORGANISATION);
		this.tableViewer.addEditListener(listener);
		SessionStore store = super.getSessionStore();
		IProfileData profile = store.getData();
		if(( profile != null ) && !Utils.assertNull(profile.getOrganisation())) {
			Collection<ChuruataOrganisationData> organisations = new ArrayList<>();
			for( OrganisationData organisation: profile.getOrganisation())
				organisations.add((ChuruataOrganisationData) organisation);
			this.tableViewer.setInput(organisations);
		}
		return super.postProcess(parent);
	}

	protected void onServiceEvent( EditEvent<ChuruataOrganisationData> event ) {
		SessionStore store = super.getSessionStore();
		IProfileData person = store.getData();

		ChuruataOrganisationData organisation = event.getData();
		JumpController<ChuruataOrganisationData> jc = new JumpController<>();
		switch( event.getType()) {
		case ADDED:
			jc.jump( new JumpEvent<ChuruataOrganisationData>( this, store.getToken(), Pages.ORGANISATION.toPath(), JumpController.Operations.UPDATE, organisation));			
			break;
		case SELECTED:
			store.setOrganisation(organisation);
			if( organisation.getId() <= 0 ) 
				controller.register(person.getId(), organisation);
			else {
				jc.jump( new JumpEvent<ChuruataOrganisationData>( this, store.getToken(), Pages.ORGANISATION.toPath(), JumpController.Operations.UPDATE, organisation));			
			}
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
	public void close() {
		this.tableViewer.removeEditListener(listener);
		super.close();
	}
	
	private class WebController extends AbstractHttpRequest<ChuruataOrganisationData.Requests>{
		
		public WebController( String context, IRestPages.Pages page) {
			super( context, page.toPath());
		}

		public void register( long personId, ChuruataOrganisationData organisation ) {
			Map<String, String> params = super.getParameters();
			params.put(ProfileData.Parameters.PERSON_ID.toString(), String.valueOf( personId ));
			Gson gson = new Gson();
			String data = gson.toJson(organisation, ChuruataOrganisationData.class);
			try {
				sendPut(ChuruataOrganisationData.Requests.REGISTER, params, data );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ChuruataOrganisationData.Requests> event) throws IOException {
			try {
				SessionStore store = getSessionStore();
				Gson gson = new Gson();
				switch( event.getRequest()){
				case REGISTER:
					ChuruataOrganisationData data = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
					store.setOrganisation(data);
					JumpController<ChuruataOrganisationData> jc = new JumpController<>();
					jc.jump( new JumpEvent<ChuruataOrganisationData>( this, store.getToken(), Pages.SERVICES.toPath(), JumpController.Operations.UPDATE, data));			
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
		protected void onHandleResponseFail(HttpStatus status, ResponseEvent<ChuruataOrganisationData.Requests> event) throws IOException {
			super.onHandleResponseFail(status, event);
		}	
	}
}