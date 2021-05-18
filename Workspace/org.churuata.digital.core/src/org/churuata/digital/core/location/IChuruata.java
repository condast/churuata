package org.churuata.digital.core.location;

import java.util.Collection;

import org.churuata.digital.core.location.IChuruataType.Contribution;
import org.churuata.digital.core.location.IChuruataType.Types;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;

public interface IChuruata {

	public enum Requests{
		REGISTER,
		FIND,
		ADD,
		REMOVE,
		UPDATE;
	}
	
	long getId();

	ILoginUser getOwner();

	String getName();

	void setName(String name);

	void setTypes(Collection<IChuruataType> types);

	String getDescription();

	void setDescription(String description);

	LatLng getLocation();

	boolean setType(IChuruataType type);

	boolean addType(ILoginUser user, IChuruataType.Types type);


	boolean addType(ILoginUser user, Types type, Contribution contribution);

	boolean removeType(IChuruataType type);	

	IChuruataType removeType(long typeId);

	IChuruataType[] getTypes();

	boolean addMurmering(ILoginUser user, String text);

	boolean removeMurmering( IMurmering murmering);

	int getLogs();

	void setLogs(int logs);

	int getMaxlogs();

	void setMaxlogs(int maxlogs);

	int getLeaves();

	void setLeaves(int leaves);

	int getMaxLeaves();

	void setMaxLeaves(int maxLeaves);

	int getHammocks();

	void setHammocks(int hammocks);

	String getHomepage();

	void setHomepage(String url);
}