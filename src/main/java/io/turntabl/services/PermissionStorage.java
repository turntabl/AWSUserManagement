package io.turntabl.services;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

public class PermissionStorage {
    // private static String client_url = "mongodb://" +  System.getenv("ME_CONFIG_MONGODB_ADMINUSERNAME") + ":" +  System.getenv("ME_CONFIG_MONGODB_ADMINPASSWORD") + "@" + "turntabl.io:27017/" + "permissions";
    private MongoDatabase database;

    private static MongoClient CLIENT = new MongoClient(
        new MongoClientURI("mongodb://mongo:27017/" + "permissions")
    );


    public PermissionStorage() {
        System.out.println( System.getenv("ME_CONFIG_MONGODB_ADMINUSERNAME"));
        this.database = CLIENT.getDatabase("permissions");
    }

    /**
     * Keep collectionName -> requests
     * @param collectionName
     * @param userEmail
     * @param arnsRequest
     */
    public void insert(String collectionName, String userEmail, Set<String> arnsRequest){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document document = new Document("userEmail", userEmail).append("arnsRequest", arnsRequest).append("timestamp", LocalDateTime.now()).append("status", "PENDING");
        collection.insertOne(document);
    }

    /**
     * Keep collectionName -> requests
     * @param collectionName
     * @param userEmail
     */
    public void statusUpdate(String collectionName, String userEmail, String status){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.updateOne(Filters.eq("userEmail", userEmail), Updates.set("status", status));
    }

    /**
     * Keep collectionName -> requests
     * @param collectionName
     * @return
     */
    public List<Document> fetchAllRecords(String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        FindIterable<Document> iterable = collection.find();

        List<Document> records = new ArrayList<>();

        for (Document document : iterable) {
           records.add(document);
        }
        return records;
    }

}
