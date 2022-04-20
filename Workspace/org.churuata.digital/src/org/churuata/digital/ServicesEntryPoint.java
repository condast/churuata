package org.churuata.digital;

import java.util.concurrent.TimeUnit;

import org.churuata.digital.BasicApplication.Pages;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.store.SessionStore;
import org.churuata.digital.ui.swt.ActiveToolBar;
import org.churuata.digital.ui.views.ChuruataTableComposite;
import org.condast.commons.config.Config;
import org.condast.commons.ui.controller.EditEvent;
import org.condast.commons.ui.controller.IEditListener;
import org.condast.commons.ui.entry.AbstractRestEntryPoint;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class ServicesEntryPoint extends AbstractRestEntryPoint<SessionStore> {
	private static final long serialVersionUID = 1L;

	public static final String S_CHURUATA = "Churuata-Digital";

	public static final String S_CHURUATA_PAGE = "/churuata";

	private ChuruataTableComposite churuataComposite;

	private ActiveToolBar toolbar;
	
	private long token;

	private IEditListener<String> listener = e->onButtonBackPressed(e);

	@Override
    protected Composite createComposite(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        churuataComposite = new ChuruataTableComposite( parent, SWT.NONE);
 		churuataComposite.setData( RWT.CUSTOM_VARIANT, BasicApplication.S_CHURUATA_VARIANT );
		churuataComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
 
		toolbar = new ActiveToolBar(parent, SWT.NONE);
		toolbar.setData( RWT.CUSTOM_VARIANT, BasicApplication.S_CHURUATA_VARIANT );
		toolbar.setText("Select");
		toolbar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		toolbar.addEditListener( listener);
		this.token = 1;
 		return churuataComposite;
    }

	private Object onButtonBackPressed(EditEvent<String> e) {
		Dispatcher.redirect( Pages.MAP, token);
		return null;
	}

		@Override
		protected boolean prepare(Composite parent) {
			return true;
		}

	@Override
	protected boolean postProcess(Composite parent) {
		Config config = new Config();
		churuataComposite.setInput(config.getServerContext());
		return super.postProcess(parent);
	}

	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, 10000);
	}

	@Override
	protected void handleTimer() {
		try {
			//churuataComposite.refresh();
			super.handleTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		toolbar.removeEditListener(listener);
		super.close();
	}	
}
