package org.timetracker_server.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.timetracker_server.models.Entry;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import config.AppConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.netty.handler.codec.dns.DnsPtrRecord;
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
                List<Document> documents = new ArrayList<>();
                collection.find(query).iterator().forEachRemaining(documents::add);
        
                return Response.ok(documents).build();
            } catch (MongoException e) {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorised to do this!").build();
        }

    }

    public Response startEntry(Entry entry, String jwtToken) {

        Jws<Claims> editUserClaim = null;
        try {
            editUserClaim = securityService.verifyJwt(jwtToken);
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(e.getMessage()).build();
        }

        String issuer = config.jwtIssuer() != null ? config.jwtIssuer() : System.getenv("JWT_ISSUER");

        if (editUserClaim.getPayload().getIssuer().equals(issuer) && editUserClaim.getPayload().get("upn").equals(entry.getUsername())) {

            try {
                
                Document entryDocument = new Document()
                .append("name", entry.getName())
                .append("startTime", entry.getStartTime())
                .append("username", entry.getUsername());
    
                MongoDatabase database = mongoClient.getDatabase("timetracker");
                MongoCollection<Document> collection = database.getCollection("entries");
                collection.insertOne(entryDocument);

                Document query = new Document();
                query.append("name", entry.getName());
                query.append("username", entry.getUsername());
        
                return Response.ok(collection.find(query).first()).entity("The entry has been created").build();
            } catch (MongoException e) {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorised to do this!").build();
        }

    }

    public Response stopEntry(Entry entry, LocalDateTime stopTime, String jwtToken) {

        Jws<Claims> editUserClaim = null;
        
        try {
            editUserClaim = securityService.verifyJwt(jwtToken);
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(e.getMessage()).build();
        }

        String issuer = config.jwtIssuer() != null ? config.jwtIssuer() : System.getenv("JWT_ISSUER");

        if (editUserClaim.getPayload().getIssuer().equals(issuer) && editUserClaim.getPayload().get("upn").equals(entry.getUsername())) {

            try {

                MongoDatabase database = mongoClient.getDatabase("timetracker");
                MongoCollection<Document> collection = database.getCollection("entries");
                Document query = new Document("_id", entry.getEntryId());
                Document setStopTime = new Document("$set", new Document("stopTime", stopTime));
                collection.updateOne(query, setStopTime);

                return Response.ok(collection.find(query).first()).build();
                
            } catch (MongoException e) {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorised to do this!").build();
        }
    }
    
}
