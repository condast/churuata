package org.churuata.digital.ui.image;

import org.condast.commons.strings.StringStyler;
import org.eclipse.swt.graphics.Image;

public interface IInformationImages {

	public static final String S_ICON_PATH = "/icons/";

	public static final String S_ERROR_ICON = "error_button_16.png";
	public static final String S_INFORMATION_ICON = "information-icon.png";
	public static final String S_GREENBALL_ICON = "greenball.jpg";
	public static final String S_REDBALL_ICON = "redball.jpg";
	public static final String S_ORANGEBALL_ICON = "orangeball.jpg";
	public static final String S_BLANK_ICON = "blank.png";
	public static final String S_EDIT_ICON = "edit-notes-16.png";
	public static final String S_CHECK_ICON = "check-32.png";
	public static final String S_UNCHECK_ICON = "unchecked-32.png";
	public static final String S_DELETE_ICON = "delete-32.png";
	public static final String S_DELETE_16_ICON = "delete-16.png";
	public static final String S_ADD_ICON = "add-32.png";
	public static final String S_NEXT_ICON = "next-32.png";
	public static final String S_PREVIOUS_ICON = "previous-32.png";

	public static final String S_DIS_INFORMATION_ICON = "information_dis.png";
	public static final String S_DISABLED = "-disable-";

	public enum Information{
		ADD,
		CHECK,
		UNCHECKED,
		DELETE,
		ERROR,
		INFORMATION,
		BLANK,
		ENABLED,
		WARNING,
		DISABLED,
		EXPANDED,
		COLLAPSED,
		EDIT,
		NEXT,
		PREVIOUS;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public static String convert( Information info ){
			String str = null;
			switch( info ){
			case ADD:
				str = S_ADD_ICON;
				break;
			case BLANK:
				str = S_BLANK_ICON;
				break;
			case CHECK:
				str = S_CHECK_ICON;
				break;
			case COLLAPSED:
				str = S_REDBALL_ICON;
				break;
			case DISABLED:
				str = S_REDBALL_ICON;
				break;
			case ERROR:
				str = S_ERROR_ICON;
				break;
			case ENABLED:
				str = S_GREENBALL_ICON;
				break;
			case EXPANDED:
				str = S_GREENBALL_ICON;
				break;
			case EDIT:
				str = S_EDIT_ICON;
				break;
			case INFORMATION:
				str = S_INFORMATION_ICON;
				break;
			case NEXT:
				str = S_NEXT_ICON;
				break;
			case PREVIOUS:
				str = S_PREVIOUS_ICON;
				break;
			case UNCHECKED:
				str = S_UNCHECK_ICON;
				break;
			case WARNING:
				str = S_ORANGEBALL_ICON;
				break;
			case DELETE:
				str = S_DELETE_16_ICON;
				break;
			}
			return str;
		}

		public static String convert_disable( Information info ){
			String str = S_ICON_PATH;
			switch( info ){
			case ADD:
				str += S_ADD_ICON;
				break;
			case BLANK:
				str += S_BLANK_ICON;
				break;
			case ERROR:
				str += S_ERROR_ICON;
				break;
			case ENABLED:
				str += S_GREENBALL_ICON;
				break;
			case INFORMATION:
				str += S_DIS_INFORMATION_ICON;
				break;
			case WARNING:
				str += S_ORANGEBALL_ICON;
				break;
			case DISABLED:
				str += S_REDBALL_ICON;
				break;
			case COLLAPSED:
				str += S_REDBALL_ICON;
				break;
			case EXPANDED:
				str += S_GREENBALL_ICON;
				break;
			case EDIT:
				str += S_EDIT_ICON;
				break;
			case CHECK:
				str += S_CHECK_ICON;
				break;
			case NEXT:
				str += S_NEXT_ICON;
				break;
			case PREVIOUS:
				str += S_REDBALL_ICON;
				break;
			case UNCHECKED:
				str += S_UNCHECK_ICON;
				break;
			default:
				str = convert( info );
				str = str.replace("-" , S_DISABLED);
				break;
			}
			return str;
		}

	}
	
	/**
	 * Get the describable image
	 * @param describable
	 * @return
	 */
	public Image getImage( Information information, boolean enable );
}
