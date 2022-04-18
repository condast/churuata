package org.churuata.digital.ui.views;

import org.churuata.digital.core.location.ChuruataData;
import org.churuata.digital.core.location.ChuruataType;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruataType;
import org.churuata.digital.ui.image.ChuruataImages;
import org.churuata.digital.ui.image.ImageUtils;
import org.churuata.digital.ui.image.ChuruataImages.Images;
import org.churuata.digital.ui.utils.RWTUtils;
import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.AbstractEntityComposite;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.swt.AttributeFieldComposite;
import org.condast.commons.ui.swt.InputField;
import org.condast.commons.ui.table.TableEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;

public class ShowChuruatasComposite extends AbstractEntityComposite<IChuruata>
{
	private static final long serialVersionUID = 7782765745284140623L;

	public static final String S_SHOW_CHURUATAS = "Add or Join a Churuata in your region";
	public static final String S_SHOWCHURUATE_DIALOG_ID = "showChuruata.dialog";
	public static final String S_CHURUATA_DIALOG_SHELL = "ChuruatasDialogShell";
	private static final String S_INFORMATION_IMAGE = "/icons/information-icon.png";

	private static final String S_NAME = "Name";
	private static final String S_DESCRIPTION = "Description";
	private static final String S_LOCATION = "Location";
	private static final String S_TYPE = "Type";

	private static final String S_NAME_INFORMATION_TIP = "Provide a name for the churuata.\n";
	private static final String S_DESCRIPTOR_INFORMATION_TIP = "Provide a description for this churuata";
	private static final String S_SCOPE_INFORMATION_TIP = "Set the type of the churuata";

	private Group grpFillIn;
	private InputField nameField;
	private InputField descriptionField;
	private InputField locationField;
	private AttributeFieldComposite typeField;
	private Combo comboTypes;
	//private ComboScopeController csc;
		
	private ShowChuruatasTableComposite churuataTypesTable;
	
	private LatLng location;
	
