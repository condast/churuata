package org.churuata.digital.entries;

import java.io.Closeable;
import java.io.IOException;
import java.util.Locale;

import org.churuata.digital.BasicApplication;
import org.churuata.digital.ui.banner.Banner;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class BannerEntryPoint extends AbstractEntryPoint implements Closeable{
	private static final long serialVersionUID = 1L;

	private Banner banner;
	
	@Override
	protected void createContents(Composite parent) {
		try{
			parent.setData( RWT.CUSTOM_VARIANT, BasicApplication.S_CHURUATA_VARIANT );
			Locale locale = new Locale( "nl", "NL" );
			RWT.setLocale(locale);
			RWT.getUISession().setLocale(locale);
			Locale.setDefault( locale );
	        parent.setLayout(new FillLayout()); 
			this.banner = new Banner( parent, SWT.RIGHT_TO_LEFT );
			banner.setData( RWT.CUSTOM_VARIANT, BasicApplication.S_CHURUATA_VARIANT );
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException { /* NOTHING */} 
}