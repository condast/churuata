package org.churuata.digital.ui.image;

import org.churuata.digital.ui.Activator;
import org.condast.commons.ui.image.AbstractImages;
import org.eclipse.swt.graphics.Image;

/**
 * @see: https://www.iconfinder.com/savlon
 * @author Condast
 *
 */
public class ChuruataImages extends AbstractImages{

	public static final String S_DOUBLE_ARROW = "DOUBLE_ARROW";
	public static final String S_BATHYMETRY = "depth.jpg";
	private static final String S_LOCATED_IMAGE = "ledgreen";
	private static final String S_CONNECTED_IMAGE = "ledorange";
	private static final String S_DISCONNECTED_IMAGE = "ledlightblue";

	public static final String S_ZOOM_IN = "zoom-in";
	public static final String S_ZOOM_OUT = "zoom-out";

	public static final String S_PNG = ".png";

	public enum Images{
		CHURUATA,
		ADD,
		BACKGROUND,
		BUILDER,
		CHECK,
		LOCATED,
		CONNECTED,
		DISCONNECTED,
		LOCATE,
		UP,
		DOWN,
		LEFT,
		RIGHT,
		MENU,
		SETTINGS,
		ZOOM_IN,
		ZOOM_OUT,
		DEPTH,
		HOURGLASS,
		FAIL,
		ROCKET;

		public static String getResource( Images image ){
			return getResource( image, ImageSize.NORMAL );
		}
		
		public static String getResource( Images image, ImageSize size ){
			StringBuffer buffer = new StringBuffer();
			String prep;
			switch( image ){		
			case BACKGROUND:
				buffer.append( image.name().toLowerCase());
				buffer.append( S_PNG);
				break;
			case LOCATED:
				buffer.append( ImageSize.getLocation(S_LOCATED_IMAGE, size));
				break;
			case CONNECTED:
				buffer.append( ImageSize.getLocation(S_CONNECTED_IMAGE, size));
				break;
			case DISCONNECTED:
				buffer.append( ImageSize.getLocation(S_DISCONNECTED_IMAGE, size));
				break;
			case UP:
				prep = S_DOUBLE_ARROW + "_" + image.name();
				buffer.append( ImageSize.getLocation(prep, size));
				break;
			case LEFT:
				prep = S_DOUBLE_ARROW + "-" + image.name();
				buffer.append( ImageSize.getLocation(prep, size));
				break;
			case DOWN:
				prep = S_DOUBLE_ARROW + "-" + image.name();
				buffer.append( ImageSize.getLocation(prep, size));
				break;
			case RIGHT:
				prep = S_DOUBLE_ARROW + "-" + image.name();
				buffer.append( ImageSize.getLocation(prep, size));
				break;
			case DEPTH:
				buffer.append( S_BATHYMETRY );
				break;
			default:
				buffer.append( ImageSize.getLocation(image.name(), size));
				break;
			}
			return buffer.toString();
		}
		
		/**
		 * A filename by this method is going to be fetched out of the
		 * root of the resources directory.
		 * The extensions are now only used as subdir of the resources dir
		 * ...and of course as extension of the filename.
		 * @param image
		 * @return str the filename
		 */
		public static String getFileName( Images image ){
			String str = null;
			switch( image ){
			default:
				str = image.name().toLowerCase() + "-32";
				break;
			}
			str += ".png";
			return str;
		}

		/**
		 * The filenames by this method are going to be fetched out of a
		 * subdir of the resources directory.
		 * The extensions are now only used as subdir of the resources dir and ...
		 * ...as extension of the filename.
		 * @param image enumimages like Images.ADD, DELETE and so on
		 * @param imageSize the enumsize of the image like ImageSize.TINY
		 * @return str the subdir with filename
		 */
		public static String getFileName( Images image, ImageSize imageSize ){
			//get location, filename and size from super
			String str = ImageSize.getLocation( image.name(), imageSize );
			return str;
		}

	}
	
	private static ChuruataImages images = new ChuruataImages();
	
	private ChuruataImages() {
		super( S_RESOURCES, Activator.BUNDLE_ID);
	}

	public static ChuruataImages getInstance(){
		return images;
	}
	
	@Override
	public void initialise() {
		for( Images img: Images.values() ) {
			for( ImageSize imsi : ImageSize.values() ) {
				setImage( Images.getFileName( img, imsi ) );
			}
		}
	}

	/**
	 * Get the image
	 * @param descr the descriptor like Images.ADD, DELETE and so on
	 * @return image
	 */
	public static Image getImage( Images descr ){
		Image image = images.getImageFromName( Images.getFileName( descr ) );
		return image;
	}

	/**
	 * @param descr the descriptor like Images.ADD, DELETE and so on
	 * @param imageSize, a size like Abstractimages.ImageSizes.SMALL, NORMAL, LARGE, TILE
	 * @return the image in the wanted size
	 */
	public static Image getImage( Images descr, ImageSize imageSize ){
		Image image = images.getImageFromName( Images.getFileName( descr, imageSize ) );
		return image;
	}

	/**
	 * @param descr the descriptor of the image
	 * @param size a size like 16, 24, 32, 48, 64, 128, any other value is changed in 32
	 * @return the image in the wanted size
	 */
	public static Image getImage( Images descr, int size ){
		if( size==16 || size==24 || size==32 || size==48 || size==64 || size==128 ) {
			//the size is ok
		}
		else {
			size = 32;//standard size
		}
		ImageSize imageSize = AbstractImages.ImageSize.getImageSize( size );
		Image image = images.getImageFromName( Images.getFileName( descr, imageSize ) );
		return image;
	}
}
