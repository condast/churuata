package org.churuata.digital.core.location;

import org.condast.commons.authentication.user.ILoginUser;

public class ChuruataType implements Comparable<IChuruataType>, IChuruataType{

	private Types type;
	
	private String description;
	
	private Contribution contribution;
	
	private String contributor;
	
	public ChuruataType( ILoginUser user, Types type) {
		this( user, type, Contribution.LOG );
	}

	public ChuruataType(ILoginUser user, Types type, String description) {
		this( user, type, description, Contribution.LOG);
	}

	public ChuruataType(ILoginUser user, Types type, Contribution contribution) {
		this( user, type, null, contribution);
	}
	
	public ChuruataType(ILoginUser user, Types type, String description, Contribution contribution) {
		super();
		this.type = type;
		this.description = description;
		this.contributor = user.getUserName();
		this.contribution = contribution;
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
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
	public Contribution getContribution() {
		return contribution;
	}

	@Override
	public int compareTo(IChuruataType o) {
		return type.toString().compareTo(o.getType().toString());
	}

	@Override
	public String getContributor() {
		return contributor;
	}
}
