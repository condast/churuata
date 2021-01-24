package org.churuata.rest.resources;

import com.google.gson.Gson;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.churuata.digital.core.location.IChuruataType;
import org.churuata.digital.core.location.IChuruataType.Contribution;
import org.churuata.digital.core.location.IChuruataType.Types;
import org.churuata.rest.core.Dispatcher;
import org.churuata.rest.model.Churuata;
import org.churuata.rest.model.ChuruataType;
import org.churuata.rest.service.ChuruataService;
import org.churuata.rest.service.ChuruataTypeService;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;

@Path("/")
public class ChuruataResource {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public ChuruataResource() {
	}

	/**
	 * First report of an illness. Add the history so that the system can inform the network.
	 * The system should return by giving the advice to contact a doctor
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/register")
	public Response create( @QueryParam("id") long userId, @QueryParam("token") String token, 
			@QueryParam("name") String name, @QueryParam("description") String description,
			@QueryParam("lat") long latitude, @QueryParam("lon") long longitude, @QueryParam("type") String type ) {
		logger.info( "Create " + name );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response result = null;
		try{
			if( !dispatcher.isRegistered( userId, token))
				return Response.status( Status.UNAUTHORIZED).build();
			if( StringUtils.isEmpty(name) || StringUtils.isEmpty(type ))
				return Response.status( Status.BAD_REQUEST).build();
			Gson gson = new Gson();
			
			ChuruataService service = new ChuruataService( dispatcher );
			
			String typeStr = StringStyler.styleToEnum(type);
			Churuata churuata = service.create(null, name, description, new LatLng( name, latitude, longitude), Types.valueOf(typeStr));
			String str = gson.toJson( churuata.getId(), long.class);
			result = Response.ok( str ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.ok("666").build();
			//return Response.serverError().build();
		}
		return result;
	}

	/**
	 * First report of an illness. Add the history so that the system can inform the network.
	 * The system should return by giving the advice to contact a doctor
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/find")
	public Response find( @QueryParam("userid") String userId, @QueryParam("token") String token, 
			@QueryParam("id") long id) {
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response result = null;
		try{
			if( !dispatcher.isRegistered(id, token))
				return Response.status( Status.UNAUTHORIZED).build();
			Gson gson = new Gson();
			ChuruataService service = new ChuruataService( dispatcher );
			Churuata churuata = service.find(id);
			String str = gson.toJson( churuata, Churuata.class);
			result = Response.ok( str ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.ok("666").build();
			//return Response.serverError().build();
		}
		return result;
	}

	/**
	 * First report of an illness. Add the history so that the system can inform the network.
	 * The system should return by giving the advice to contact a doctor
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/add-contribution")
	public Response addContribution( @QueryParam("userid") String userId, @QueryParam("token") String token, 
			@QueryParam("id") long id, @QueryParam("type") String type, @QueryParam("description") String description, 
			@QueryParam("contribution") String contribution) {
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response result = null;
		try{
			if( !dispatcher.isRegistered(id, token))
				return Response.status( Status.UNAUTHORIZED).build();
			ChuruataService service = new ChuruataService( dispatcher );
			Churuata churuata = service.find(id);
			ChuruataTypeService typeService = new ChuruataTypeService( dispatcher );
			String typeStr = StringStyler.styleToEnum(type);
			String contrStr = StringStyler.styleToEnum(contribution);
			ChuruataType ctype = typeService.create(null, Types.valueOf(typeStr), description, Contribution.valueOf( contrStr ));
			boolean success = churuata.addType(null, ctype);
			result = Response.ok( success ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			//return Response.serverError().build();
			return Response.ok("666").build();
		}
		return result;
	}

	/**
	 * First report of an illness. Add the history so that the system can inform the network.
	 * The system should return by giving the advice to contact a doctor
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/remove-contribution")
	public Response removeContribution( @QueryParam("userid") String userId, @QueryParam("token") String token, 
			@QueryParam("id") long id, @QueryParam("id") long typeId) {
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response result = null;
		try{
			if( !dispatcher.isRegistered(id, token))
				return Response.status( Status.UNAUTHORIZED).build();
			ChuruataService service = new ChuruataService( dispatcher );
			Churuata churuata = service.find(id);
			ChuruataTypeService typeService = new ChuruataTypeService( dispatcher );
			IChuruataType ctype = churuata.removeType(typeId);
			if( ctype == null )
				return Response.noContent().build();
			boolean success = typeService.remove(typeId);
			result = Response.ok( success ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			//return Response.serverError().build();
			return Response.ok(false).build();
		}
		return result;
	}
}