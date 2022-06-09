package org.churuata.digital.core.model;

import org.condast.commons.strings.StringStyler;

public interface IChuruataService {

	public enum ServiceTypes{
		UNKNOWN,
		FOOD,
		SHELTER,
		LEGAL,
		MEDICINE,
		PSYCHOLOGICAL,
		SCHOOL;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		/**
		 * Get the displayable texts for this enumeration
		 * @return
		 */
		public static String[] getValues(){
			String[] retval = new String[ values().length ];
			for( int i=0; i<values().length; i++ ){
				retval[i] = values()[i].name();
			}
			return retval;
		}

		/**
		 * Get the displayable texts for this enumeration
		 * @return
		 */
		public static String[] getPrettyText(){
			String[] retval = new String[ values().length ];
			for( int i=0; i<values().length; i++ ){
				retval[i] = values()[i].toString();
			}
			return retval;
		}
	}

	public long getServiceId();
	
	public ServiceTypes getServiceType();

	void setServiceType(ServiceTypes contactType);

	public String getValue();
	
	void setValue(String value);

	String getDescription();

	void setDescription(String description);

}
