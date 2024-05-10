package org.timetracker_server.models;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/")
public class ServerLive {

    @GET
    public Response weAreLive() {
        return Response.ok("Server is Live!").build();
    }
    
}
