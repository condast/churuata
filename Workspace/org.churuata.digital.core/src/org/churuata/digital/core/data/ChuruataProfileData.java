package org.churuata.digital.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.na.data.AddressData;
import org.condast.commons.na.data.OrganisationData;
import org.condast.commons.na.data.PersonData;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.na.profile.IProfileData;
import org.condast.commons.strings.StringStyler;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
public class ChuruataProfileData extends PersonData implements IProfileData, Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	public enum Requests{
		CREATE,
		GET,
		GET_PROFILE,
		FIND,
		REGISTER,
		ADD_CONTACT_TYPE,
		UPDATE_PERSON;
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

	private LoginData user;
	
	private AddressData address;
	
	private Collection<ChuruataOrganisationData> organisations;

	public ChuruataProfileData( IContactPerson person ){
		super( person );
		this.organisations = new ArrayList<>();
	}

	public ChuruataProfileData( ILoginUser user, IContactPerson person ){
		super( person );
		this.user = new LoginData( user );
		this.organisations = new ArrayList<>();
	}

	public ChuruataProfileData( LoginData user, IContactPerson person ){
		super( person );
		this.user = user;
		this.organisations = new ArrayList<>();
	}

	public LoginData getLoginUser() {
		return user;
	}

	public AddressData getAddress() {
		return address;
	}

	public void setAddress(AddressData address) {
		this.address = address;
	}

	public void addOrganisation( OrganisationData organisation ) {
		this.organisations.add((ChuruataOrganisationData) organisation);
	}

	public void removeOrganisation( OrganisationData organisation ) {
		this.organisations.remove(organisation);
	}

	public ChuruataOrganisationData[] getOrganisation() {
		return organisations.toArray( new ChuruataOrganisationData[ this.organisations.size()]);
	}
}