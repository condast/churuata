package org.churuata.digital.ui.views;

import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.core.location.ChuruataData;
import org.condast.commons.Utils;
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

public class EditPersonComposite extends AbstractEntityComposite<ProfileData>
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
	
	private OrganisationTableComposite organisationTable;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public EditPersonComposite( Composite parent, int style ){
		super(parent, style );
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

		this.organisationTable = new OrganisationTableComposite(container, SWT.NONE);
		this.organisationTable.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true ));	
		this.organisationTable.addEditListener(e->notifyInputEdited(new EditEvent<ProfileData>( this, EditTypes.ADDED)));
	}

	@Override
	public void update(){
		ProfileData result = super.getInput();
	}

	@Override
	protected ProfileData onGetInput(ProfileData input) {
		//input.setName(this.nameField.getText());
		//input.setDescription( this.descriptionField.getText());
		//input.setWebsite( this.txtURL.getText());
		return input;
	}

	@Override
	protected void onSetInput(ProfileData input, boolean overwrite) {
		//latlngLabel.setText(input.getLocation().toLocation());
		this.nameField.setText(input.getName());
		//this.descriptionField.setText(input.getDescription());
		//this.txtURL.setText(input.getWebsite());
		//this.churuataTypesTable.setInput(input);
	}

	public void setInput( ProfileData input ){
		super.setInput( input, false );
	}

	protected void onNotifyTableEvent( TableEvent<ChuruataData> event ) {
		switch( event.getTableEvent()) {
		case DELETE:
			ProfileData model = getInput();
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
				this.notifyInputEdited( new EditEvent<ProfileData>( this, EditTypes.COMPLETE, getInput()));
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
		return !Utils.assertNull( this.organisationTable.getInput());
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
}