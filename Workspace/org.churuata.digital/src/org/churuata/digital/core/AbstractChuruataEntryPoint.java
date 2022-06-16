package org.churuata.digital.core;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.churuata.digital.session.SessionStore;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.ui.entry.IDataEntryPoint;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public abstract class AbstractChuruataEntryPoint extends AbstractEntryPoint implements IDataEntryPoint<SessionStore>, AutoCloseable{
	private static final long serialVersionUID = 1L;

	public static final String S_CHURUATA = "churuata";
	public static final String S_PAGE = "page";

	public static final String S_CHURUATA_RESOURCES = "/resources/index.html";

	public static final String S_INVALID_PREPARATION = "Login first";

	public static final int DEFAULT_SCHEDULE = 1000; //milliseconds
	public static final int DEFAULT_SESSION_TIMEOUT= 9000 ; //15 minutes seconds

	private RWTUiSessionHandler handler;

	private String message;

	private SessionStore store;

	private ScheduledExecutorService timer;
	private int startTime, rate;
	
	protected AbstractChuruataEntryPoint() {
		this( DEFAULT_SCHEDULE, DEFAULT_SCHEDULE);
	}

	protected AbstractChuruataEntryPoint( int startTime, int rate ) {
		super();
		this.startTime = startTime;
		this.rate = rate;
		this.message = S_INVALID_PREPARATION;
	}

	protected SessionStore getSessionStore() {
		return store;
	}

	@Override
	public void setData( SessionStore store) {
		this.store = store;
		HttpSession session = RWT.getUISession().getHttpSession();
		if( this.store != null )
			session.setAttribute(IDomainProvider.Attributes.TOKEN.name(), store.getToken());
		session.setMaxInactiveInterval(DEFAULT_SESSION_TIMEOUT);
	}

	/**
	 * Try to retrieve the token. Use the one stored
	 * in the session store if possible, otherwise see
	 * if it is also present in the http session
	 * @return
	 */
	protected long getToken() {
		if( this.store != null )
			return store.getToken();
		HttpSession session = RWT.getUISession().getHttpSession();
		if( session.getAttribute(IDomainProvider.Attributes.TOKEN.name()) == null )
			return -1;
		return (long) session.getAttribute(IDomainProvider.Attributes.TOKEN.name());
	}
	
	protected Locale onSetLocale() {
		return new Locale( "nl", "NL" );
	}

	protected abstract boolean prepare( Composite parent );

	protected abstract Composite createComposite( Composite parent  );

	protected void handleTimer() {
		/* default nothing */
	}

	protected boolean postProcess( Composite parent ) {
		RWT.getUISession().getHttpSession().setMaxInactiveInterval( DEFAULT_SESSION_TIMEOUT);
		return true;
	}

	protected String createMessage( ) {
		return message;
	}

	protected void setMessage(String message) {
		this.message = message;
	}

	protected void createTimer( boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate ) {
		if(!create)
			return;
		timer = Executors.newScheduledThreadPool(nrOfThreads);
		timer.scheduleAtFixedRate(()->handleTimer(), startTime, rate, unit);
	}

	@Override
	protected void createContents(Composite parent) {
		try{
			this.message = S_INVALID_PREPARATION;
			if( !prepare( parent )) {
				Label label = new Label( parent, SWT.NONE);
				label.setText( createMessage());
				return;
			}
			parent.addDisposeListener( e->close());
			handler = new RWTUiSessionHandler(parent.getDisplay());
			//Set the RWT Locale
			parent.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
			Locale locale = onSetLocale();
			RWT.setLocale(locale);
			RWT.getUISession().setLocale(locale);
			Locale.setDefault( locale );
	        parent.setLayout(new FillLayout( SWT.VERTICAL));
			Composite composite = createComposite(parent );
			composite.setData( RWT.CUSTOM_VARIANT, S_CHURUATA );
			composite.addDisposeListener(e->close());
			createTimer(false, 1, TimeUnit.MILLISECONDS, this.startTime, this.rate);
			if(!postProcess(parent)) {
				message = getClass().getName() + ": " + message;
				throw new Exception( createMessage());
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	protected void stopTimer() {
		this.timer.shutdown();
	}

	/**
	 * Handles a session time out. If a false is returned, the time out is stopped
	 * @param reload
	 * @return
	 */
	protected boolean handleSessionTimeout( boolean reload ) {
		return true;
	}
	
	protected void setHttpSessionTimeout( int timeout ) {
		RWT.getUISession().getHttpSession().setMaxInactiveInterval(timeout);
	}

	@Override
	public  void close(){
		try {
			if( timer != null )
				timer.shutdown();
			handler.dispose();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private class RWTUiSessionHandler extends org.condast.commons.ui.rwt.AbstractRWTSessionSupport{

		public RWTUiSessionHandler(Display display) {
			super(display, Integer.MAX_VALUE);
		}

		@Override
		protected void onHandleTimeout(boolean reload) {
			try {
				Logger logger = Logger.getLogger(AbstractChuruataEntryPoint.class.getName());
				if(!handleSessionTimeout( reload )) {
					return;
				}
				logger.info("Invalidating session");
				close();
				AuthenticationDispatcher.getInstance().logout( store.getLoginUser());
				store.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}