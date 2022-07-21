package org.churuata.digital.core.data;

import java.io.Serializable;
import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.na.data.ProfileData;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.strings.StringStyler;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
public class ChuruataProfileData extends ProfileData implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	public static final String S_PROFILE_REST_PATH = "profile/";
	
	public enum Requests{
		CREATE,
		GET,
		GET_PROFILE,
		FIND,
		REGISTER,
		ADD_CONTACT_TYPE,
		UPDATE_PERSON,
		ADD_SERVICE,
		REMOVE_SERVICE;
	}

	public enum Parameters{
		PERSON_ID,
		DESCRIPTION,
		EMAIL,
		LATITUDE,
		LONGITUDE,
		NAME,
		FIRST_NAME,
		SURNAME,
		PREFIX,
		SECURITY,
		TITLE,
		TYPE,
		USER_ID;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString( name());
		}
	}
	public ChuruataProfileData( LoginData user, IContactPerson person ){
		super( user, person );
	}
}