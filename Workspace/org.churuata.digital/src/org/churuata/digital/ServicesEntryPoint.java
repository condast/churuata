package org.churuata.digital;

import java.util.concurrent.TimeUnit;

import org.churuata.digital.session.SessionStore;
import org.churuata.digital.ui.views.ChuruataTableComposite;
import org.condast.commons.config.Config;
import org.condast.commons.ui.entry.AbstractRestEntryPoint;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;


public class ServicesEntryPoint extends AbstractRestEntryPoint<SessionStore> {
	private static final long serialVersionUID = 1L;

	public static final String S_CHURUATA = "Churuata-Digital";

	public static final String S_CHURUATA_PAGE = "/churuata";

	private ChuruataTableComposite churuataComposite;
	
	@Override
	protected boolean prepare(Composite parent) {
		return true;
	}

	@Override
    protected Composite createComposite(Composite parent) {
        parent.setLayout(new FillLayout());
        churuataComposite = new ChuruataTableComposite( parent, SWT.NONE);
 		churuataComposite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
 		return churuataComposite;
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
		super.close();
	}	
}
