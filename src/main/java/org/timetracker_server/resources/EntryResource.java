package org.timetracker_server.resources;

import java.time.LocalDateTime;

import org.bson.Document;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.timetracker_server.models.Entry;
import org.timetracker_server.services.EntryService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
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

    @POST
    @Path("/start-entry")
    public Response startEntry(@RequestBody Entry entry, @HeaderParam("Authorization") String jwtToken) {
        return entryService.startEntry(entry, jwtToken);
    }

    @POST
    @Path("/end-entry")
    public Response stopEntry(@RequestBody Entry entry, LocalDateTime stopTime, @HeaderParam("Authorization") String jwtToken) {
        return entryService.stopEntry(entry, stopTime, jwtToken);
    }
    
}
