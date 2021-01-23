package org.churuata.digital.ui.views;

import org.churuata.digital.core.location.Churuata;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.ui.image.ImageUtils;
import org.condast.commons.ui.controller.AbstractEntityComposite;
import org.condast.commons.ui.table.TableEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;

public class EditChuruataComposite extends AbstractEntityComposite<IChuruata>
{
	private static final long serialVersionUID = 7782765745284140623L;

	public static final String S_SHOW_LINK = "Show Link";
	public static final String S_URL = "url";
	public static final String S_SHOWLINK_DIALOG_ID = "showlink.dialog";
	public static final String S_LINK_DIALOG_SHELL = "LinkDialogShell";
	public static final String REGEXP = ("[\\ ;:,]");
	private static final String S_INFORMATION_IMAGE = "/icons/information-icon.png";

	private static final String S_ADD_IMAGE = "/icons/add-32.png";
	

/*
	private InputField nameField;
	private InputField descriptionField;
	private InputField txtURL;
	private AttributeFieldComposite scopeField;
	private Combo comboScope;
	private ComboScopeController csc;
*/		
	private ChuruataTableComposite churuataTypesTable;

	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public EditChuruataComposite( Composite parent, int style ){
		super(parent, style );
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
				
		Group grpFillIn = new Group(container, SWT.NONE);
		grpFillIn.setText("Details:");
		grpFillIn.setLayout(new GridLayout(2, false));
		grpFillIn.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1 ));	
	/*	
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
		gridData_2 = new GridData();
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
		gridData_3 = new GridData();
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
		this.scopeField.setLabel( S_SCOPE + ": ");
		this.scopeField.setLabelWidth(115);
		this.scopeField.setIconSize(17);
		this.scopeField.setInformationMessage( S_SCOPE_INFORMATION_TIP);
		
		GridData gd_scope = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_scope.widthHint = 295;
		gd_scope.heightHint = 40;
		this.scopeField.setLayoutData(gd_scope);

		comboScope = new Combo(this.scopeField, SWT.BORDER);
		GridData gd_comboScope = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_comboScope.widthHint = 149;
		comboScope.setLayoutData(gd_comboScope);
		comboScope.addVerifyListener(new VerifyListener() {
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
		txtURL.setEditable(false);

		Group grpCategory = new Group(container, SWT.BORDER);
		grpCategory.setText("Category:");
		grpCategory.setLayout(new GridLayout(2, false));
		grpCategory.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1 ));	
*/		
		Image image = ImageUtils.getImage(getDisplay(), this.getClass().getResourceAsStream( S_ADD_IMAGE ));
		
		Button addButton = new Button( container, SWT.None );
		addButton.setLayoutData(new GridData( SWT.FILL, SWT.FILL, false, false, 1, 2));
		addButton.setImage(image);
		addButton.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
			}
		});

		this.churuataTypesTable = new ChuruataTableComposite(container, SWT.NONE);
		//this.categoryTable.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true ));	
	}

	/**
	 * Initialise the composite
	 * @param parent
	 */
	protected void initComponent( Composite parent ){
		
		Image image = ImageUtils.getImage( parent.getDisplay(), this.getClass().getResourceAsStream( S_INFORMATION_IMAGE ));
		//nameField.setImage( image );
		//descriptionField.setImage(image);
		//scopeField.setImage(image);
		
		parent.requestLayout();
	}

	@Override
	public void update(){
		IChuruata result = super.getInput();
	}

	@Override
	protected IChuruata onGetInput(IChuruata input) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void onSetInput(IChuruata input, boolean overwrite) {
		// TODO Auto-generated method stub
		
	}

	public void setInput( IChuruata input ){
		super.setInput( input, false );
		this.churuataTypesTable.setInput(input.getTypes());
	}

	protected void onNotifyTableEvent( TableEvent<Churuata> event ) {
		switch( event.getTableEvent()) {
		case DELETE:
			IChuruata model = getInput();
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

	private boolean isFilled(){
		/*
		boolean filled = !StringUtils.isEmpty( nameField.getText());
		if( !filled )
			return false;
		filled = !StringUtils.isEmpty( txtURL.getText());
		if( !filled )
			return false;
		filled = !StringUtils.isEmpty( comboScope.getText());
		*/
		return true;//filled;
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