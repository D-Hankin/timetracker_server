package org.timetracker_server.resources;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.timetracker_server.models.User;
import org.timetracker_server.services.UserService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
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
        return userService.findUser(username);
    }

    @GET
    @Path("/admin/all")
    public Response getAllUsers(@HeaderParam("Authorization") String jwtToken) {
        return userService.getAllUsers(jwtToken);
    }

    @POST
    @Path("/create-user")
    public Response createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PATCH
    @Path("/edit-user")
    public Response editUser(@RequestBody User user, @HeaderParam("Authorization") String jwtToken) throws Exception {
        return userService.editUser(user, jwtToken);
    }

}
