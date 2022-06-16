package org.churuata.digital.ui.swt;

import java.util.ArrayList;
import java.util.Collection;

import org.churuata.digital.core.location.IChuruata;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class ActiveToolBar extends Group{
	private static final long serialVersionUID = 1L;

	private static final String S_BACK = "Back";

	private Button btnBack;

	private Collection<IEditListener<String>> listeners;

	public ActiveToolBar(Composite parent, int style) {
		super(parent, style);
		this.createComposite( parent, style);
		this.listeners = new ArrayList<>();
	}

	private void createComposite(Composite parent, int style) {
		setLayout( new GridLayout(6, false ));
		setData(RWT.CUSTOM_VARIANT, IChuruata.S_CHURUATA);

		btnBack = new Button(this, SWT.NONE);
		btnBack.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnBack.setText( S_BACK );
		btnBack.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyEditListeners( new EditEvent<String>( this ));
				super.widgetSelected(e);
			}
		});
	}

	public void addEditListener( IEditListener<String> listener ) {
		this.listeners.add(listener);
	}

	public void removeEditListener( IEditListener<String> listener ) {
		this.listeners.remove(listener);
	}

	protected void notifyEditListeners( EditEvent<String> event ) {
		for( IEditListener<String> listener: listeners)
			listener.notifyInputEdited( event );
	}

	public Button getButtonStart() {
		return btnBack;
	}
}