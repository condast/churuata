/*******************************************************************************
 * Copyright (c) 2014 Chaupal.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0.html
 *******************************************************************************/
package org.churuata.digital.ui.image;

import org.condast.commons.strings.StringStyler;
import org.condast.commons.ui.image.AbstractImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @See: https://www.iconfinder.com/savlon
 * @author Condast
 *
 */
public class BannerImages extends AbstractImages{

	public static final String BUNDLE_ID = "org.churuata.digital.ui";

	public static final String S_ICON_PATH = "/banner/";
	
	public enum Images{
		RU,
		CONDAST,
		DIRKSEN;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
		
		public static String getResource( Images image ){
			String str = null;
			switch( image ){
			case RU:
				str = "ru.png";
				break;
			case CONDAST:
				str = "condast.png";
				break;
			case DIRKSEN:
				str = "dirksen.jpg";
				break;
			default:
				str = image.name().toLowerCase();
				str = str.replace("_", "-");
				str += "-32.png";
				break;
			}
			return str;
		}
	}

	private static BannerImages images = new BannerImages();
	
	private BannerImages() {
		super( S_ICON_PATH, BUNDLE_ID );
	}

	/**
	 * Get an instance of this map
	 * @return
	 */
	public static BannerImages getInstance(){
		return images;
	}
	
	@Override
	public void initialise(){
		for( Images img: Images.values() )
			setImage( Images.getResource( img ));
	}
	
	public void setImage( String name, String url ){
		super.setImage(name);
	}

	/**
	 * Get the image
	 * @param desc
	 * @return
	 */
	public static Image getImage( Images image ){
		return getInstance().getImageFromName( Images.getResource(image));
	}

	/**
	 * Get the image
	 * @param desc
	 * @return
	 */
	public static Image getImage( String name ){
		return getInstance().getImageFromName( name);
	}
	
	public static Image getImageFromResource( Display display, Images image ){
		return getImageFromResource(display, BannerImages.class, S_ICON_PATH + Images.getResource(image));
	}
}