	private boolean addType;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ShowChuruatasComposite( Composite parent, int style, LatLng location ){
		super(parent, style );
		this.location = location;
		this.addType = false;
		initComponent(parent);
		//this.csc = new ComboScopeController(this.comboScope);
		//this.categoryTable.addTableEventListener(e-> onNotifyTableEvent(e ));
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
				
		grpFillIn = new Group(container, SWT.NONE);
		grpFillIn.setText("Details:");
		grpFillIn.setLayout(new GridLayout(3, false));
		grpFillIn.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1 ));	
		nameField = new InputField( grpFillIn, SWT.NONE );
		nameField.setBackgroundControl(1);
		nameField.setLabel( S_NAME + ": ");
		nameField.setLabelWidth(85);
		nameField.setInformationMessage( S_NAME_INFORMATION_TIP );
		nameField.setBackgroundControl(0);
		nameField.setEnabled(false);
		this.nameField.addVerifyListener( new VerifyListener()
		{	
			private static final long serialVersionUID = 1L;

			@Override
			public void verifyText(VerifyEvent event) {
				onVerifyText(event, null);
			}
		});
		nameField.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false, 3, 1 ));	
		
		descriptionField = new InputField(grpFillIn, SWT.NONE);
		descriptionField.setLabel( S_DESCRIPTION + ": ");
		descriptionField.setBackgroundControl(0);
		descriptionField.setLabelWidth(85);
		descriptionField.setInformationMessage(S_DESCRIPTOR_INFORMATION_TIP);
		descriptionField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1 ));
		descriptionField.setEnabled(false);
		this.descriptionField.addVerifyListener( new VerifyListener()
		{	
			private static final long serialVersionUID = 1L;

			@Override
			public void verifyText(VerifyEvent event) {
				onVerifyText(event, null);
			}
		});

		locationField = new InputField(grpFillIn, SWT.NONE);
		locationField.setLabel( S_LOCATION + ": ");
		locationField.setBackgroundControl(0);
		locationField.setLabelWidth(85);
		locationField.setInformationMessage(S_DESCRIPTOR_INFORMATION_TIP);
		locationField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1 ));
		locationField.setEnabled(false);
		
		this.typeField = new AttributeFieldComposite( grpFillIn, SWT.NONE );
		this.typeField.setLabel( S_TYPE + ": ");
		this.typeField.setLabelWidth(115);
		this.typeField.setIconSize(17);
		this.typeField.setInformationMessage( S_SCOPE_INFORMATION_TIP);
		this.typeField.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		this.typeField.setEnabled(false);
		
		comboTypes = new Combo(this.typeField, SWT.BORDER);
		GridData gd_comboScope = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_comboScope.widthHint = 149;
		comboTypes.setLayoutData(gd_comboScope);
		comboTypes.setEnabled(false);
		comboTypes.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if(!addType)
						return;
					Combo combo = (Combo) e.widget;
					IChuruata churuata = getInput();
					churuata.addType( null, IChuruataType.Types.values()[ combo.getSelectionIndex()] );
					addType = false;
					if( isFilled() )
						notifyInputEdited( new EditEvent<IChuruata>( this, EditEvent.EditTypes.COMPLETE, churuata ));
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		});		

		Button joinButton = new Button( grpFillIn, SWT.None );
		joinButton.setLayoutData(new GridData( SWT.RIGHT, SWT.FILL, false, false));
		joinButton.setText("Join");
		joinButton.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				RWTUtils.redirect("web/unity/index.html");
				super.widgetSelected(e);
			}
		});

		Button addButton = new Button( grpFillIn, SWT.None );
		addButton.setLayoutData(new GridData( SWT.RIGHT, SWT.FILL, false, false));
		try {
			ChuruataImages ci = ChuruataImages.getInstance();
			Image image = ci.getImage( Images.ADD);
			addButton.setImage(image);
		} catch (Exception e1) {
			addButton.setText("Add");
			e1.printStackTrace();
		}
		addButton.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				nameField.setEnabled(true);
				descriptionField.setEnabled(true);
				typeField.setEnabled(true);
				comboTypes.setEnabled(true);
				setInput( new ChuruataData( location ), true);
			}
		});

		this.churuataTypesTable = new ShowChuruatasTableComposite(container, SWT.NONE);
		this.churuataTypesTable.addTableEventListener( e -> onNotifyTableEvent(e));
		this.churuataTypesTable.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true ));	
	}

	/**
	 * Initialise the composite
	 * @param parent
	 */
	protected void initComponent( Composite parent ){
		
		try {
			this.comboTypes.setItems(IChuruataType.Types.getItems());
			this.comboTypes.select(0);
			this.locationField.setText(this.location.toLocation());
			//descriptionField.setImage(image);
			//scopeField.setImage(image);
			Image image = ImageUtils.getImage( parent.getDisplay(), this.getClass().getResourceAsStream( S_INFORMATION_IMAGE ));
			nameField.setImage( image );
			
			parent.requestLayout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(){
		IChuruata input = super.getInput();
		if( input == null )
			return;
		String text = this.nameField.getText();
		if( !StringUtils.isEmpty(text ))
			input.setName( this.nameField.getText());
		text = this.descriptionField.getText();
		if( !StringUtils.isEmpty(text ))
			input.setDescription( this.descriptionField.getText());
		input.setType( new ChuruataType( ChuruataType.Types.values()[ this.comboTypes.getSelectionIndex()] ));
	}

	@Override
	protected IChuruata onGetInput(IChuruata input) {
		return input;
	}


	@Override
	protected void onSetInput(IChuruata input, boolean overwrite) {
		if(!overwrite)
			return;
		this.nameField.setText(input.getName());
		this.descriptionField.setText(input.getDescription());
		ChuruataType.Types type = Utils.assertNull(input.getTypes())? IChuruataType.Types.COMMUNITY: input.getTypes()[0].getType();
		this.comboTypes.select( type .ordinal());	
	}

	public void setInput( ChuruataData[] input ){
		this.churuataTypesTable.setInput(input);
	}

	protected void onNotifyTableEvent( TableEvent<IChuruata> event ) {
		switch( event.getTableEvent()) {
		case SELECT:
			setInput(event.getData(), true);
			this.comboTypes.setEnabled(true);
			this.addType = true;
			break;
		case DELETE:
			IChuruata churuata = getInput();
			churuata.setName( this.nameField.getText());
			churuata.setDescription(this.descriptionField.getText());
			churuata.setType( new ChuruataType( IChuruataType.Types.values()[ this.comboTypes.getSelectionIndex()] ));
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
				
			if( this.isFilled() )
				this.notifyInputEdited( new EditEvent<IChuruata>( this, EditEvent.EditTypes.COMPLETE, getInput() ));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isFilled(){
		boolean filled = !StringUtils.isEmpty( nameField.getText());
		if( !filled )
			return false;
		return !StringUtils.isEmpty( descriptionField.getText());
	}

	@Override
	public boolean checkRequiredFields() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void dispose() {
		update();
		super.dispose();
	}
}