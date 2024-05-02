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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@Transactional(Transactional.TxType.SUPPORTS)
@ApplicationScoped
public class UserService {

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
            .append("roleId", roleDocument.getObjectId("_id").toHexString());

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

        Document query = new Document("persmissions", user.getRoleId());
        Document roleDocument = collection.find(query).first();

        Set<String> permissions;
        if (roleDocument != null) {
            String persmissionsString = roleDocument.getString("persmissions");
            permissions = Set.of(persmissionsString.split(","));
        } else {
            return Collections.emptySet();
        }

        return permissions;
    }

    public Response edit_user(User user, String jwtToken) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'edit_user'");
    }
    
}
