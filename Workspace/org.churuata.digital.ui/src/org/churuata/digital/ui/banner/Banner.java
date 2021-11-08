package org.churuata.digital.ui.banner;

import org.churuata.digital.ui.image.BannerImages;
import org.condast.commons.ui.banner.AbstractBanner;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class Banner extends AbstractBanner {
	private static final long serialVersionUID = 1L;

	public static final String S_CONDAST_URL = "http://www.condast.com/";
	public static final String S_DIRKSEN_URL = "http://www.dirksen.nl/";
	public static final String S_RU_URL = "https://twitter.com/refugiadosuorg?s=11";

	public Banner(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createBanner(Composite comp, int style) {
		Button button = createImageButton( comp, BannerImages.getImage( BannerImages.Images.RU ), SWT.FLAT );
		button.setData( S_RU_URL);
		button = createImageButton( comp, BannerImages.getImage( BannerImages.Images.CONDAST ), SWT.FLAT );
		button.setData( S_CONDAST_URL );
		button = createImageButton( comp, BannerImages.getImage( BannerImages.Images.DIRKSEN ), SWT.FLAT );
		button.setData( S_DIRKSEN_URL );
	}

	@Override
	protected void onHandleButtonSelect( final Button button) {
		Display.getCurrent().asyncExec( new Runnable(){

			@Override
			public void run() {
				try{
					UrlLauncher launcher = RWT.getClient().getService( UrlLauncher.class );
					String url = (String) button.getData();
					launcher.openURL( url );
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}					
		});
	}
}
