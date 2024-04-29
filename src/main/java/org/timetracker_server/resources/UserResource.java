package org.timetracker_server.resources;

import org.timetracker_server.services.UserService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;
    
    @GET
    @Path("/user")
    public Response findUser(@HeaderParam("username") String username) {
        System.out.println("here i am");
        return userService.findUser("dhankin86");
    }

}
