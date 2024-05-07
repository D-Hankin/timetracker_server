package org.timetracker_server.services;

import java.util.Collections;
import java.util.Set;

import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;
import org.timetracker_server.models.User;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import config.AppConfig;

import static com.mongodb.client.model.Filters.eq;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@Transactional(Transactional.TxType.SUPPORTS)
@ApplicationScoped
public class UserService {

    @Inject
    SecurityService securityService;

    @Inject 
    AppConfig config;

    private final MongoClient mongoClient;

    @Inject
    public UserService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Response findUser(String username) {

        MongoDatabase database = mongoClient.getDatabase("timetracker");
        MongoCollection<Document> collection = database.getCollection("users");
        Document query = new Document("username", username);

        return Response.ok(collection.find(query).first()).build();
    }

    public Response createUser(User user) {
        try {
            MongoDatabase database = mongoClient.getDatabase("timetracker");
            MongoCollection<Document> roleCollection = database.getCollection("roles");
            Document query = new Document("role", "user");
            Document roleDocument = roleCollection.find(query).first();

            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            Document userDocument = new Document()
            .append("username", user.getUsername())
            .append("name", user.getName())
            .append("password", hashedPassword)
            .append("email", user.getEmail())
            .append("roleId", roleDocument.getObjectId("_id"));

            MongoCollection<Document> userCollection = database.getCollection("users");
            userCollection.insertOne(userDocument);
            Document returnQuery = new Document("username", user.getUsername()); 
            userDocument = userCollection.find(returnQuery).first();
            userDocument.put("_id", userDocument.getObjectId("_id").toHexString());
            userDocument.remove("password");
            
            return Response.ok().entity(userDocument).build();

        } catch(MongoException e) {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity(e).build();
        }
        
    }

    public Set<String> getUserPermissions(final User user) {

        MongoDatabase database = mongoClient.getDatabase("timetracker");
        MongoCollection<Document> collection = database.getCollection("roles");
        System.out.println(user.getRoleId());
        Document query = new Document("_id", user.getRoleId());
        Document roleDocument = collection.find(query).first();
        System.out.println(roleDocument);
        Set<String> permissions;
        if (roleDocument != null) {
            String persmissionsString = roleDocument.getString("permissions");
            permissions = Set.of(persmissionsString.split(","));
        } else {
            return Collections.emptySet();
        }

        return permissions;
    }

    public Response editUser(User user, String jwtToken) throws Exception {

        Jws<Claims> editUserClaim = null;
        try {
            editUserClaim = securityService.verifyJwt(jwtToken);
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN.getStatusCode()).entity(e.getMessage()).build();
        }

        Response oldUser = findUser(user.getUsername());
        Document oldUserDoc = (Document) oldUser.getEntity();

        String issuer = config.jwtIssuer() != null ? config.jwtIssuer() : System.getenv("JWT_ISSUER");

        System.out.println(editUserClaim.getPayload().get("groups").toString());

        if (editUserClaim.getPayload().getIssuer().equals(issuer) && editUserClaim.getPayload().get("upn").equals(user.getUsername()) && editUserClaim.getPayload().get("groups").toString().contains("edit_user")) {

            try {
                MongoDatabase database = mongoClient.getDatabase("timetracker");
                MongoCollection<Document> collection = database.getCollection("users");

                oldUserDoc.append("username", user.getUsername())
                .append("name", user.getName())
                .append("password", user.getPassword())
                .append("email", user.getEmail());

                collection.replaceOne(eq("_id", oldUserDoc.getObjectId("_id")), oldUserDoc);

                return Response.ok().entity("You successfully edited your details").build();

            } catch (MongoException e) {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorised to do this!").build();
        }
    }

    // public Response getAllUsers(String jwtToken) {


    //     return null;
    // }
    
}
