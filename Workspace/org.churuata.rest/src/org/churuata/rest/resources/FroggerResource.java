package org.churuata.rest.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.churuata.rest.core.Dispatcher;
import org.condast.commons.Utils;
import org.condast.commons.strings.StringUtils;

@Path("/")
public class FroggerResource {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private Lock lock;

	public FroggerResource() {
		lock = new ReentrantLock();
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
	public Response create( @QueryParam("identifier") String identifier ) {
		logger.info( "Create " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response result = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			Gson gson = new Gson();
			String str = gson.toJson( null, CreateRegisterData.class);
			result = Response.ok( str ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return result;
	}

	/**
	 * Start the frogger environment
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/start")
	public Response start( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier,
			@QueryParam("width") int width, @QueryParam("density") int density,  @QueryParam("infected") int infected) {
		logger.info( "Start mobile " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response response = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			boolean result = false;//dispatcher;//.start(identifier, width, density, infected );
			response = Response.ok( result ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return response;
	}

	/**
	 * Pause the frogger environment
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/pause")
	public Response pause( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier ) {
		logger.info( "Get mobile " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response response = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			boolean result = false;//dispatcher.pause(identifier);
			response = Response.ok( result ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return response;
	}

	/**
	 * Start the frogger environment
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/stop")
	public Response stop( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier ) {
		logger.info( "Get mobile " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response response = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			boolean result = false;//dispatcher.stop(identifier);
			response = Response.ok( result ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return response;
	}

	/**
	 * Start the frogger environment
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/clear")
	public Response clear( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier ) {
		logger.info( "Clear environment " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response response = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			boolean result = false;//dispatcher.stop(identifier);
			response = Response.ok( result ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return response;
	}

	/**
	 * Start the frogger environment
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/set-infected")
	public Response setInfected( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier,
			@QueryParam("infected") int infected) {
		logger.fine( "Set infected environment " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response response = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			boolean result = false;//dispatcher.setInfected(identifier, infected);
			response = Response.ok( result ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return response;
	}

	/**
	 * Start the frogger environment
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/set-density")
	public Response setDensity( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier,
			@QueryParam("density") int density) {
		logger.fine( "Set population density " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response response = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			boolean result = false;//dispatcher.setDensity(identifier, density);
			response = Response.ok( result ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return response;
	}

	/**
	 * Set the protection for the frogger environment
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/set-protection")
	public Response setProtection( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier,
			@QueryParam("protection") boolean protection) {
		logger.fine( "Set protection environment " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response response = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			boolean result = false;//dispatcher.setProtection(identifier, protection);
			response = result? Response.ok( protection ).build(): Response.noContent().build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return response;
	}

	/**
	 * Get the protected locations
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/protected")
	public Response getProtected( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier) {
		logger.fine( "Get protected " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response response = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			GsonBuilder builder = new GsonBuilder();
			builder.enableComplexMapKeySerialization();
			Gson gson = builder.create();
			String str=  null;// gson.toJson(results, LocationData[].class);
			response = Response.ok(str).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			response = Response.serverError().build();
		}
		return response;
	}

	/**
	 * Get the protected locations
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/prediction")
	public Response getPrediction( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier,
			@QueryParam("range") int range) {
		logger.fine( "Get Prediction " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response response = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			Map<Integer, Double> results = null;//
			if( Utils.assertNull(results))
				return Response.noContent().build();
			GsonBuilder builder = new GsonBuilder();
			builder.enableComplexMapKeySerialization();
			Gson gson = builder.create();
			String str=  gson.toJson(results, Map.class);
			response = Response.ok(str).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			response = Response.serverError().build();
		}
		return response;
	}

	
	/**
	 * Get the protected locations
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/average")
	public Response getAverage( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier,
			@QueryParam("range") int range) {
		logger.fine( "Get Average " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response response = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			GsonBuilder builder = new GsonBuilder();
			builder.enableComplexMapKeySerialization();
			Gson gson = builder.create();
			String str= null;//gson.toJson(results, TimelineCollection.class);
			response = Response.ok(str).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			response = Response.serverError().build();
		}
		return response;
	}

	/**
	 * Set the health of the owner
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/getAdvice")
	public Response getAdvice( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier,
			@QueryParam("contagion") String contagionStr) {
		logger.info( "Get Advice " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response result = null;
		result = Response.ok(  ).build();
		return result;
	}

	/**
	 * dispose of the frogger environment
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/dispose")
	public Response dispose( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier ) {
		logger.info( "Clear environment " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response response = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			boolean result = false;
			response = Response.ok( result ).build();
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return Response.serverError().build();
		}
		return response;
	}

	/**
	 * Get the updated information about the hubs
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/update")
	public Response getUpdate( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier,
			@QueryParam("step") int step ) {
		logger.fine( "Update information " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response response = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			
			StreamingOutput stream = new StreamingOutput() {
	            @Override
	            public void write(OutputStream os) throws IOException, WebApplicationException {
	    			GsonBuilder builder = new GsonBuilder();
	    			final Gson gson = builder.enableComplexMapKeySerialization().create();
	                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
	                writer.write(gson.toJson( null));
	                writer.flush();
	            }
	        };
			response = Response.ok(stream).build();
		}
		catch( Exception ex ){
			logger.warning(ex.getMessage());
			return Response.serverError().build();
		}
		return response;
	}

	/**
	 * Get the updated information about the hubs
	 * @param id
	 * @param token
	 * @param identifier
	 * @param history
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/surroundings")
	public Response getSurroundings( @QueryParam("id") long id, @QueryParam("token") long token, @QueryParam("identifier") String identifier,
			@QueryParam("radius") int radius, @QueryParam("step") int step  ) {
		logger.fine( "Get surroundings " + identifier );
		Dispatcher dispatcher = Dispatcher.getInstance();
		Response response = null;
		try{
			if( StringUtils.isEmpty(identifier))
				return Response.serverError().build();
			lock.lock();
			try {
				GsonBuilder builder = new GsonBuilder();
				Gson gson = builder.enableComplexMapKeySerialization().create();
				String str = gson.toJson( null);
				response = Response.ok( str ).build();
			}
			finally {
				lock.unlock();
			}
		}
		catch( Exception ex ){
			logger.warning(ex.getMessage());
			return Response.serverError().build();
		}
		return response;
	}
	
	@SuppressWarnings("unused")
	private static class CreateRegisterData{
	}
}