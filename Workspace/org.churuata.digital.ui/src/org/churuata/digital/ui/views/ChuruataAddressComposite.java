package org.churuata.digital.ui.views;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import org.condast.commons.na.data.AddressData;

import org.condast.commons.ui.na.address.AddressComposite;
import org.eclipse.swt.SWT;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ChuruataAddressComposite extends AddressComposite{
	private static final long serialVersionUID = 1L;
	
	private Button btn_principal;
	
	public ChuruataAddressComposite(Composite parent, int style) {
		super(parent, style);
	}
	
	protected void createComposite( Composite parent, int style ){
		super.createComposite( parent, style );

		Composite entryComposite = super.getEntryComposite();
		btn_principal = new Button( entryComposite, SWT.CHECK );
		btn_principal.setEnabled(false);
		btn_principal.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}			
		});

	}

	@Override
	public AddressData onGetInput( AddressData input) {
		input = super.onGetInput(input);
		input.setPrincipal(btn_principal.getSelection());
		return input;
	}

	@Override
	public void onSetInput(AddressData input, boolean overwrite) {
		super.onSetInput(input, overwrite);
		if( input == null )
			return;
		btn_principal.setSelection( input.isPrincipal());
	}
}
