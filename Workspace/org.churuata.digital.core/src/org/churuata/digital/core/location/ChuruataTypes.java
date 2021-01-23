package org.churuata.digital.core.location;

import org.condast.commons.authentication.user.ILoginUser;

public class ChuruataTypes implements Comparable<IChuruataType>, IChuruataType{

	private Types type;
	
	private String description;
	
	private ILoginUser user;
	
	public ChuruataTypes( Types type, ILoginUser user) {
		this( type, null, user );
	}

	public ChuruataTypes(Types type, String description, ILoginUser user) {
		super();
		this.type = type;
		this.description = description;
		this.user = user;
	}


	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Types getType() {
		return type;
	}

	@Override
	public ILoginUser getUser() {
		return user;
	}

	@Override
	public int compareTo(IChuruataType o) {
		return type.toString().compareTo(o.getType().toString());
	}
}
