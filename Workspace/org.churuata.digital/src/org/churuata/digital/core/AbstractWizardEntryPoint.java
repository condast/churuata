package org.churuata.digital.core;

import java.util.concurrent.TimeUnit;

import org.churuata.digital.session.SessionStore;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.config.Config;
import org.condast.commons.ui.player.PlayerImages;
import org.condast.commons.ui.session.AbstractSessionHandler;
import org.condast.commons.ui.session.SessionEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

public abstract class AbstractWizardEntryPoint<C extends Composite, D extends Object> extends AbstractChuruataEntryPoint<D> {
	private static final long serialVersionUID = 1L;

	private C composite;
	private Button btnNext;

	private SessionHandler handler;
	
	private D data = null;

	protected abstract IDomainProvider<SessionStore<D>> getDomainProvider( StartupParameters service );
	
	@Override
	protected boolean prepare(org.eclipse.swt.widgets.Composite parent) {
		handler = new SessionHandler( parent.getDisplay());
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore<D>> domain = getDomainProvider( service );
		if( domain == null )
			return false;
		SessionStore<D> store = domain.getData();
		if( store == null )
			return false;
		setData(store);
		return true;
	}

	protected abstract C onCreateComposite( Composite parent, int style );

	protected abstract void onNextButtonPressed( D data, SessionStore<D> store );

	@Override
	protected org.eclipse.swt.widgets.Composite createComposite(org.eclipse.swt.widgets.Composite parent) {
        parent.setLayout(new GridLayout( 1, false ));
        composite = onCreateComposite( parent, SWT.NONE);
        if( composite != null ) {
        	composite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
        	composite.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true, true));
        }
        Group group = new Group( parent, SWT.NONE );
		group.setText("Add Churuata Service");
		group.setLayout( new GridLayout(5, false ));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		btnNext = new Button(group, SWT.NONE);
		btnNext.setEnabled(false);
		btnNext.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		btnNext.setImage( PlayerImages.getImage( PlayerImages.Images.NEXT));
		btnNext.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try{
					SessionStore<D> store = getSessionStore();
					onNextButtonPressed(data, store);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

 		return composite;
    }

	protected abstract boolean onPostProcess( String context, D data, SessionStore<D> store );

	@Override
	protected boolean postProcess( Composite parent) {
		Config config = new Config();
		String context = config.getServerContext();
		SessionStore<D> store = getSessionStore();
		if( !onPostProcess(context, data, store))
			return false;
		return super.postProcess(parent);
	}

	protected Button getBtnNext() {
		return btnNext;
	}
	
	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, 10000);
	}

	protected abstract void onHandleTimer( SessionEvent<D> event );

	@Override
	protected void handleTimer() {
		SessionStore<D> store = getSessionStore();
		handler.addData( store.getData());
		super.handleTimer();
	}

	@Override
	protected boolean handleSessionTimeout(boolean reload) {
		SessionStore<D> store = super.getSessionStore();
		store.setLoginUser(null);
		return super.handleSessionTimeout(reload);
	}

	private class SessionHandler extends AbstractSessionHandler<D>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<D> sevent) {
			onHandleTimer(sevent);
		}
	}
}
