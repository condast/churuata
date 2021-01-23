package org.churuata.digital.ui.image;

import java.io.IOException;
import java.io.InputStream;

import org.churuata.digital.Activator;
import org.condast.commons.ui.image.AbstractImages;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.swt.graphics.Image;

public class InformationImages extends AbstractImages implements IInformationImages{

	private static InformationImages images = new InformationImages();

	private InformationImages() {
		super( S_ICON_PATH, Activator.BUNDLE_ID );
	}

	public static InformationImages getInstance(){
		return images;
	}

	@Override
	protected void initialise(){
		for( Information inf: Information.values() )
			setImage( Information.convert(inf));
		setImage( Information.convert_disable( Information.INFORMATION ));
		setImage( Information.convert_disable( Information.DELETE ));
	}

	@Override
	public Image getImage( Information desc, boolean enable ){
		String location = (enable)? Information.convert( desc ): Information.convert_disable( desc );
		Image image = getImageFromName( location );
		return image;
	}
	
	/**
	 * Add a resource
	 * @param resourceName
	 * @param location
	 */
	protected static void addResource( Application application, String resourceName, final String location ){
		application.addResource( resourceName, new ResourceLoader() {
			@Override
			public InputStream getResourceAsStream( String resourceName ) throws IOException {
				return this.getClass().getClassLoader().getResourceAsStream( location );
			}
		});
	}
}