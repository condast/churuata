package org.satr.arnac.authentication.core;

import java.io.IOException;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import org.condast.commons.authentication.callback.LoginUserCallback;
import org.condast.commons.authentication.ui.core.CallbackData;
import org.condast.commons.authentication.ui.dialog.AbstractLoginDialog;
import org.condast.commons.authentication.ui.dialog.AbstractLoginDialogImplemetation;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.config.Config;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.verification.IVerification;
import org.condast.commons.verification.IVerification.VerificationTypes;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.satr.arnac.authentication.services.LoginService;

/**
 * Handles the callbacks
 */
public class ArnacCallbackHandler implements CallbackHandler{

	public static final String S_ARNAC_AUTHENTICATION_PATH = "arnac/auth/";
	public static final String S_COE_RDM = "coe-rdm";

	public static final String S_ERR_NO_CONNECTION = "No Connection to the Database Service";
	public static final String S_MSG_ATTEMPTING_LOGIN = "Attemting to log in";
	public static final String S_MSG_USER_FOUND = "User found: ";


	private Display display;
	private LoginDialog dialog;
	
	private Config config;
	
	private boolean cancel;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public ArnacCallbackHandler() {
		this.config = new Config();
		this.cancel = false;
		config.setLegalContext(S_COE_RDM);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.security.auth.callback.CallbackHandler#handle(javax.security.auth
	 * .callback.Callback[])
	 */
	@Override
	public void handle( final Callback[] callbacks ) throws IOException {
		if( cancel )
			return;
		try{
			Dispatcher service = Dispatcher.getInstance();
			LoginService ls = new LoginService( service );
			if(!ls.isConnected()) {
				logger.warning( S_ERR_NO_CONNECTION );
				return;
			}

			this.display = Display.getDefault();
			String legal = config.getLegalContext();
			String tos = config.getTermsOfServiceURL( RWT.getLocale());
			String privacy = config.getPrivacyURL(RWT.getLocale());
			CallbackData cd = new CallbackData( AbstractLoginDialog.S_LOGIN, legal, tos, privacy );

			dialog = new LoginDialog( display.getActiveShell(), cd );
			dialog.setBlockOnOpen(true);
			dialog.setCallback(callbacks);
			int result = dialog.open(); 
			if(  result == Window.OK ) {
				dialog.close();
				return;
			}else if( result == Window.CANCEL) {
				dialog.close();
				cancel = true;
				return;
			}
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	private class LoginDialog extends AbstractLoginDialogImplemetation{
		private static final long serialVersionUID = 1L;

		protected LoginDialog(Shell parentShell, CallbackData data) {
			super(parentShell, data );
		}

		@Override
		protected Point getInitialSize() {
			return new Point(500, 450);
		}

		protected LoginUserCallback getLoginUserCallback() {
			for( Callback callback: super.getCallbacks() ) {
				if( callback instanceof LoginUserCallback )
					return (LoginUserCallback) callback;
			}
			return null;
		}
		
		@Override
		protected boolean onHandleLogin(SelectionEvent event) {
			try {
				logger.info(S_MSG_ATTEMPTING_LOGIN);
				LoginUserCallback lucb = getLoginUserCallback();
				if( lucb == null )
					return false;
				Dispatcher service = Dispatcher.getInstance();
				LoginService ls = new LoginService( service );
				
				boolean registering = super.isRegistering();
				ILoginUser user = null;
				if( registering ) {
					try{
						ls.open();
						user = ls.create(getName(), getPassword(), getEmail()); 
					}
					finally {
						ls.close();
					}
				}else
					user = ls.login( super.getName(), super.getPassword() );
				logger.info(S_MSG_USER_FOUND + ( user != null ));
				lucb.setUser(user);		
				return ( user != null );
			} catch (Exception e1) {
				e1.printStackTrace();
				return false;
			}
		}

		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			getButton( IDialogConstants.OK_ID).setEnabled(false);
		}

		@Override
		protected boolean isValidEntry() {
			if( !super.isValidEntry() )
				return false;
			boolean registering = super.isRegistering();
			Text confirmText = super.getConfirmText(); 
			if( registering && ( StringUtils.isEmpty( confirmText.getText() ) || !getPassword().equals( confirmText.getText())))
				return false;
			boolean valid = IVerification.VerificationTypes.verify( VerificationTypes.EMAIL, getEmailText().getText() );
			return registering?valid: true;
		}

		@Override
		protected void onModifyText(ModifyEvent event) {
			getButton( IDialogConstants.OK_ID).setEnabled(isValidEntry());
		}  
	}
}