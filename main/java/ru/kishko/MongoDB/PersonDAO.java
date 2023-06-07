package ru.kishko.MongoDB;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.print.Doc;

import static com.mongodb.client.model.Filters.eq;

public class PersonDAO {
    private static MongoCollection<Document> collection = null;

    public PersonDAO(MongoCollection<Document> collection) {
        PersonDAO.collection = collection;
    }

    public static void create(Person person) {
        collection.insertOne(person.pojoToDoc());
    }

    public static FindIterable<Document> read(String id) {
        return collection.find(eq("_id", new ObjectId(id)));
    }

    public static void update(Person person, String id) {
        collection.replaceOne(eq("_id", new ObjectId(id)), person.pojoToDoc());
    }

    public static void delete(String id) {
        collection.deleteOne(eq("_id", new ObjectId(id)));
    }
}
