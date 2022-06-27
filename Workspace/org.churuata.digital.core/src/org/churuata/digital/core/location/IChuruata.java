package org.churuata.digital.core.location;

import java.util.Collection;

import org.churuata.digital.core.IPresentation;
import org.churuata.digital.core.location.IChuruataService.Contribution;
import org.churuata.digital.core.location.IChuruataService.Services;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;

public interface IChuruata {

	public enum Requests{
		REGISTER,
		SHOW,
		FIND,
		ADD,
		REMOVE,
		UPDATE;
	}

	public static String S_CHURUATA = "Churuata";
	
	long getId();

	ILoginUser getOwner();

	String getName();

	void setName(String name);

	void setTypes(Collection<IChuruataService> types);

	String getDescription();

	void setDescription(String description);

	LatLng getLocation();

	boolean setType(IChuruataService type);

	IChuruataService addType( String contributor, IChuruataService.Services type);


	IChuruataService addType(String contributor, Services type, Contribution contribution);

	boolean removeType(IChuruataService type);	

	IChuruataService removeType(long typeId);

	IChuruataService removeType(String contributor, Services type);

	IChuruataService[] getTypes();

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