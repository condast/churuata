package org.churuata.digital.core;

import java.util.concurrent.TimeUnit;

import org.churuata.digital.session.SessionStore;
import org.condast.commons.config.Config;
import org.condast.commons.ui.player.PlayerImages;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public abstract class AbstractWizardEntryPoint<C extends Composite, D extends Object> extends AbstractChuruataEntryPoint<D> {
	private static final long serialVersionUID = 1L;

	private C composite;
	private Button btnNext;
	private String title;

	private D cache = null;
	
	private Group buttonBar;
	private boolean includeButtonBar;

	protected AbstractWizardEntryPoint( String title ) {
		this( title, true);
	}

	public AbstractWizardEntryPoint( String title, boolean includeButtonBar) {
		super();
		this.title = title;
		this.includeButtonBar = includeButtonBar;
	}
	
	protected abstract C onCreateComposite( Composite parent, int style );

	protected void onSetupButtonBar( Group buttonBar ) {
		//DEFAULT NOTHING
	}

	@Override
	protected org.eclipse.swt.widgets.Composite createComposite(org.eclipse.swt.widgets.Composite parent) {
		parent.setLayout(new GridLayout( 1, false ));
		composite = onCreateComposite( parent, SWT.NONE);
		if( composite != null ) {
			composite.setData( RWT.CUSTOM_VARIANT, Entries.S_CHURUATA );
			composite.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true));
		}

		if( this.includeButtonBar) {
			buttonBar = new Group( parent, SWT.NONE );
			buttonBar.setText(title + ":");
			buttonBar.setLayout( new GridLayout(5, false ));
			buttonBar.setData( RWT.CUSTOM_VARIANT, Entries.S_CHURUATA );
			buttonBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			btnNext = new Button(buttonBar, SWT.NONE);
			btnNext.setEnabled(false);
			btnNext.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
			btnNext.setImage( PlayerImages.getImage( PlayerImages.Images.NEXT));
			btnNext.addSelectionListener( new SelectionAdapter(){
				private static final long serialVersionUID = 1L;

				@Override
				public void widgetSelected(final SelectionEvent e) {
					try{
						SessionStore store = getSessionStore();
						onButtonPressed(cache, store);
					}
					catch( Exception ex ){
						ex.printStackTrace();
					}
					super.widgetSelected(e);
				}
			});
			onSetupButtonBar(buttonBar);
		}
		return composite;
	}

	protected abstract boolean onPostProcess( String context, SessionStore store );

	protected abstract void onButtonPressed( D data, SessionStore store );

	@Override
	protected boolean postProcess( Composite parent) {
		Config config = Config.getInstance();
		String context = config.getServerContext();
		SessionStore store = getSessionStore();
		if( !onPostProcess(context, store))
			return false;
		return super.postProcess(parent);
	}

	protected D getCache() {
		return cache;
	}

	protected void setCache(D data) {
		this.cache = data;
	}

	protected Button getBtnNext() {
		return btnNext;
	}
	
	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, 10000);
	}

	@Override
	protected boolean handleSessionTimeout(boolean reload) {
		SessionStore store = super.getSessionStore();
		store.setLoginUser(null);
		return super.handleSessionTimeout(reload);
	}
}
