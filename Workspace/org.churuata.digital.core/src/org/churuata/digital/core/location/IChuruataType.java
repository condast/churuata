package org.churuata.digital.core.location;

import java.util.Date;

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

		public static boolean isValid(String typeStr) {
			for( Types type: values() ) {
				if( type.name().equals(typeStr))
					return true;
			}
			return false;
		}
	}

	public enum Contribution{
		LOG,
		LEAF;
		
		@Override
		public String toString() {
			return StringStyler.prettyString( name());
		}

		public static boolean isValid(String contributionStr) {
			for( Contribution type: values() ) {
				if( type.name().equals(contributionStr))
					return true;
			}
			return false;
		}
	}

	public static final String S_ANONYMOUS = "anonymous";
	
	long getId();

	String getDescription();

	void setDescription(String description);

	Types getType();

	String getContributor();
	
	Contribution getContribution();
	
	public Date from();
	
	public void setFrom( Date date );
	
	public Date to();
	
	public void setTo( Date date );

}