package org.churuata.digital.ui.views;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.churuata.digital.core.location.ChuruataData;
import org.churuata.digital.core.location.ChuruataType;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruata.Requests;
import org.churuata.digital.core.rest.IRestPages;
import org.churuata.digital.core.location.IChuruataType;
import org.churuata.digital.ui.image.ChuruataImages;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.ui.controller.AbstractEntityComposite;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.swt.AttributeFieldComposite;
import org.condast.commons.ui.swt.InputField;
import org.condast.commons.ui.table.TableEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class EditChuruataComposite extends AbstractEntityComposite<LatLng>
{
	private static final long serialVersionUID = 7782765745284140623L;

	public static final String S_NAME = "Churuata";
	public static final String S_SHOW_LINK = "Show Link";
	public static final String S_URL = "url";
	public static final String S_SHOWLINK_DIALOG_ID = "showlink.dialog";
	public static final String S_LINK_DIALOG_SHELL = "LinkDialogShell";
	public static final String REGEXP = ("[\\ ;:,]");

	private static final String S_NAME_INFORMATION_TIP = "The Churuata name";
	private static final String S_DESCRIPTION = "Description";
	private static final String S_DESCRIPTOR_INFORMATION_TIP = "Describe the Churuata";
	private static final String S_SERVICES = "Services";
	private static final String S_CHURUATA_INFORMATION_TIP = "The type of churuata";
	private static final String S_WEBSITE = "Website";
	private static final String S_LOCATION = "Location:";
	
	public enum Parameters{
		ID,
		TOKEN,
		NAME,
		DESCRIPTION,
		LATITUDE,
		LONGITUDE,
		TYPE;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString( name());
		}
	}

	private InputField nameField;
	private InputField descriptionField;
	private InputField txtURL;
	private AttributeFieldComposite scopeField;
	private Combo comboTypes;
	
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
		this.comboTypes.setItems(IChuruataType.Types.getItems());
		this.comboTypes.select(0);
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
		
		this.scopeField = new AttributeFieldComposite( grpFillIn, SWT.NONE );
		this.scopeField.setLabel( S_SERVICES + ": ");
		this.scopeField.setLabelWidth(115);
		this.scopeField.setIconSize(17);
		this.scopeField.setInformationMessage( S_CHURUATA_INFORMATION_TIP);
		
		GridData gd_scope = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_scope.widthHint = 295;
		gd_scope.heightHint = 40;
		this.scopeField.setLayoutData(gd_scope);

		comboTypes = new Combo(this.scopeField, SWT.BORDER);
		GridData gd_comboScope = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_comboScope.widthHint = 149;
		comboTypes.setLayoutData(gd_comboScope);
		comboTypes.addVerifyListener(new VerifyListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void verifyText(VerifyEvent event) {
				try {
					//int index = comboScope.getSelectionIndex();
					//String item = comboScope.getItem( index );
				} catch (Exception e) {
					e.printStackTrace();
				}
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

		ChuruataImages image = ChuruataImages.getInstance();
		
		Button addButton = new Button( container, SWT.None );
		addButton.setLayoutData(new GridData( SWT.FILL, SWT.FILL, false, false, 1, 2));
		addButton.setImage(image.getImage(ChuruataImages.Images.ADD));
		addButton.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IChuruata churuata = new ChuruataData( getInput());
					churuata.setName(nameField.getText());
					churuata.setDescription( descriptionField.getText());
					churuata.setName(nameField.getText());
					churuata.setHomepage(txtURL.getText());
					
					IChuruataType ct = new ChuruataType( IChuruataType.Types.values()[ comboTypes.getSelectionIndex()]);
					churuata.setType(ct);
					controller.register(churuata);
					super.widgetSelected(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		this.churuataTypesTable = new ChuruataTableComposite(container, SWT.NONE);
		this.churuataTypesTable.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true ));	
	}

	public void setInput( String context, ILoginUser user ){
		controller.setInput(context, IRestPages.Pages.SUPPORT.toPath());
		this.user = user;
	}

	@Override
	public void update(){
		//LatLng result = super.getInput();
	}

	@Override
	protected LatLng onGetInput(LatLng input) {
		return input;
	}

	@Override
	protected void onSetInput(LatLng input, boolean overwrite) {
		latlngLabel.setText(input.toLocation());
		//this.churuataTypesTable.setInput(input.getTypes());
	}

	public void setInput( LatLng input ){
		super.setInput( input, false );
	}

	protected void onNotifyTableEvent( TableEvent<ChuruataData> event ) {
		switch( event.getTableEvent()) {
		case DELETE:
			//LatLng model = getInput();
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
			/*
			if( event.widget.getData() instanceof InputField ){
				InputField ifc = (InputField) event.widget.getData();
				ifc.refresh();
			}
				
			this.enableWidgets( event, attribute );
			if( this.isFilled() )
				this.notifyComponentChanged( new CompositeEvent( this, CompositeEvents.VERIFIED ));
				*/
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				params.put(Parameters.NAME.toString(), churuata.getName() );
				params.put(Parameters.DESCRIPTION.toString(), churuata.getDescription());
				
				IChuruataType ct = churuata.getTypes()[0];
				params.put(Parameters.TYPE.toString(), String.valueOf( ct.getType().name()));
				params.put(Parameters.LATITUDE.toString(), String.valueOf( churuata.getLocation().getLatitude()));
				params.put(Parameters.LONGITUDE.toString(), String.valueOf( churuata.getLocation().getLongitude()));
				sendGet(IChuruata.Requests.REGISTER, params);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public Map<String, String> getUserParams( ILoginUser user) {
			Map<String, String> params = new HashMap<>();
			if( user != null ) {
				params.put(Parameters.ID.toString(), String.valueOf( user.getId() ));
				params.put(Parameters.TOKEN.toString(), String.valueOf( user.getSecurity() ));
			}
			return params;
		}

		@Override
		protected String onHandleResponse(ResponseEvent<Requests> event) throws IOException {
			switch( event.getRequest()){
			case REGISTER:
				notifyInputEdited( new EditEvent<LatLng>( this, EditTypes.CHANGED, null));
				break;
			default:
				break;
			}
			return null;
		}		
	}
}