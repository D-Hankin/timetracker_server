package org.timetracker_server.services;

import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import config.AppConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@Transactional(Transactional.TxType.SUPPORTS)
@ApplicationScoped
public class EntryService {

    private final MongoClient mongoClient;

    @Inject
    public EntryService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Inject 
    SecurityService securityService;

    @Inject
    AppConfig config;

    public Response getEntriesByUsername(String username, String jwtToken) {
        
        Jws<Claims> editUserClaim = null;
        try {
            editUserClaim = securityService.verifyJwt(jwtToken);
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(e.getMessage()).build();
        }

        String issuer = config.jwtIssuer() != null ? config.jwtIssuer() : System.getenv("JWT_ISSUER");

        if (editUserClaim.getPayload().getIssuer().equals(issuer) && editUserClaim.getPayload().get("upn").equals(username)) {
            
            try {
                MongoDatabase database = mongoClient.getDatabase("timetracker");
                MongoCollection<Document> collection = database.getCollection("entries");
                Document query = new Document("username", username);
        
                return Response.ok(collection.find(query).iterator()).build();
            } catch (MongoException e) {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorised to do this!").build();
        }

    }
    
}
