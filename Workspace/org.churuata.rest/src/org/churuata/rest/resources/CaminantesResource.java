package org.churuata.rest.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.churuata.digital.core.IPresentation;
import org.churuata.digital.core.location.IChuruata;
import org.churuata.digital.core.location.IChuruataType;
import org.churuata.digital.core.location.IMurmering;
import org.churuata.rest.core.Dispatcher;
import org.churuata.rest.model.Churuata;
import org.churuata.rest.service.ChuruataService;
import org.churuata.rest.service.MurmeringService;
import org.churuata.rest.service.PresentationService;
import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;

@Path("/walkers")
public class CaminantesResource {

	public enum Attributes{
		LOGS,
		MAX_LOGS,
		LEAVES,
		MAX_LEAVES,
		VIDEOS,
		HAMMOCKS;
	}
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public CaminantesResource() {
	}

	/**
	 * Select the nearest churuata
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/select")
	public Response select( @QueryParam("name") String name, @QueryParam("lat") double latitude, @QueryParam("lon") double longitude,
			@QueryParam("range") int range) {
		logger.info( "Create " + name );
		Dispatcher dispatcher = Dispatcher.getInstance();
		try{
			if( StringUtils.isEmpty(name) || ( latitude < 0 ) || ( longitude < 0) || ( range < 0))
				return Response.status( Status.BAD_REQUEST).build();
			ChuruataService service = new ChuruataService( dispatcher );
			service.open();
			try {
				LatLng latlng = new LatLng( name, latitude, longitude);
				Collection<Churuata> churuatas = service.findChuruata(latlng, range);
				if( Utils.assertNull(churuatas))
					return Response.noContent().build();
				IChuruata churuata = churuatas.iterator().next();
				return Response.ok( churuata.getId()).build();
			}
			finally {
				service.close();
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	/**
	 * Contribute
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/contribute")
	public Response contribute( @QueryParam("name") String name, @QueryParam("token") long token, 
			@QueryParam("churuata-id") long churuataId, @QueryParam("type") String type, 
			@QueryParam("description") String description, @QueryParam("contribution") String contribution ) {
		logger.info( "Create " + name );
		Dispatcher dispatcher = Dispatcher.getInstance();
		try{
			if( StringUtils.isEmpty(name) || ( token < 0 ) || StringUtils.isEmpty(type ))
				return Response.status( Status.BAD_REQUEST).build();
			String typeStr = StringStyler.styleToEnum(type);
			if( !IChuruataType.Types.isValid( typeStr))
				return Response.status( Status.BAD_REQUEST).build();				
			IChuruataType.Types ct = IChuruataType.Types.valueOf(typeStr);

			String contrStr = StringStyler.styleToEnum( contribution);
			if( !IChuruataType.Contribution.isValid( contrStr))
				return Response.status( Status.BAD_REQUEST).build();
			IChuruataType.Contribution contr = IChuruataType.Contribution.valueOf(contrStr);

			ChuruataService service = new ChuruataService( dispatcher );
			service.open();
			Churuata churuata = null;
			try {
				churuata = service.find(churuataId);
				if( churuata == null )
					return Response.noContent().build();			
				churuata.addType(name, ct, contr);
				return Response.ok().build();
			}
			finally {
				service.close();
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	/**
	 * Remove a contribution
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/remove-contribution")
	public Response removeContribution( @QueryParam("name") String name, @QueryParam("token") long token, 
			@QueryParam("churuata-id") long churuataId, @QueryParam("type") String type ) {
		logger.info( "Create " + name );
		Dispatcher dispatcher = Dispatcher.getInstance();
		try{
			if( StringUtils.isEmpty(name) || ( token < 0 ) || StringUtils.isEmpty(type ))
				return Response.status( Status.BAD_REQUEST).build();
			String typeStr = StringStyler.styleToEnum(type);
			if( !IChuruataType.Types.isValid( typeStr))
				return Response.status( Status.BAD_REQUEST).build();				
			IChuruataType.Types ct = IChuruataType.Types.valueOf(typeStr);

			ChuruataService service = new ChuruataService( dispatcher );
			service.open();
			Churuata churuata = null;
			try {
				churuata = service.find(churuataId);
				if( churuata == null )
					return Response.noContent().build();			
				churuata.removeType(name, ct);
				return Response.ok().build();
			}
			finally {
				service.close();
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	/**
	 * Add a presentation
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/add-presentation")
	public Response addPresentation( @QueryParam("name") String name, @QueryParam("token") long token, 
			@QueryParam("churuata-id") long churuataId, @QueryParam("type") String type, 
			@QueryParam("title") String title, @QueryParam("description") String description, 
			@QueryParam("link") String link ) {
		logger.info( "Create " + name );
		Dispatcher dispatcher = Dispatcher.getInstance();
		try{
			if( StringUtils.isEmpty(name) || ( token < 0 ) || StringUtils.isEmpty(type ) || 
					StringUtils.isEmpty( title ) || StringUtils.isEmpty( link ))
				return Response.status( Status.BAD_REQUEST).build();
			String typeStr = StringStyler.styleToEnum(type);
			if( !IPresentation.PresentationTypes.isValid( typeStr))
				return Response.status( Status.BAD_REQUEST).build();				

			ChuruataService service = new ChuruataService( dispatcher );
			service.open();
			Churuata churuata = null;
			try {
				churuata = service.find(churuataId);
				if( churuata == null )
					return Response.noContent().build();			
				IPresentation.PresentationTypes pt = IPresentation.PresentationTypes.valueOf(typeStr);
	
				PresentationService ps = new PresentationService( dispatcher);
				IPresentation presentation = ps.create( churuata, pt, title, link, description );
				churuata.addPresentation(presentation);
				return Response.ok().build();
			}
			finally {
				service.close();
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	/**
	 * Add a presentation
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/remove-presentation")
	public Response removePresentation( @QueryParam("name") String name, @QueryParam("token") long token, 
			@QueryParam("churuata-id") long churuataId, @QueryParam("title") String title ) {
		logger.info( "Create " + name );
		Dispatcher dispatcher = Dispatcher.getInstance();
		try{
			if( StringUtils.isEmpty(name) || ( token < 0 ) || StringUtils.isEmpty( title ))
				return Response.status( Status.BAD_REQUEST).build();

			ChuruataService service = new ChuruataService( dispatcher );
			service.open();
			Churuata churuata = null;
			try {
				churuata = service.find(churuataId);
				if( churuata == null )
					return Response.noContent().build();			
				
				churuata.removePresentation(title);
				return Response.ok().build();
			}
			finally {
				service.close();
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	/**
	 * Get the videos
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get-videos")
	public Response getVideos( @QueryParam("name") String name, @QueryParam("token") long token, 
			@QueryParam("churuata-id") long churuataId) {
		logger.info( "Create " + name );
		Dispatcher dispatcher = Dispatcher.getInstance();
		try{
			if( StringUtils.isEmpty(name) || ( token < 0 ))
				return Response.status( Status.BAD_REQUEST).build();

			ChuruataService service = new ChuruataService( dispatcher );
			service.open();
			Churuata churuata = null;
			try {
				churuata = service.find(churuataId);
				if( churuata == null )
					return Response.noContent().build();			
				
				GsonBuilder builder = new GsonBuilder();
				builder.enableComplexMapKeySerialization();
				Gson gson = builder.create();
				Collection<Map<String,String>> results = new ArrayList<>();
				for( IPresentation presentation: churuata.getVideos()) {
					results.add(presentation.toAttributes());
				}
				String str = gson.toJson(results, Collection.class);
				return Response.ok( str ).build();
			}
			finally {
				service.close();
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	/**
	 * Get the hammocks
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get-hammocks")
	public Response getHammocks( @QueryParam("name") String name, @QueryParam("token") long token, 
			@QueryParam("churuata-id") long churuataId) {
		logger.info( "Create " + name );
		Dispatcher dispatcher = Dispatcher.getInstance();
		try{
			if( StringUtils.isEmpty(name) || ( token < 0 ))
				return Response.status( Status.BAD_REQUEST).build();

			ChuruataService service = new ChuruataService( dispatcher );
			service.open();
			Churuata churuata = null;
			try {
				churuata = service.find(churuataId);
				if( churuata == null )
					return Response.noContent().build();			
				
				Gson gson = new Gson();
				Collection<Map<String,String>> results = new ArrayList<>();
				for( IPresentation presentation: churuata.getHammocks()) {
					results.add(presentation.toAttributes());
				}
				String str = gson.toJson(results, Collection.class);
				return Response.ok( str ).build();
			}
			finally {
				service.close();
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	/**
	 * Add a presentation
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/add-murmering")
	public Response addMurmering( @QueryParam("name") String name, @QueryParam("token") long token, 
			@QueryParam("churuata-id") long churuataId, @QueryParam("text") String text ) {
		logger.info( "Create " + name );
		Dispatcher dispatcher = Dispatcher.getInstance();
		try{
			if( StringUtils.isEmpty(name) || ( token < 0 ) || StringUtils.isEmpty(text ))
				return Response.status( Status.BAD_REQUEST).build();

			ChuruataService service = new ChuruataService( dispatcher );
			service.open();
			Churuata churuata = null;
			try {
				churuata = service.find(churuataId);
				if( churuata == null )
					return Response.noContent().build();			
				
				MurmeringService ms = new MurmeringService( dispatcher);
				IMurmering presentation = ms.create( churuata, name, text );
				churuata.addMurmering(presentation);
				return Response.ok().build();
			}
			finally {
				service.close();
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	/**
	 * Add a presentation
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/remove-murmering")
	public Response removeMurmering( @QueryParam("name") String name, @QueryParam("token") long token, 
			@QueryParam("churuata-id") long churuataId, @QueryParam("filter") String filter ) {
		logger.info( "Create " + name );
		Dispatcher dispatcher = Dispatcher.getInstance();
		try{
			if( StringUtils.isEmpty(name) || ( token < 0 ))
				return Response.status( Status.BAD_REQUEST).build();

			ChuruataService service = new ChuruataService( dispatcher );
			service.open();
			Churuata churuata = null;
			try {
				churuata = service.find(churuataId);
				if( churuata == null )
					return Response.noContent().build();			
				
				churuata.removeMurmering( filter );
				return Response.ok().build();
			}
			finally {
				service.close();
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	/**
	 * Get the murmerings
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get-murmerings")
	public Response getMurmerings( @QueryParam("name") String name, @QueryParam("token") long token, 
			@QueryParam("churuata-id") long churuataId) {
		logger.info( "Create " + name );
		Dispatcher dispatcher = Dispatcher.getInstance();
		try{
			if( StringUtils.isEmpty(name) || ( token < 0 ))
				return Response.status( Status.BAD_REQUEST).build();

			ChuruataService service = new ChuruataService( dispatcher );
			service.open();
			Churuata churuata = null;
			try {
				churuata = service.find(churuataId);
				if( churuata == null )
					return Response.noContent().build();			
				
				Gson gson = new Gson();
				Collection<Map<String,String>> results = new ArrayList<>();
				for( IMurmering murmering: churuata.getMurmerings()) {
					results.add(murmering.toAttributes());
				}
				String str = gson.toJson(results, Collection.class);
				return Response.ok( str ).build();
			}
			finally {
				service.close();
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	/**
	 * Get the state of the churuata
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get-state")
	public Response getState( @QueryParam("name") String name, @QueryParam("token") long token, 
			@QueryParam("churuata-id") long churuataId ) {
		logger.info( "Create " + name );
		Dispatcher dispatcher = Dispatcher.getInstance();
		try{
			if( StringUtils.isEmpty(name) || ( token < 0 ))
				return Response.status( Status.BAD_REQUEST).build();

			ChuruataService service = new ChuruataService( dispatcher );
			service.open();
			Churuata churuata = null;
			try {
				churuata = service.find(churuataId);
				if( churuata == null )
					return Response.noContent().build();			
				Map<String, Integer> state = new HashMap<>();
				state.put(Attributes.LOGS.name(), churuata.getLogs());
				state.put(Attributes.MAX_LOGS.name(), churuata.getMaxLogs());
				state.put(Attributes.LEAVES.name(), churuata.getLeaves());
				state.put(Attributes.MAX_LEAVES.name(), churuata.getMaxLeaves());
				state.put(Attributes.VIDEOS.name(), churuata.getNrOfVideos());
				state.put(Attributes.HAMMOCKS.name(), churuata.getNrOfHammocks());
				
				Gson gson = new Gson();
				String str = gson.toJson(state, Map.class);
				return Response.ok( str ).build();
			}
			finally {
				service.close();
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}
}