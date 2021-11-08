package org.churuata.digital.core;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public interface IPresentation {

	public enum Attributes{
		DESCRIPTION,
		TITLE,
		TYPE,
		LINK;
	}
	
	public enum PresentationTypes implements Serializable{
		VIDEO,
		HAMMOCK;

		public static boolean isValid(String contributionStr) {
			for( PresentationTypes type: values() ) {
				if( type.name().equals(contributionStr))
					return true;
			}
			return false;
		}
	}
	
	long getId();

	/* (non-Javadoc)
	 * @see org.fgf.animal.count.location.model.IBatch#getDescription()
	 */
	String getDescription();

	String getTitle();

	PresentationTypes getType();

	String getLink();

	Date getCreateDate();

	Map<String, String> toAttributes();
}