package org.churuata.digital.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.strings.StringStyler;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
public class ProfileData extends PersonData implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	public static final String S_PROFILE_REST_PATH = "profile/";
	
	public enum Requests{
		CREATE,
		FIND,
		ADD_SERVICE,
		REMOVE_SERVICE;
	}

	public enum Parameters{
		USER_ID,
		SECURITY,
		NAME,
		DESCRIPTION,
		LATITUDE,
		LONGITUDE,
		TYPE;

		@Override
		public String toString() {
			return StringStyler.xmlStyleString( name());
		}
	}
	
	private Collection<OrganisationData> types;
	
	public ProfileData( IContactPerson person ){
		super( person );
		types = new ArrayList<>();
	}

	
	public void addChuruataType( OrganisationData type ) {
		this.types.add(type);
	}

	public OrganisationData[] getTypes() {
		return types.toArray( new OrganisationData[ this.types.size()]);
	}
	
	
	
	
}