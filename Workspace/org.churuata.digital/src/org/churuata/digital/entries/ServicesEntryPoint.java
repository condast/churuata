package org.churuata.digital.entries;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.Entries.Pages;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.core.data.ServiceData;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.views.ServicesTableViewer;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.messaging.core.util.NodeData;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.image.DashboardImages;
import org.condast.commons.ui.image.IImageProvider.ImageSize;
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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ServicesEntryPoint extends AbstractWizardEntryPoint<ServicesTableViewer, IChuruataService> {
	private static final long serialVersionUID = 1L;

	public static final String S_EDIT_SERVICES = "Edit Services";

	private ServicesTableViewer servicesTable;

	private WebController controller;

	private IEditListener<IChuruataService> listener = e -> onServiceEvent( e );
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public ServicesEntryPoint() {
		super(S_EDIT_SERVICES, false);
	}

	@Override
	protected SessionStore createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		return ( domain == null )? null: domain.getData();
	}

	@Override
	protected ServicesTableViewer onCreateComposite(Composite parent, int style) {
        parent.setLayout(new GridLayout( 1, false ));
        servicesTable = new ServicesTableViewer( parent, SWT.NONE);
 		servicesTable.setData( RWT.CUSTOM_VARIANT, Entries.S_CHURUATA );
 		servicesTable.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false));
 		servicesTable.addEditListener(listener);
 		return servicesTable;
    }

	@Override
	protected void onSetupButtonBar(Group buttonBar) {
		Button btnAdd = getBtnNext();
		btnAdd.setImage( DashboardImages.getImage(DashboardImages.Images.ADD, ImageSize.NORMAL));
		super.onSetupButtonBar(buttonBar);
	}

	@Override
	protected boolean onPostProcess(String context, SessionStore store) {
		controller = new WebController(context, IRestPages.Pages.ORGANISATION.toPath());
		this.servicesTable.addEditListener(listener);

		ChuruataOrganisationData org = (ChuruataOrganisationData) store.getData().getOrganisation()[0];
		this.servicesTable.setInput(Arrays.asList( org.getServices()));
		return true;
	}

	@Override
	protected void onButtonPressed(IChuruataService org, SessionStore store) {/* NOTHING */}

	protected void onServiceEvent( EditEvent<IChuruataService> event ) {
		try {
			SessionStore store = super.getSessionStore();
			IChuruataService service = event.getData();
			ILoginUser user = store.getLoginUser();
			ProfileData profile= store.getData();
			ChuruataOrganisationData organisation = (ChuruataOrganisationData) profile.getOrganisation()[0];
			JumpController<NodeData<ChuruataOrganisationData, IChuruataService>> jc = new JumpController<>();
			switch( event.getType()) {
			case ADDED:
				jc.jump( new NodeJumpEvent<ChuruataOrganisationData, IChuruataService>( this, Pages.SERVICES.name(), store.getToken(), Pages.SERVICE.toPath(), JumpController.Operations.UPDATE, organisation, service));			
				break;
			case SELECTED:
				setCache(service);
				jc.jump( new NodeJumpEvent<ChuruataOrganisationData, IChuruataService>( this, Pages.SERVICES.name(), store.getToken(), Pages.SERVICE.toPath(), JumpController.Operations.UPDATE, organisation, service));			
				break;
			case DELETE:
				controller.remove( user, organisation, ServiceData.getIds( event.getBatch() ));
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		this.servicesTable.removeEditListener(listener);
		super.close();
	}
		
	private class WebController extends AbstractHttpRequest<ChuruataOrganisationData.Requests>{
		
		public WebController( String context, String path) {
			super( context, path);
		}

		public void remove(ILoginUser user, ChuruataOrganisationData organisation, long[] batch) {
			Map<String, String> params = super.getParameters();
			params.put( LoginData.Parameters.USER_ID.toString(), String.valueOf( user.getId()));
			params.put( LoginData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity() ));
			params.put( ChuruataOrganisationData.Parameters.ORGANISATION_ID.toString(), String.valueOf( organisation.getId() ));
			Gson gson = new Gson();
			String data = gson.toJson(batch, long[].class);
			try {
				sendDelete(ChuruataOrganisationData.Requests.REMOVE_SERVICES, params, data );
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}

		@Override
		protected String onHandleResponse(ResponseEvent<ChuruataOrganisationData.Requests> event) throws IOException {
			try {
				SessionStore store = getSessionStore();
				ProfileData profile = store.getData();
				Gson gson = new Gson();
				switch( event.getRequest()){
				case UPDATE_SERVICE:
				case ADD_SERVICE:
					ChuruataOrganisationData org = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
					profile.addOrganisation(org);
					ChuruataOrganisationData data = gson.fromJson(event.getResponse(), ChuruataOrganisationData.class);
					JumpController<ChuruataOrganisationData> jc = new JumpController<>();
					jc.jump( new JumpEvent<ChuruataOrganisationData>( this, Pages.SERVICES.name(), store.getToken(), Pages.ORGANISATION.toPath(), JumpController.Operations.UPDATE, data));			
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
