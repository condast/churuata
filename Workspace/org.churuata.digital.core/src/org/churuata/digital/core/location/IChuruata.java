package org.churuata.digital.core.location;

import java.util.Collection;

import org.churuata.digital.core.IPresentation;
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

	boolean addType( String contributor, IChuruataType.Types type);


	boolean addType(String contributor, Types type, Contribution contribution);

	boolean removeType(IChuruataType type);	

	IChuruataType removeType(long typeId);

	IChuruataType removeType(String contributor, Types type);

	IChuruataType[] getTypes();

	boolean addMurmering( IMurmering murmering);

	boolean removeMurmering( IMurmering murmering);

	boolean removeMurmering(String filter);
	
	IMurmering[] getMurmerings();

	int getLogs();

	int getMaxLogs();

	void setMaxLogs(int maxlogs);

	int getLeaves();

	int getMaxLeaves();

	void setMaxLeaves(int maxLeaves);

	String getHomepage();

	void setHomepage(String url);

	boolean addPresentation( IPresentation presentation);

	boolean removePresentation( String title);

	IPresentation[] getVideos();

	IPresentation[] getHammocks();

	int getNrOfVideos();

	int getNrOfHammocks();
}