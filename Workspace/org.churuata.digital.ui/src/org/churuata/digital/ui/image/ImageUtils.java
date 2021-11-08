package org.churuata.digital.ui.image;

import java.io.InputStream;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ImageUtils
{

	public ImageUtils()
	{
	}
	
	/**
	 * Get an image from the given input stream
	 * @param display
	 * @param in
	 * @return
	 */
	public static Image getImage( Display display, InputStream in ){
		return new Image(display, in );
 	}

	/**
	 * Get a color
	 * @param display
	 * @param in
	 * @return
	 */
	public static Color getColor( Display display, int SWTColor ){
    return display.getSystemColor( SWTColor);
 	}
}
