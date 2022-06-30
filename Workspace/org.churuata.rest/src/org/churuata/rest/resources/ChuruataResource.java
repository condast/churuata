package org.churuata.rest.resources;

import com.google.gson.Gson;

import java.util.Collection;
import java.util.Random;
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

import org.churuata.digital.core.location.ChuruataData;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruataService;
import org.churuata.digital.core.location.IChuruataService.Contribution;
import org.churuata.digital.core.location.IChuruataService.Services;
import org.churuata.rest.core.AuthenticationDispatcher;
import org.churuata.rest.core.Dispatcher;
import org.churuata.rest.model.Churuata;
import org.churuata.rest.model.ChuruataType;
import org.churuata.rest.service.ChuruataService;
import org.churuata.rest.service.ChuruataTypeService;
import org.condast.commons.Utils;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.plane.Field;
import org.condast.commons.data.plane.FieldData;
import org.condast.commons.persistence.service.TransactionManager;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;

@Path("/support")
public class ChuruataResource {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public ChuruataResource() {
		super();
	}

	/**
	 * Register a churuata
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
	public Response create( @QueryParam("id") long userId, @QueryParam("token") long token, 
			@QueryParam("name") String name, @QueryParam("description") String description,
			@QueryParam("lat") double latitude, @QueryParam("lon") double longitude, @QueryParam("type") String type ) {
		logger.info( "Create " + name );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response result = null;
		try{
			AuthenticationDispatcher ad = AuthenticationDispatcher.getInstance();
			ILoginUser user = ad.getLoginUser(userId, token);
			if(user == null)
				return Response.status( Status.UNAUTHORIZED).build();
			if( StringUtils.isEmpty(name) || StringUtils.isEmpty(type ))
				return Response.status( Status.BAD_REQUEST).build();
			Gson gson = new Gson();

			TransactionManager t = new TransactionManager( dispatcher );
			ChuruataService service = new ChuruataService( dispatcher );
			Churuata churuata = null;
			try {
				t.open();
				String typeStr = StringStyler.styleToEnum(type);
				LatLng latlng = new LatLng( name, latitude, longitude);
				churuata = service.create(user, name, description, latlng, Services.valueOf(typeStr));
			}
			finally {
				t.close();
			}
			String str = gson.toJson( churuata.getId(), long.class);
			result = Response.ok( str ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return result;
	}

	/**
	 * Register a churuata
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/show")
	public Response showAll( @QueryParam("latitude") double latitude, @QueryParam("longitude") double longitude ) {
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response result = null;
		try{
			if( !dispatcher.isConnected())
				return Response.status( Status.NOT_FOUND).build();
			TransactionManager t = new TransactionManager( dispatcher );
			ChuruataService service = new ChuruataService( dispatcher );
			dispatcher.clear();
			Collection<ChuruataData> results = dispatcher.getResults();
			try {
				t.open();
				FieldData fieldData = new FieldData( -1, new LatLng( "home", latitude, longitude), 10000l, 10000l, 0d, 11);
				dispatcher.setFieldData(fieldData);
				Collection<Churuata> churuatas = service.findAll();
				churuatas.forEach(c-> results.add(new ChuruataData( c )));
				int xmax = 10000; int ymax = 10000;
				int amount = 25;
				Random random = new Random();
				Field field = new Field( fieldData );
				for( int i=0; i<amount; i++ ) {	
					int x = random.nextInt(xmax);
					int y = random.nextInt(ymax);
					char newChar = (char)('A' + i);
					String id = String.valueOf( newChar );
					LatLng location = LatLngUtils.transform( field.getCentre(), x, y);
					location.setId( id);
					ChuruataData cd = new ChuruataData( location ); 
					//cd.setName("Organisation " + i);
					results.add( cd);
				}
			}
			finally {
				t.close();
			}
			Gson gson = new Gson();
			String str = gson.toJson( results.toArray( new IChuruata[ results.size()]), Churuata[].class);
			result = Response.ok( str ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return result;
	}

	/**
	 * Create the dimensions of the churuata
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/plan")
	public Response plan( @QueryParam("id") long userId, @QueryParam("token") long token, 
			@QueryParam("churuata-id") long churuataId, @QueryParam("max-logs") int logs,
			@QueryParam("max-leaves") int leaves ) {
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response result = null;
		TransactionManager t = new TransactionManager( dispatcher );
		ChuruataService service = new ChuruataService( dispatcher );
		try{
			AuthenticationDispatcher ad = AuthenticationDispatcher.getInstance();
			ILoginUser user = ad.getLoginUser(userId, token);
			if(user == null)
				return Response.status( Status.UNAUTHORIZED).build();

			if(( logs < 0 ) || ( leaves < 0 ))
				return Response.status( Status.BAD_REQUEST).build();
	
			t.open();
			Churuata churuata = service.find(churuataId);		
			if( churuata == null )
				return Response.noContent().build();
			
			logger.info( "Create " + churuata.getName() );
			//if( logs > 0 )
			//	churuata.setMaxLogs(logs);
			//if( leaves > 0 )
			//	churuata.setMaxLeaves(leaves);
			
			result = Response.ok().build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
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
	public Response find( @QueryParam("churuata-id") long churuataId, @QueryParam("latitude") double latitude, 
			@QueryParam("longitude") double longitude) {
		Dispatcher dispatcher = Dispatcher.getInstance();
		Collection<ChuruataData> results = dispatcher.getResults();
		Response result = Response.noContent().build();
		try{
			//AuthenticationDispatcher ad = AuthenticationDispatcher.getInstance();
			//if( !ad.isRegistered( userId, token))
			//	return Response.status( Status.UNAUTHORIZED).build();
			Gson gson = new Gson();
			if( Utils.assertNull(results))
				return Response.noContent().build();
			LatLng location = new LatLng( latitude, longitude );
			for( IChuruata churuata: results ) {
				if( LatLngUtils.isInRange( churuata.getLocation(), location, 1000)) {
					for(int i=0; i<5; i++ ) {
						int size = IChuruataService.Services.values().length;
						IChuruataService.Services type = IChuruataService.Services.values()[ i%size];
						String description = "We help";
						switch( type) {
						case COMMUNITY:
							description = "Various community services";
							break;
						case EDUCATION:
							description = "Volunteers are teaching children for free";
							break;
						case FAMILY:
							description = "Family services by professional people";
							break;
						case FOOD:
							description = "Fruit, vegetables and water";
							break;
						case MEDICINE:
							description = "Various donations from pharmacies";
							break;
						case SHELTER:
							description = "Temporary homes provided by the minucipality";
							break;
						case LEGAL:
							description = "Online consultation by specialists";
							break;
						default:
							break;
						}
						IChuruataService ct = churuata.addType("contributor: " + i, type);
						ct.setDescription(description);
					}
					String str = gson.toJson( churuata, ChuruataData.class);
					result = Response.ok( str ).build();
					return result;			
				}
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
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
	@Path("/add")
	public Response addChuruata( @QueryParam("userid") long userId, @QueryParam("token") long token, 
			@QueryParam("id") long id, @QueryParam("type") String type, @QueryParam("description") String description, 
			@QueryParam("contribution") String contribution) {
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response result = null;
		try{
			AuthenticationDispatcher ad = AuthenticationDispatcher.getInstance();
			if( !ad.isRegistered( userId, token))
				return Response.status( Status.UNAUTHORIZED).build();
			ChuruataService service = new ChuruataService( dispatcher );
			Churuata churuata = service.find(id);
			ChuruataTypeService typeService = new ChuruataTypeService( dispatcher );
			String typeStr = StringStyler.styleToEnum(type);
			String contrStr = StringStyler.styleToEnum(contribution);
			ChuruataType ctype = typeService.create(null, Services.valueOf(typeStr), description, Contribution.valueOf( contrStr ));
			boolean success = churuata.addType( ctype);
			result = Response.ok( success ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
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
	@Path("/remove")
	public Response removeChuruata( @QueryParam("userid") long userId, @QueryParam("token") long token, 
			@QueryParam("id") long id, @QueryParam("id") long typeId) {
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response result = null;
		try{
			AuthenticationDispatcher ad = AuthenticationDispatcher.getInstance();
			if( !ad.isRegistered( userId, token))
				return Response.status( Status.UNAUTHORIZED).build();
			ChuruataService service = new ChuruataService( dispatcher );
			Churuata churuata = service.find(id);
			ChuruataTypeService typeService = new ChuruataTypeService( dispatcher );
			IChuruataService ctype = churuata.removeType(typeId);
			if( ctype == null )
				return Response.noContent().build();
			boolean success = typeService.remove(typeId);
			result = Response.ok( success ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return result;
	}
}