package org.churuata.digital.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.na.data.AddressData;
import org.condast.commons.na.data.ContactPersonData;
import org.condast.commons.na.data.OrganisationData;
import org.condast.commons.na.model.IContactPerson;
import org.condast.commons.na.profile.IProfileData;
import org.condast.commons.strings.StringStyler;

/**
 * The persistent class for the eet_tb_persoon database table.
 */
public class ProfileData extends ContactPersonData implements Serializable, Cloneable, IProfileData {
	private static final long serialVersionUID = 1L;
	
	public enum Requests{
		CREATE,
		GET,
		GET_PROFILE,
		FIND,
		REGISTER,
		ADD_CONTACT_TYPE,
		UPDATE_PERSON, 
		REMOVE_CONTACTS;
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
	
	//Principle address
	private AddressData address;
	
	private Collection<ChuruataOrganisationData> organisations;

	public ProfileData( IContactPerson person ){
		super( person );
		this.organisations = new ArrayList<>();
	}

	public ProfileData( ILoginUser user, IContactPerson person ){
		super( person );
		this.user = ( user == null )?null: new LoginData( user );
		this.organisations = new ArrayList<>();
	}

	public ProfileData( LoginData user, IContactPerson person ){
		super( person );
		this.user = user;
		this.organisations = new ArrayList<>();
	}

	@Override
	public LoginData getLoginUser() {
		return user;
	}

	@Override
	public AddressData getAddress() {
		return address;
	}

	@Override
	public void setAddress(AddressData address) {
		this.address = address;
	}

	@Override
	public void addOrganisation( OrganisationData organisation ) {
		this.organisations.add((ChuruataOrganisationData) organisation);
	}

	@Override
	public void removeOrganisation( OrganisationData organisation ) {
		this.organisations.remove(organisation);
	}

	@Override
	public OrganisationData[] getOrganisation() {
		return organisations.toArray( new OrganisationData[ this.organisations.size()]);
	}
	
	
}