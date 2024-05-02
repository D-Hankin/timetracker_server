package org.timetracker_server.services;

import java.util.Collections;
import java.util.Set;

import org.bson.Document;
import org.timetracker_server.models.User;

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

        return Response.ok().entity(collection.find(query).first()).build();
    }

    public Response createUser(User user) {
        // user.setRoleId(
        //     mongoClient.getDatabase("timetracker")
        //     .getCollection("roles"));
        return null;
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
    
}
