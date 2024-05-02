package org.timetracker_server.resources;

import org.timetracker_server.models.LoginDto;
import org.timetracker_server.services.SecurityService;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/secured")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SecurityResource {

    @Inject
    SecurityService securityService;
    
    @POST
    @Path("/login")
    @PermitAll
    public Response userLogin(@Valid final LoginDto loginDto) {
        return securityService.userLogin(loginDto);
    }

}
