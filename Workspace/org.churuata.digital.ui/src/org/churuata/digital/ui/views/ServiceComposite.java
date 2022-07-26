package org.churuata.digital.ui.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import org.churuata.digital.core.data.ServiceData;
import org.churuata.digital.core.location.IChuruataService;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.AbstractEntityComposite;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.date.DateUtils;
import org.condast.commons.ui.na.NALanguage;
import org.condast.commons.ui.swt.AttributeFieldComposite;
import org.condast.commons.ui.swt.InputField;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class ServiceComposite extends AbstractEntityComposite<IChuruataService>
{
	private static final long serialVersionUID = 7782765745284140623L;

	public static final String S_NAME = "Churuata";
	public static final String REGEXP = ("[\\ ;:,]");

	private static final String S_SERVICES = "Service: ";
	private static final String S_CONTRIBUTION = "Contribution: ";
	private static final String S_CHURUATA_INFORMATION_TIP = "The type of churuata";
	private static final String S_NAME_INFORMATION_TIP = "The Churuata name";
	private static final String S_FROM = "From: ";
	private static final String S_TO = "To: ";
	
	private AttributeFieldComposite serviceField;
	private Combo serviceTypes;

	private InputField churuataField;

	private AttributeFieldComposite contributionField;
	private Combo contributionTypes;

	private DateTime fromField;
	private DateTime toField;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ServiceComposite( Composite parent, int style ){
		super(parent, style );
		this.serviceTypes.setItems(IChuruataService.Services.getItems());
		this.serviceTypes.select(1);
		this.contributionTypes.setItems(IChuruataService.Contribution.getItems());
		this.contributionTypes.select(0);
		Calendar calendar = Calendar.getInstance();
		DateUtils.setDate(fromField, calendar.getTime());
		calendar.add(Calendar.YEAR, 1);
		DateUtils.setDate(toField, calendar.getTime());
	}
	

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected void createComposite( Composite parent, int style )
	{
		ServiceComposite container = this;
		this.setLayout( new GridLayout(1, false));

		setData( RWT.CUSTOM_ITEM_HEIGHT, Integer.valueOf( 10 ));
				
		Group grpFillIn = new Group(container, SWT.NONE);
		grpFillIn.setText("Churuata:");
		grpFillIn.setLayout(new GridLayout(2, false));
		grpFillIn.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1 ));	
		

		this.serviceField = new AttributeFieldComposite( grpFillIn, SWT.NONE );
		this.serviceField.setLabel( S_SERVICES + ": ");
		this.serviceField.setLabelWidth(115);
		this.serviceField.setIconSize(17);
		this.serviceField.setInformationMessage( S_CHURUATA_INFORMATION_TIP);
		
		GridData gd_scope = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_scope.widthHint = 295;
		gd_scope.heightHint = 40;
		this.serviceField.setLayoutData(gd_scope);

		serviceTypes = new Combo(this.serviceField, SWT.BORDER);
		GridData gd_comboScope = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_comboScope.widthHint = 149;
		serviceTypes.setLayoutData(gd_comboScope);
		serviceTypes.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;
	
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					EditEvent.EditTypes type = isFilled()? EditEvent.EditTypes.COMPLETE: EditEvent.EditTypes.CHANGED;
					IChuruataService input = getInput();
					if( input != null )
						notifyInputEdited( new EditEvent<IChuruataService>( this, type, getInput()));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});		
		
		churuataField = new InputField( grpFillIn, SWT.NONE );
		churuataField.setBackgroundControl(1);
		churuataField.setLabel( S_NAME + ": ");
		churuataField.setLabelWidth(85);
		churuataField.setInformationMessage( S_NAME_INFORMATION_TIP );
		churuataField.setBackgroundControl(0);
		this.churuataField.addVerifyListener( new VerifyListener()
		{	
			private static final long serialVersionUID = 1L;

			@Override
			public void verifyText(VerifyEvent event) {
				try {
					onVerifyText(event, null);
					EditEvent.EditTypes type = isFilled()? EditEvent.EditTypes.COMPLETE: EditEvent.EditTypes.CHANGED;
					IChuruataService input = getInput();
					if( input != null ) {
						input.setDescription(event.text);
						notifyInputEdited( new EditEvent<IChuruataService>( this, type, input));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		GridData gridData_2 = new GridData();
		gridData_2.horizontalSpan = 2;
		gridData_2.heightHint = 33;
		gridData_2.widthHint = 182;
		gridData_2.horizontalAlignment = GridData.FILL;
		gridData_2.grabExcessHorizontalSpace = true;
		churuataField.setLayoutData(gridData_2);	
								
		this.contributionField = new AttributeFieldComposite( grpFillIn, SWT.NONE );
		this.contributionField.setLabel( S_CONTRIBUTION + ": ");
		this.contributionField.setLabelWidth(115);
		this.contributionField.setIconSize(17);
		this.contributionField.setInformationMessage( S_CHURUATA_INFORMATION_TIP);
		
		GridData gd_contribution = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_contribution.widthHint = 295;
		gd_contribution.heightHint = 40;
		this.contributionField.setLayoutData(gd_scope);

		contributionTypes = new Combo(this.contributionField, SWT.BORDER);
		GridData gd_contributionScope = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_contributionScope.widthHint = 149;
		contributionTypes.setLayoutData(gd_comboScope);
		contributionTypes.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					EditEvent.EditTypes type = isFilled()? EditEvent.EditTypes.COMPLETE: EditEvent.EditTypes.CHANGED;
					notifyInputEdited( new EditEvent<IChuruataService>( this, type, getInput()));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});		
		
		Label fromLabel = new Label( grpFillIn, SWT.NONE);
		fromLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, true));
		fromLabel.setText( S_FROM);
		
		this.fromField = new DateTime( grpFillIn, SWT.BORDER);
		this.fromField.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, true));
		this.fromField.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				DateTime dateField = (DateTime) e.widget;
				IChuruataService input = getInput();
				input.setFrom(DateUtils.getDate(dateField));
				EditEvent.EditTypes type = isFilled()? EditEvent.EditTypes.COMPLETE: EditEvent.EditTypes.CHANGED;
				notifyInputEdited( new EditEvent<IChuruataService>( this, type, getInput()));
				super.widgetSelected(e);
			}	
		});

		Label toLabel = new Label( grpFillIn, SWT.NONE);
		toLabel.setLayoutData(new GridData( SWT.FILL, SWT.FILL, false, true));
		toLabel.setText( S_TO);
		
		this.toField = new DateTime( grpFillIn, SWT.BORDER);
		this.toField.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, true));
		this.toField.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				DateTime dateField = (DateTime) e.widget;
				IChuruataService input = getInput();
				input.setTo(DateUtils.getDate(dateField));
				EditEvent.EditTypes type = isFilled()? EditEvent.EditTypes.COMPLETE: EditEvent.EditTypes.CHANGED;
				notifyInputEdited( new EditEvent<IChuruataService>( this, type, getInput()));
				super.widgetSelected(e);
			}	
		});

	}

	public String[] getItems(){
		return this.serviceTypes.getItems();
	}
	
	public void setItems( Collection<IChuruataService.Services> types, String[] items ){
		this.serviceTypes.setItems(items);
		this.serviceTypes.select(0);
	}

	public void select(int ordinal) {
		this.serviceTypes.select(ordinal);	
	}

	@Override
	protected IChuruataService onGetInput(IChuruataService input) {
		if( input == null )
			input = new ServiceData();
		input.setFrom( DateUtils.getDate(fromField ));
		input.setTo( DateUtils.getDate(toField));
		input.setDescription( this.churuataField.getText() );
		input.setContribution( IChuruataService.Contribution.values()[ contributionTypes.getSelectionIndex()] );
		input.setService( IChuruataService.Services.values()[ this.serviceTypes.getSelectionIndex()]);
		if( input instanceof ServiceData ) {
			ServiceData sd = (ServiceData) input;
			sd.setContribution( input.getContribution());
		}
		return input;
	}

	@Override
	protected void onSetInput(IChuruataService input, boolean overwrite) {
		if( input == null )
			return;
		DateUtils.setDate(fromField, input.from());
		DateUtils.setDate(toField, input.to());
		this.churuataField.setText(input.getDescription( ) );
		this.contributionTypes.select(input.getContribution().ordinal());
		this.serviceTypes.select( input.getService().ordinal());
		if( input instanceof ServiceData ) {
			ServiceData sd = (ServiceData) input;
			sd.setContribution( input.getContribution());
		}
	}

	public void setInput( IChuruataService input ){
		super.setInput( input, false );
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
			if( this.isFilled() ) {
				this.notifyInputEdited( new EditEvent<IChuruataService>( this, EditTypes.COMPLETE, getInput()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isFilled(){
		boolean filled = !StringUtils.isEmpty( churuataField.getText());
		if( !filled )
			return false;
		return !StringUtils.isEmpty( this.churuataField.getText());
	}

	@Override
	public boolean checkRequiredFields() {
		return false;
	}


	public static void createContactTypes( ServiceComposite widget, Set<IChuruataService.Services> selection){
		Collection<String> contacts = new ArrayList<String>();
		for( IChuruataService.Services type: selection ){
			contacts.add( NALanguage.getInstance().getString( type ));
		}
		widget.setItems(selection, contacts.toArray( new String[ contacts.size() ]));
	}
}