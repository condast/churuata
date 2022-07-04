package org.churuata.digital.ui.views;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.churuata.digital.core.data.OrganisationData;
import org.churuata.digital.core.location.ChuruataData;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruata.Requests;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.core.location.IChuruataService;
import org.condast.commons.Utils;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.AbstractEntityComposite;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.swt.InputField;
import org.condast.commons.ui.table.TableEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class EditChuruataComposite extends AbstractEntityComposite<OrganisationData>
{
	private static final long serialVersionUID = 7782765745284140623L;

	public static final String S_NAME = "Churuata";
	public static final String REGEXP = ("[\\ ;:,]");

	private static final String S_NAME_INFORMATION_TIP = "The Churuata name";
	private static final String S_DESCRIPTION = "Description";
	private static final String S_DESCRIPTOR_INFORMATION_TIP = "Describe the Churuata";
	private static final String S_WEBSITE = "Website";
	private static final String S_LOCATION = "Location:";
	
	private InputField nameField;
	private InputField descriptionField;
	private InputField txtURL;
	
	private Label latlngLabel;
	
	private ChuruataTableComposite churuataTypesTable;

	private ILoginUser user;
	
	private WebController controller;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public EditChuruataComposite( Composite parent, int style ){
		super(parent, style );
		controller = new WebController();
	}
	

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected void createComposite(Composite parent, int style )
	{
		Composite container = this;
		this.setLayout( new GridLayout(1, false));

		setData( RWT.CUSTOM_ITEM_HEIGHT, Integer.valueOf( 10 ));
				
		Group grpFillIn = new Group(container, SWT.NONE);
		grpFillIn.setText("Churuata:");
		grpFillIn.setLayout(new GridLayout(2, false));
		grpFillIn.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1 ));	
		
		nameField = new InputField( grpFillIn, SWT.NONE );
		nameField.setBackgroundControl(1);
		nameField.setLabel( S_NAME + ": ");
		nameField.setLabelWidth(85);
		nameField.setInformationMessage( S_NAME_INFORMATION_TIP );
		nameField.setBackgroundControl(0);
		this.nameField.addVerifyListener( new VerifyListener()
		{	
			private static final long serialVersionUID = 1L;

			@Override
			public void verifyText(VerifyEvent event) {
				onVerifyText(event, null);
			}
		});
		GridData gridData_2 = new GridData();
		gridData_2.horizontalSpan = 2;
		gridData_2.heightHint = 33;
		gridData_2.widthHint = 182;
		gridData_2.horizontalAlignment = GridData.FILL;
		gridData_2.grabExcessHorizontalSpace = true;
		nameField.setLayoutData(gridData_2);	
		
		descriptionField = new InputField(grpFillIn, SWT.NONE);
		descriptionField.setLabel( S_DESCRIPTION + ": ");
		descriptionField.setBackgroundControl(0);
		descriptionField.setLabelWidth(85);
		descriptionField.setInformationMessage(S_DESCRIPTOR_INFORMATION_TIP);
		GridData gridData_3 = new GridData();
		gridData_3.horizontalSpan = 2;
		gridData_3.heightHint = 33;
		gridData_3.widthHint = 182;
		gridData_3.horizontalAlignment = GridData.FILL;
		gridData_3.grabExcessHorizontalSpace = true;
		descriptionField.setLayoutData(gridData_3);
		this.descriptionField.addVerifyListener( new VerifyListener()
		{	
			private static final long serialVersionUID = 1L;

			@Override
			public void verifyText(VerifyEvent event) {
				onVerifyText(event, null);
			}
		});
		
				
		txtURL = new InputField(grpFillIn, SWT.NONE );
		txtURL.setLabel( S_WEBSITE );
		txtURL.setLabelWidth(85);
		txtURL.addVerifyListener( new VerifyListener()
		{	
			private static final long serialVersionUID = 1L;

			@Override
			public void verifyText(VerifyEvent event) {
				onVerifyText(event, null);
			}
		});
		GridData gd_txtURL = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_txtURL.heightHint = 33;
		gd_txtURL.widthHint = 260;
		txtURL.setLayoutData(gd_txtURL);
		
		Label locationLabel = new Label( grpFillIn, SWT.NONE);
		GridData locGrid = new GridData( SWT.FILL, SWT.FILL, false, true);
		locGrid.widthHint = 120;
		locationLabel.setLayoutData( locGrid);
		locationLabel.setText( S_LOCATION);
		
		latlngLabel = new Label( grpFillIn, SWT.BORDER);
		latlngLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true));

		this.churuataTypesTable = new ChuruataTableComposite(container, SWT.NONE);
		this.churuataTypesTable.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true ));	
		this.churuataTypesTable.addEditListener(e->notifyInputEdited(new EditEvent<OrganisationData>( this, EditTypes.ADDED)));
	}

	public void setInput( String context, ILoginUser user ){
		controller.setInput(context, IRestPages.Pages.SUPPORT.toPath());
		this.user = user;
	}

	@Override
	public void update(){
		OrganisationData result = super.getInput();
	}

	@Override
	protected OrganisationData onGetInput(OrganisationData input) {
		input.setName(this.nameField.getText());
		input.setDescription( this.descriptionField.getText());
		input.setWebsite( this.txtURL.getText());
		return input;
	}

	@Override
	protected void onSetInput(OrganisationData input, boolean overwrite) {
		latlngLabel.setText(input.getLocation().toLocation());
		this.nameField.setText(input.getName());
		this.descriptionField.setText(input.getDescription());
		this.txtURL.setText(input.getWebsite());
		this.churuataTypesTable.setInput(input);
	}

	public void setInput( OrganisationData input ){
		super.setInput( input, false );
	}

	protected void onNotifyTableEvent( TableEvent<ChuruataData> event ) {
		switch( event.getTableEvent()) {
		case DELETE:
			OrganisationData model = getInput();
			//Collection<Churuata> remove = (Collection<Churuata>) event.getData();
			//database.removeOnDescriptorId(remove.getID(), model.getData().getID());
			break;
		default:
			break;
		}
	}

	/**
	 * Response to a changed attribute
	 * @param event
	 * @param attribute
	 */
	protected void onVerifyText( VerifyEvent event, String attribute ){
		try {
			if( event.widget.getData() instanceof InputField ){
				InputField ifc = (InputField) event.widget.getData();
				ifc.refresh();
			}
				
			this.enableWidgets( event, attribute );
			if( this.isFilled() )
				this.notifyInputEdited( new EditEvent<OrganisationData>( this, EditTypes.COMPLETE, getInput()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isFilled(){
		boolean filled = !StringUtils.isEmpty( nameField.getText());
		if( !filled )
			return false;
		filled = !StringUtils.isEmpty( this.descriptionField.getText());
		if( !filled )
			return false;
		filled = !StringUtils.isEmpty( txtURL.getText());
		if( !filled )
			return false;
		return !Utils.assertNull( this.churuataTypesTable.getInput());
	}

	private final boolean enableWidgets(VerifyEvent event, String attribute){
		boolean enable = true;//!StringUtils.isEmpty( nameField.getText()) && !StringUtils.isEmpty( txtURL.getText() );
		return enable;
	}

	@Override
	public boolean checkRequiredFields() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private class WebController extends AbstractHttpRequest<IChuruata.Requests>{
		
		public WebController() {
			super();
		}

		public void setInput(String context, String path) {
			super.setContextPath(context + path);
		}

		public void register( IChuruata churuata ) {
			Map<String, String> params = getUserParams(user);
			try {
				params.put(OrganisationData.Parameters.NAME.toString(), churuata.getName() );
				params.put(OrganisationData.Parameters.DESCRIPTION.toString(), churuata.getDescription());
				
				IChuruataService ct = churuata.getTypes()[0];
				params.put(OrganisationData.Parameters.TYPE.toString(), String.valueOf( ct.getService().name()));
				params.put(OrganisationData.Parameters.LATITUDE.toString(), String.valueOf( churuata.getLocation().getLatitude()));
				params.put(OrganisationData.Parameters.LONGITUDE.toString(), String.valueOf( churuata.getLocation().getLongitude()));
				sendGet(IChuruata.Requests.REGISTER, params);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public Map<String, String> getUserParams( ILoginUser user) {
			Map<String, String> params = new HashMap<>();
			if( user != null ) {
				params.put(OrganisationData.Parameters.USER_ID.toString(), String.valueOf( user.getId() ));
				params.put(OrganisationData.Parameters.SECURITY.toString(), String.valueOf( user.getSecurity() ));
			}
			return params;
		}

		@Override
		protected String onHandleResponse(ResponseEvent<Requests> event) throws IOException {
			switch( event.getRequest()){
			case REGISTER:
				notifyInputEdited( new EditEvent<OrganisationData>( this, EditTypes.CHANGED));
				break;
			default:
				break;
			}
			return null;
		}		
	}
}