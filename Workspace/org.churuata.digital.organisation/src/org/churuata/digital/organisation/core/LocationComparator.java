package org.churuata.digital.organisation.core;

import org.churuata.digital.core.model.IOrganisation;
import org.condast.commons.comparator.AbstractReferenceComparator;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;

public class LocationComparator<C extends IOrganisation> extends AbstractReferenceComparator<LatLng, C>{

	public LocationComparator( double latitude, double longitude) {
		this( new LatLng( latitude, longitude));
	}

	public LocationComparator(LatLng location) {
		super(location);
	}

	@Override
	public int compare(IOrganisation o1, IOrganisation o2) {
		double distance1 = (o1 == null ) || ( o1.getLocation()==null)? 0: LatLngUtils.distance(super.getReference(), o1.getLocation());
		double distance2 = (o2 == null ) || ( o2.getLocation()==null)? 0: LatLngUtils.distance(super.getReference(), o2.getLocation());
		return ( distance1 < ( distance2 - Double.MIN_VALUE))?1:( distance1 > ( distance2 + Double.MIN_VALUE))?1:0;
	}
}
