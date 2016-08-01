package com.rooztr.util;

import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public class ConstructDB {
	
	public static void main(String[] args){
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		MongoDatabase db = mongoClient.getDatabase("rooztr");
		try{
			db.createCollection("users");
			//MongoCollection<Document> users = db.getCollection("users");
			//users.createIndex(new Document("phone", 1), new IndexOptions().unique(true));
			db.createCollection("registration");
			MongoCollection<Document> registration = db.getCollection("registration");
			registration.createIndex(new Document("createdAt", -1), new IndexOptions().expireAfter(30L, TimeUnit.SECONDS));
			db.createCollection("requests");
			MongoCollection<Document> requests = db.getCollection("requests");
			//requests.createIndex(new Document("requester", 1).append("requestee", 1).append("sentAt", -1));
			requests.createIndex(new Document("requester", 1).append("sentAt", -1));
			requests.createIndex(new Document("requestee", 1).append("sentAt", -1));
			//registration.createIndex(new Document("phone", 1), new IndexOptions().unique(true));
			System.out.println("connected to MongoDB");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
