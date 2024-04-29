package org.timetracker_server.services;

import org.bson.Document;

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
    
}
