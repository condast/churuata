package org.churuata.digital.entries.register;

import java.util.Locale;

import org.churuata.digital.core.AbstractWizardEntryPoint;
import org.churuata.digital.core.Dispatcher;
import org.churuata.digital.core.Entries;
import org.churuata.digital.core.data.ChuruataOrganisationData;
import org.churuata.digital.core.data.ProfileData;
import org.churuata.digital.session.SessionStore;
import org.condast.commons.authentication.http.IDomainProvider;
import org.condast.commons.config.Config;
import org.condast.commons.legal.LegalUtils;
import org.condast.commons.legal.LegalUtils.Version;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.parser.AbstractResourceParser;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.ui.image.DashboardImages;
import org.condast.commons.ui.utils.RWTUtils;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

public class ShowLegalEntryPoint extends AbstractWizardEntryPoint<Browser, ChuruataOrganisationData>{
	private static final long serialVersionUID = 1L;

	public static final String S_TITLE = "Legal issues";

	public static final String S_RESOURCE_FILE = "/resources/ChuruataRegistration.html";

	public static final String S_CHURUATA_LEGAL = "churuata-legal";
	public static final String S_PRIVACY = "/privacy";
	public static final String S_TOS = "/terms-of-service";

	public static final String S_LEGAL_VERSION = "/version_1.0/";
	public static final String S_LEGAL_TERMS_OF_SERVICE = "terms-of-service.html";
	public static final String S_LEGAL_PRIVACY = "privacy.html";

	private static final String S_AGREEMENT = "By checking this button you agree to our: ";
	private static final String S_HREF = "<a href=\"";
	private static final String S_AGREEMENT_1 = "\">Terms of Use</a>";
	private static final String S_AND_OUR = " and our ";
	private static final String S_AGREEMENT_2 = "\"> Privacy Policy</a>";

	public enum LegalAttributes{
		NAME,
		TEAM;
		
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public static boolean isValid( String str ) {
			String test = StringStyler.styleToEnum(str);
			for( LegalAttributes attr: values()) {
				if( attr.name().equals(test))
					return true;
			}
			return false;
		}

		public String toAttribute(){
			return StringStyler.xmlStyleString(name());
		}

		public static LegalAttributes getAttribute( String str ){
			return LegalAttributes.valueOf( StringStyler.styleToEnum(str));
		}
		
	}
	private Browser browser;
	private Button agreementButton;
	private Link linktos;
	private Label lbl_andours;
	private Link linkpriv;

	private String licensePath;
	private String privacyPath;

	
	public ShowLegalEntryPoint() {
		super(S_TITLE);
	}

	@Override
	protected SessionStore createSessionStore() {
		StartupParameters service = RWT.getClient().getService( StartupParameters.class );
		IDomainProvider<SessionStore> domain = Dispatcher.getDomainProvider( service );
		return ( domain == null )? null: domain.getData();
	}

	@Override
	protected boolean onPrepare(SessionStore store) {
		return true;
	}

	@Override
	protected Browser onCreateComposite(Composite parent, int style) {
		browser = new Browser( parent, style );

		Composite agreementComposite = new Composite( parent, SWT.NONE);
		agreementComposite.setLayout( new GridLayout(4,false ));
		agreementComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true));
		agreementButton = new Button( agreementComposite, SWT.CHECK );
		agreementButton.setText( S_AGREEMENT );
		agreementButton.setLayoutData( new GridData( SWT.LEFT, SWT.FILL, false, false ));
		agreementButton.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = getBtnNext();
				button.setEnabled(agreementButton.getSelection());
				super.widgetSelected(e);
			}
		});

		linktos = new Link( agreementComposite, SWT.NONE );
		linktos.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
		linktos.setText( S_HREF + licensePath + S_AGREEMENT_1);
		linktos.setLayoutData(new GridData( SWT.FILL, SWT.FILL, false, false ));
		linktos.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Link link = (Link) e.widget;
					String path = (String) link.getData();
					RWTUtils.redirect(path);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

		lbl_andours=  new Label( agreementComposite, SWT.NONE );
		lbl_andours.setText( S_AND_OUR);
		lbl_andours.setLayoutData(new GridData( SWT.FILL, SWT.FILL ,false, false));

		linkpriv = new Link( agreementComposite, SWT.NONE );
		linkpriv.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
		linkpriv.setText( S_HREF + privacyPath + S_AGREEMENT_2);
		linkpriv.setLayoutData(new GridData( SWT.FILL, SWT.FILL, false, false));
		linkpriv.addSelectionListener( new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Link link = (Link) e.widget;
					String path = (String) link.getData();
					RWTUtils.redirect(path);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				super.widgetSelected(e);
			}
		});

		return browser;
	}

	public void setLicensePath(String licensePath) {
		this.licensePath = licensePath;
		linktos.setText( S_HREF + licensePath + ".html" + S_AGREEMENT_1);
		linktos.setData(licensePath);
	}

	public void setPrivacyPath(String privacyPath) {
		this.privacyPath = privacyPath;
		linkpriv.setText( S_HREF + privacyPath + ".html" + S_AGREEMENT_2);
		linkpriv.setData(privacyPath);
	}

	@Override
	protected void onButtonPressed(ChuruataOrganisationData data, SessionStore store) {
		store.setData(null);
		Config config = Config.getInstance();
		String path = config.getServerContext() + Entries.S_HOME;
		RWTUtils.jump( path );
	}

	@Override
	protected boolean onPostProcess(String context, SessionStore store) {
		Button button = getBtnNext();
		button.setImage(DashboardImages.getImage( DashboardImages.Images.ADD, 32));
		
		String root = context + S_CHURUATA_LEGAL;
		Locale locale = Locale.getDefault();
		String path = LegalUtils.createLegalPath(locale, Version.VERSION_1_0, root );
		setPrivacyPath( path + S_PRIVACY);
		setLicensePath( path + S_TOS);

		if( store.getData() == null )
			return false;
		ProfileData profile= store.getData();
		ChuruataOrganisationData organisation = (ChuruataOrganisationData) profile.getOrganisation()[0];
	
		FileParser parser = new FileParser( organisation, 0 );
		String str =null;
		try{
			str = parser.parse( this.getClass().getResourceAsStream(S_RESOURCE_FILE) );
			browser.setText(str);
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
		return true;
	}

	private class FileParser extends AbstractResourceParser{

		private long token;
		private ChuruataOrganisationData organisation;
		
		public FileParser(ChuruataOrganisationData organisation, long token) {
			super();
			this.organisation = organisation;
			this.token = token;
		}

		@Override
		protected String getToken() {
			return String.valueOf(token);
		}

		@Override
		protected String onHandleTitle(String subject, Attributes attr) {
			String result = null;
			switch( attr ){
			case HTML:
				result = "Churuata Digital";
				break;
			case PAGE:
				result = "Churuata Digital";
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		protected String onCreateList(String[] arguments) {
			StringBuilder builder = new StringBuilder();
			return builder.toString();
		}
	
		@Override
		protected String onCreateFrame(Attributes attr, String[] arguments) {
			StringBuilder builder = new StringBuilder();
			return builder.toString();
		}

		@Override
		protected String onHandleLabel(String id, Attributes attr) {
			String result = null;
			if( !LegalAttributes.isValid(id))
				return result;
			IContactPerson person = this.organisation.getContact();
			switch( LegalAttributes.getAttribute(id)) {
			case NAME:
				result = person.getName();
				break;
			case TEAM:
				result = "Churuata Team";
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		protected String onCreateLink(String link, String page, String arguments) {
			String result = "";
			return result;
		}
	}

}