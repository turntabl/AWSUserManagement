package io.turntabl.services;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
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
    public Document insert(String collectionName, String userEmail, Set<String> arnsRequest){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document document = new Document("userEmail", userEmail).append("arnsRequest", arnsRequest).append("timestamp", LocalDateTime.now()).append("status", "PENDING");
        collection.insertOne(document);
        return document;
    }

    /**
     * Keep collectionName -> requests
     * @param collectionName
     * @param userEmail
     */
    public boolean statusUpdate(String collectionName, String userEmail, String status){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document first = collection.find(Filters.eq("userEmail", userEmail)).first();
        assert first != null;
        first.append("status", status).append("timestamp", LocalDateTime.now());
        UpdateResult userEmail1 = collection.updateOne(Filters.eq("userEmail", userEmail), Updates.setOnInsert(first));
        return userEmail1.wasAcknowledged();
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

    public Document removeRequest(String collectionName, String requestId) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.findOneAndDelete(Filters.eq("_id", requestId));
    }

    public Document getRequestDetails(String collectionName, String requestId) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find(Filters.eq("_id", requestId)).first();
    }

    public List<Document> approvedPermissions(String collectionName){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<Document> approvedList = new ArrayList<>();
        for (Document document : collection.find()){
            approvedList.add(document);
        }
        return approvedList;
    }
}
