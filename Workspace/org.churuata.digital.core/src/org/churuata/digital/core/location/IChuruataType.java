package org.churuata.digital.core.location;

import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.images.IDefaultMarkers.Markers;

public interface IChuruataType {

	public enum Types{
		FOOD,
		SHELTER,
		MEDICINE,
		COMMUNITY,
		FAMILY,
		EDUCATION;

		@Override
		public String toString() {
			return StringStyler.prettyString( name());
		}
		
		public static String[] getItems() {
			String[] items = new String[ values().length ];
			for( int i=0; i< values().length; i++ )
				items[i] = values()[i].toString();
			return items;
		}
		
		public static Markers getMarker( Types type ) {
			Markers marker = Markers.BROWN;
			switch( type ) {
			case FOOD:
				marker = Markers.DARKGREEN;
				break;
			case SHELTER:
				marker = Markers.YELLOW;
				break;
			case MEDICINE:
				marker = Markers.RED;
				break;
			case COMMUNITY:
				marker = Markers.PINK;
				break;
			case FAMILY:
				marker = Markers.ORANGE;
				break;
			case EDUCATION:
				marker = Markers.PALEBLUE;
				break;
			default:
				break;
			}
			return marker;
			
		}
	}

	String getDescription();

	void setDescription(String description);

	Types getType();

	ILoginUser getUser();

}