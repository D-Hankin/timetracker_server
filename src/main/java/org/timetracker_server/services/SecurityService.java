package org.timetracker_server.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.Claims;
import org.timetracker_server.models.LoginDto;
import org.timetracker_server.models.Role;
import org.timetracker_server.models.TokenResponse;
import org.timetracker_server.models.User;
import org.xbill.DNS.Message;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@Transactional(Transactional.TxType.SUPPORTS)
@ApplicationScoped
public class SecurityService {

    @Inject
    UserService userService;

    public Response userLogin(final LoginDto loginDto) {
        if(checkUserCredentials(loginDto.getUsername(), loginDto.getPassword())) {
            Response userResponse = userService.findUser(loginDto.getUsername());
            User user = userResponse.readEntity(User.class);
            String token = generateJwtToken(user);
            return Response.ok().entity(new TokenResponse("Bearer " + token, "86400")).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorised login attempt!").build();
        }
    }

    private boolean checkUserCredentials(String username, String password) {

        return true;
    }

    private String generateJwtToken(final User user) {

        Set<String> userPermissions = userService.getUserPermissions(user);
        
        return Jwt.issuer("the-dark-lord")
            .upn(user.getUsername())
            .groups(userPermissions)
            .expiresIn(86400)
            .claim(Claims.email_verified.name(), user.getEmail())
            .sign();
    } 
}
