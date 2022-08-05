package org.churuata.digital.ui.views;

import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.model.IOrganisation;
import org.churuata.digital.ui.core.IChuruataThemes;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.controller.AbstractEntityComposite;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.EditEvent.EditTypes;
import org.condast.commons.ui.swt.InputField;
import org.condast.commons.ui.verification.VerificationUtils;
import org.condast.commons.verification.IVerification.VerificationTypes;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class OrganisationComposite extends AbstractEntityComposite<ChuruataOrganisationData>
{
	private static final long serialVersionUID = 7782765745284140623L;

	public static final String S_NAME = "Churuata";
	public static final String REGEXP = ("[\\ ;:,]");

	private static final String S_NAME_INFORMATION_TIP = "The Churuata name";
	private static final String S_DESCRIPTION = "Description";
	private static final String S_DESCRIPTOR_INFORMATION_TIP = "Describe the Churuata";
	private static final String S_WEBSITE = "Website";
	private static final String S_WEBSITE_INFORMATION_TIP = "Add the Web site";
	private static final String S_INCORRECT_WEBSITE = "Incorrect Website";
	private static final String S_ORGANISATION_TYPE = "Type";
	
	private InputField churuataField;
	private InputField descriptionField;
	private InputField websiteField;
	private Combo orgTypeCombo;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public OrganisationComposite( Composite parent, int style ){
		super(parent, style );
		this.orgTypeCombo.setItems( IOrganisation.OrganisationTypes.getItems());
		this.orgTypeCombo.select(0);
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
		grpFillIn.setData( RWT.CUSTOM_VARIANT, IChuruataThemes.RWT_CHURUATA);
		grpFillIn.setLayout(new GridLayout(2, false));
		grpFillIn.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1 ));	
				
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
					EditTypes type = isFilled()?EditTypes.COMPLETE: EditTypes.CHANGED;
					ChuruataOrganisationData input = getInput();
					if( input == null )
						return;
					input.setName(event.text);
					notifyInputEdited( new EditEvent<ChuruataOrganisationData>( this, type, input));
				} catch (Exception e) {
					e.printStackTrace();
				}		
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
				try {
					EditTypes type = isFilled()?EditTypes.COMPLETE: EditTypes.CHANGED;
					ChuruataOrganisationData input = getInput();
					if( input == null )
						return;
					input.setDescription(event.text);
					notifyInputEdited( new EditEvent<ChuruataOrganisationData>( this, type, input));
				} catch (Exception e) {
					e.printStackTrace();
				}		
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
				try {
					if( !VerificationUtils.defaultVerificationAction( event.text, websiteField.getControl(), VerificationTypes.WEBSITE, S_INCORRECT_WEBSITE))
						return;
					EditTypes type = isFilled()?EditTypes.COMPLETE: EditTypes.CHANGED;
					ChuruataOrganisationData input = getInput();
					if( input == null )
						return;
					input.setWebsite(event.text);
					notifyInputEdited( new EditEvent<ChuruataOrganisationData>( this, type, input));
				} catch (Exception e) {
					e.printStackTrace();
				}		
			}
		});

		Label typeLabel = new Label( grpFillIn, SWT.NONE );
		typeLabel.setText(S_ORGANISATION_TYPE);
		GridData gridData = new GridData( SWT.FILL, SWT.FILL, false, false);
		gridData.widthHint = 120;
		typeLabel.setLayoutData(gridData);
		
		orgTypeCombo = new Combo(grpFillIn, SWT.NONE);
		orgTypeCombo.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					EditTypes type = isFilled()?EditTypes.COMPLETE: EditTypes.CHANGED;
					ChuruataOrganisationData input = getInput();
					if( input == null )
						return;
					notifyInputEdited( new EditEvent<ChuruataOrganisationData>( this, type, input));
				} catch (Exception ex) {
					ex.printStackTrace();
				}		
				super.widgetSelected(e);
			}		
		});
		orgTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}
	
	@Override
	protected ChuruataOrganisationData onGetInput(ChuruataOrganisationData input) {
		if( input == null )
			input = new ChuruataOrganisationData();
		input.setDescription(this.descriptionField.getText());
		input.setName( this.churuataField.getText());
		input.setWebsite(this.websiteField.getText());
		input.setType( IOrganisation.OrganisationTypes.values()[ orgTypeCombo.getSelectionIndex() ]);
		return input;
	}

	@Override
	protected void onSetInput(ChuruataOrganisationData input, boolean overwrite) {
		if( input == null )
			return;
		this.descriptionField.setText( input.getDescription());
		this.churuataField.setText( input.getName());
		this.websiteField.setText(input.getWebsite());
		orgTypeCombo.select( input.getType().getIndex());
	}

	public boolean isFilled(){
		boolean filled = !StringUtils.isEmpty( churuataField.getText());
		if( !filled )
			return false;
		filled = !StringUtils.isEmpty( this.websiteField.getText());
		if( !filled )
			return false;
		return !StringUtils.isEmpty( this.descriptionField.getText());
	}

	@Override
	public boolean checkRequiredFields() {
		return this.isFilled();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}