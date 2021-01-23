package org.churuata.digital.core.location;

import java.util.Collection;

import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;

public interface IChuruata {

	ILoginUser getOwner();

	String getName();

	void setName(String name);

	void setTypes(Collection<IChuruataType> types);

	String getDescription();

	void setDescription(String description);

	LatLng getLocation();

	boolean setType(IChuruataType type);

	boolean addType(ILoginUser user, IChuruataType.Types type);

	boolean removeType(IChuruataType type);

	IChuruataType[] getTypes();

}