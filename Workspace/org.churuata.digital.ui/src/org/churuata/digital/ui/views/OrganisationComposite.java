package org.churuata.digital.ui.views;

import java.util.Arrays;
import java.util.Calendar;

import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.condast.commons.Utils;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.AbstractEntityComposite;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.swt.InputField;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;

public class OrganisationComposite extends AbstractEntityComposite<ChuruataOrganisationData>
{
	private static final long serialVersionUID = 7782765745284140623L;

	public static final String S_NAME = "Churuata";
	public static final String REGEXP = ("[\\ ;:,]");

	public static final String S_ORGANISATION = "Churuata";

	private static final String S_NAME_INFORMATION_TIP = "The Churuata name";
	private static final String S_DESCRIPTION = "Description";
	private static final String S_DESCRIPTOR_INFORMATION_TIP = "Describe the Churuata";
	private static final String S_WEBSITE = "Website";
	private static final String S_WEBSITE_INFORMATION_TIP = "Add the Web site";
	
	private InputField churuataField;
	private InputField descriptionField;
	private InputField websiteField;
	
	private ServicesTableViewer viewer;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public OrganisationComposite( Composite parent, int style ){
		super(parent, style );
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
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
		grpFillIn.setText("Organisation:");
		grpFillIn.setLayout(new GridLayout(2, false));
		grpFillIn.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1 ));	
				
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
				onVerifyText(event, null);
				if( isFilled())
					notifyInputEdited( new EditEvent<ChuruataOrganisationData>( this, EditTypes.COMPLETE, getInput()));
			}
		});
		churuataField.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false,2,1 ));	
		
		descriptionField = new InputField(grpFillIn, SWT.NONE);
		descriptionField.setLabel( S_DESCRIPTION + ": ");
		descriptionField.setBackgroundControl(0);
		descriptionField.setLabelWidth(85);
		descriptionField.setInformationMessage(S_DESCRIPTOR_INFORMATION_TIP);
		descriptionField.setLayoutData(new GridData( SWT.FILL, SWT.FILL, true, false,2,1 ));
		this.descriptionField.addVerifyListener( new VerifyListener()
		{	
			private static final long serialVersionUID = 1L;

			@Override
			public void verifyText(VerifyEvent event) {
				onVerifyText(event, null);
				if( isFilled())
					notifyInputEdited( new EditEvent<ChuruataOrganisationData>( this, EditTypes.COMPLETE, getInput()));
			}
		});

		websiteField = new InputField(grpFillIn, SWT.NONE);
		websiteField.setLabel( S_WEBSITE + ": ");
		websiteField.setBackgroundControl(0);
		websiteField.setLabelWidth(85);
		websiteField.setInformationMessage(S_WEBSITE_INFORMATION_TIP);
		websiteField.setLayoutData(new GridData( SWT.FILL, SWT.FILL, true, false,2,1 ));
		this.websiteField.addVerifyListener( new VerifyListener()
		{	
			private static final long serialVersionUID = 1L;

			@Override
			public void verifyText(VerifyEvent event) {
				onVerifyText(event, null);
				if( isFilled())
					notifyInputEdited( new EditEvent<ChuruataOrganisationData>( this, EditTypes.COMPLETE, getInput()));
			}
		});
		
 		viewer = new ServicesTableViewer( this, SWT.NONE );
		viewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyInputEdited( new EditEvent<ChuruataOrganisationData>( this, EditTypes.ADDED, getInput()));
				super.widgetSelected(e);
			}
		});
	}

	@Override
	protected ChuruataOrganisationData onGetInput(ChuruataOrganisationData input) {
		if( input == null )
			input = new ChuruataOrganisationData();
		input.setDescription(this.descriptionField.getText());
		input.setName( this.churuataField.getText());
		input.setWebsite(this.websiteField.getText());
		if( Utils.assertNull( viewer.getInput()))
			return input;
		input.setChuruataServices( viewer.getInput());
		return input;
	}

	@Override
	protected void onSetInput(ChuruataOrganisationData input, boolean overwrite) {
		this.descriptionField.setText( input.getDescription());
		this.churuataField.setText( input.getName());
		this.websiteField.setText(input.getWebsite());
		viewer.setInput( Arrays.asList( input.getServices()));
	}

	public void setInput( ChuruataOrganisationData input ){
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
				//OrganisationData.Types ct = OrganisationData.Types.values()[ this.comboTypes.getSelectionIndex()];
				//OrganisationData.Contribution cot = OrganisationData.Contribution.values()[ this.contributionTypes.getSelectionIndex()];
				//OrganisationData type = new ChuruataType( ct, this.descriptionField.getText(), cot );
				//type.setFrom( DateUtils.getDate( this.fromField ));
				//type.setTo( DateUtils.getDate( this.toField ));
				//setInput(type);
				this.notifyInputEdited( new EditEvent<ChuruataOrganisationData>( this, EditTypes.COMPLETE, getInput()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isFilled(){
		boolean filled = !StringUtils.isEmpty( churuataField.getText());
		if( !filled )
			return false;
		filled = !StringUtils.isEmpty( this.websiteField.getText());
		if( !filled )
			return false;
		filled = !StringUtils.isEmpty( this.descriptionField.getText());
		if( !filled )
			return false;
		return !Utils.assertNull( this.viewer.getInput());
	}

	@Override
	public boolean checkRequiredFields() {
		return this.isFilled();
	}
}