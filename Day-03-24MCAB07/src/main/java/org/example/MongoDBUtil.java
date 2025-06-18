package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBUtil {
    public static MongoClient getClient() {
        return MongoClients.create("mongodb://localhost:27017"); // use Atlas URI if online
    }

    public static MongoDatabase getDatabase() {
        return getClient().getDatabase("day3-tasks");
    }
}
