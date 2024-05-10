package org.timetracker_server.services;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.timetracker_server.models.Entry;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

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
        System.out.println("admin incoming: " + editUserClaim.getPayload().get("groups").toString());
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
        } else if (editUserClaim.getPayload().getIssuer().equals(issuer) && editUserClaim.getPayload().get("groups").toString().contains("get_users")) {
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

        if (editUserClaim.getPayload().getIssuer().equals(issuer) && editUserClaim.getPayload().get("upn").equals(entry.getUsername()) && !findEntryByName(entry.getName(), entry.getUsername())) {

            try {
                
                Document entryDocument = new Document()
                .append("name", entry.getName())
                .append("minutes", 0)
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

    private boolean findEntryByName(String name, String username) {
        
        MongoDatabase database = mongoClient.getDatabase("timetracker");
        MongoCollection<Document> collection = database.getCollection("entries");
        Document query = new Document();
        query.append("name", name);
        query.append("username", username);

        Document result = collection.find(query).first();

        return result != null && !result.isEmpty();
    }

    public Response stopEntry(String username, String name, int seconds, String jwtToken) {

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
                Document query = new Document();
                query.append("username", username);
                query.append("name", name);
                Document setStopTime = new Document("$inc", new Document("minutes", seconds));
                collection.updateOne(query, setStopTime);
                System.out.println(collection.find(query).first());
                return Response.ok(collection.find(query).first()).entity("The time has been added!").build();
                
            } catch (MongoException e) {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorised to do this!").build();
        }
    }

    public Response deleteEntry(String entryId, String jwtToken) {

        Jws<Claims> editUserClaim = null;
        try {
            editUserClaim = securityService.verifyJwt(jwtToken);
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(e.getMessage()).build();
        }

        Document entry = findEntryById(entryId);

        String issuer = config.jwtIssuer() != null ? config.jwtIssuer() : System.getenv("JWT_ISSUER");

        if (editUserClaim.getPayload().getIssuer().equals(issuer) && editUserClaim.getPayload().get("upn").equals(entry.get("username"))) {
            
            try {
                
                MongoDatabase database = mongoClient.getDatabase("timetracker");
                MongoCollection<Document> collection = database.getCollection("entries");
                ObjectId queryId = new ObjectId(entryId);
                Document query = new Document("_id", queryId);
                DeleteResult result = collection.deleteOne(query);
        
                if(result.getDeletedCount() > 0) {
                    return Response.ok().entity("Entry deleted.").build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).entity("Entry not found").build();
                }
            } catch (MongoException e) {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorised to do this!").build();
        }
    }

    public Response editEntry(Entry entry, String jwtToken) {
        
        Jws<Claims> editEntryClaim = null;
        try {
            editEntryClaim = securityService.verifyJwt(jwtToken);
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(e.getMessage()).build();
        }

        String issuer = config.jwtIssuer() != null ? config.jwtIssuer() : System.getenv("JWT_ISSUER");
        Document oldEntry = findEntryById(entry.getEntryId());

        if (editEntryClaim.getPayload().getIssuer().equals(issuer) && editEntryClaim.getPayload().get("upn").equals(oldEntry.get("username"))) {
            
            try {
                
            } catch (MongoException e) {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorised to do this!").build();
        }

        
        return null;
    }

    private Document findEntryById(String entryId) {
        
        MongoDatabase database = mongoClient.getDatabase("timetracker");
        MongoCollection<Document> collection = database.getCollection("entries");
        ObjectId queryId = new ObjectId(entryId);
        Document query = new Document("_id", queryId);

        return collection.find(query).first();
    }
}
