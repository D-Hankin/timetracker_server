package org.timetracker_server.resources;

import org.timetracker_server.services.EntryService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/entry")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EntryResource {

    @Inject
    EntryService entryService;

    @GET
    @Path("/user")
    public Response getEntriesByUsername(@HeaderParam("username") String username, @HeaderParam("Authorization") String jwtToken) {
        return entryService.getEntriesByUsername(username, jwtToken);
    }
    
}