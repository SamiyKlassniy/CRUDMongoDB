package ru.kishko.MongoDB;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Data;
import org.bson.Document;

@Data
public class Connection {

    private String login;
    private String password;
    private PersonDAO personDAO;
    private String connectionString;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    public static MongoCollection<Document> userCollection;
    private MongoCollection<Document> collection;

    public Connection(String login, String password) {

        connectionString = "mongodb://" + login + ":" + password + "@localhost:27017";
        mongoClient = MongoClients.create(connectionString);
        mongoDatabase = mongoClient.getDatabase(MongoDB.database);
        collection = mongoDatabase.getCollection(MongoDB.collectionString);
        userCollection = mongoDatabase.getCollection(MongoDB.usersCollectionString);

        personDAO = new PersonDAO(collection);

    }

}
