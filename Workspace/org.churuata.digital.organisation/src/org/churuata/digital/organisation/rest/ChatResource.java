package org.churuata.digital.organisation.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.churuata.digital.core.utils.ChatUtils;
import org.churuata.digital.organisation.core.Dispatcher;
import org.churuata.digital.organisation.model.Organisation;
import org.churuata.digital.organisation.services.OrganisationService;
import org.condast.commons.persistence.service.TransactionManager;
import org.condast.commons.strings.StringUtils;


//Sets the path to base URL + /organisation/chat
@Path("/chat")
public class ChatResource{

	public static final String S_ERR_UNKNOWN_REQUEST = "An invalid request was rertrieved: ";
	public static final String S_ERR_INVALID_USER    = "The provided credentials are invalid:";

	public ChatResource() {
		super();
	}

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/ask")
	public Response create( @QueryParam("organisation-id") long organisationId, @QueryParam("name") String name, String data) {

		if( StringUtils.isEmpty(data)) 
			return Response.noContent().build();
	
		TransactionManager t = new TransactionManager( Dispatcher.getInstance() );
		OrganisationService os = new OrganisationService(); 
		Organisation organisation = null;
		try {
			t.open();
			organisation = os.find( organisationId);
			if( organisation == null )
				return Response.status( Status.NOT_FOUND).build();
					
			String str = ChatUtils.createChatMessage(organisation, name);
			return Response.ok( str ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
		finally {
			t.close();
		}
	}
}