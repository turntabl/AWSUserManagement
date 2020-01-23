package io.turntabl.service;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.List;

public class PermissionStorage {
    private MongoCredential credential = MongoCredential.createCredential("dawud-ismail", "mydatabase", "password".toCharArray());
    private static MongoClient CLIENT = new MongoClient("localhost", 27017);
    private MongoDatabase database;

    public PermissionStorage() {
        this.database = CLIENT.getDatabase("permisssions");
    }

    /**
     * Keep collectionName -> requests
     * @param collectionName
     * @param userEmail
     * @param arnsRequest
     */
    public void insert(String collectionName, String userEmail, List<String> arnsRequest){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document document = new Document("userEmail", userEmail).append("arnsRequest", arnsRequest).append("status", "PENDING");
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


}
