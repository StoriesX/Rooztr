package com.rooztr.dao;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.ws.rs.NotAuthorizedException;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.rooztr.exceptions.UserNotExistException;
import com.rooztr.model.CallRequest;
import com.rooztr.util.RandomCode;

public class UserDao {
	private static UserDao userdao;
	private final MongoDatabase db;
	private final Random random;
	private final RandomCode randomCode;
	private final MongoCollection<Document> registration;
	private final MongoCollection<Document> users;
	private final MongoCollection<Document> requests;
	
	public UserDao(){
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		db = mongoClient.getDatabase("rooztr");
		registration = db.getCollection("registration");
		users = db.getCollection("users");
		requests = db.getCollection("requests");
		System.out.println("connected to MongoDB");
		random = new SecureRandom();
		randomCode = new RandomCode();
	}
	
	public static UserDao getInstance(){
		if(userdao == null){
			userdao = new UserDao();
		}
		return userdao;
	}	
	
	public String authenticate(String phone, String code) throws Exception{
		//nullCheck(phone, code);
		Document user = registration.findOneAndDelete(new Document("_id", phone).append("code", code));
		if(user != null){
			String token = new BigInteger(130, random).toString(32);
			users.updateOne(new Document("_id", phone), new Document("$set", new Document("token", token)), new UpdateOptions().upsert(true));
			System.out.println("phone logged in: "+phone+" token: "+token);
			return token;
		}else{
			System.out.println("invalid user information");
			throw new NotAuthorizedException("Invalid user information");
		}
	}

	public void validate(String phone, String token) throws Exception{
		//nullCheck(phone, token);
		Document user = users.find(new Document("_id", phone).append("token", token)).first();
		if(user == null){
			throw new UserNotExistException(phone);
		}
		System.out.println(phone+" is validated");
	}

	public void sendTextMessage(String phone) throws Exception{
		nullCheck(phone);
		String code = randomCode.nextString();
		registration.updateOne(new Document("_id", phone), new Document("$set", new Document("code", code).append("createdAt", new Date())), new UpdateOptions().upsert(true));
		//send the text message here
		System.out.println("Send Text Message to Phone "+phone+" : "+code);
	}

	public CallRequest sendRequest(String fromPhone, String toPhone, String start, String end, String message) throws Exception{
		//nullCheck(fromPhone, start, end, toPhone);
		if(fromPhone.equals(toPhone)) throw new NullPointerException();
		Document user = users.find(new Document("_id", toPhone)).first();
		if(user == null){
			throw new UserNotExistException(toPhone);
		}else{
			Date s = new Date(Long.parseLong(start));
			Date e = new Date(Long.parseLong(end));
			if(s.after(e)){
				throw new NullPointerException(); //should create a new exception, but NullPointerException will just work
			}
			Document req = new Document("requester", fromPhone).append("requestee", toPhone).append("sentAt", new Date()).append("start", s).append("end", e).append("status", CallRequest.WAITING).append("message", message);
			requests.insertOne(req);
			System.out.println("request with ID was created: "+req.get("_id").toString());
			return CallRequest.parseCallRequest(req);
		}
	}
	
	private void nullCheck(String... params) throws Exception{
		for(String param: params){
			if(param == null){
				throw new NullPointerException();
			}
		}
	}

	public List<CallRequest> getWaitList(String phone, String from, String to) throws Exception{
		Date f = getDate(from, false);
		Date t = getDate(to, true);
		FindIterable<Document> iterable = requests.find(new Document("requestee", phone).append("sentAt", new Document("$gt", f).append("$lt", t)).append("status", new Document("$lt", CallRequest.REQUESTEE_DELETED)));
		List<CallRequest> waitlist = new ArrayList<CallRequest>();
		iterable.forEach(new Block<Document>(){
			@Override
			public void apply(Document doc) {
				waitlist.add(CallRequest.parseCallRequest(doc));
			}
		});
		System.out.println("waitlist size: "+waitlist.size());
		return waitlist;
	}
	
	public List<CallRequest> getRequestList(String phone, String from, String to) throws Exception{
		Date f = getDate(from, false);
		Date t = getDate(to, true);
		FindIterable<Document> iterable = requests.find(new Document("requester", phone).append("sentAt", new Document("$gt", f).append("$lt", t)).append("status", new Document("$gt", CallRequest.REQUESTER_DELETED)));
		List<CallRequest> requestlist = new ArrayList<CallRequest>();
		iterable.forEach(new Block<Document>(){
			@Override
			public void apply(Document doc) {
				requestlist.add(CallRequest.parseCallRequest(doc));
			}
		});
		System.out.println("requestlist size: "+requestlist.size());
		return requestlist;
	}

	private Date getDate(String date, boolean passive) throws Exception{
		long d = 0L;
		if(date == null || date.length() == 0){
			d = passive? Long.MAX_VALUE : Long.MIN_VALUE;
		}else{
			d = Long.parseLong(date);
		}
		return new Date(d);
	}

	public void withdraw(String phone, String id) throws Exception{
		Document doc = requests.findOneAndUpdate(
				new Document("_id", new ObjectId(id)).append("requester", phone).append("status", new Document("$eq", CallRequest.WAITING)),
				new Document("$set", new Document("sentAt", new Date()).append("status", CallRequest.WITHDRAWL)));
		if(doc == null){
			throw new NullPointerException();
		}
	}
	
	public void finish(String phone, String id) throws Exception{
		Document doc = requests.findOneAndUpdate(
				new Document("_id", new ObjectId(id)).append("requestee", phone).append("status", new Document("$eq", CallRequest.WAITING)),
				new Document("$set", new Document("sentAt", new Date()).append("status", CallRequest.DONE)));
		if(doc == null){
			throw new NullPointerException();
		}
	}
	
	public void refuse(String phone, String id) throws Exception{
		Document doc = requests.findOneAndUpdate(
				new Document("_id", new ObjectId(id)).append("requestee", phone).append("status", new Document("$eq", CallRequest.WAITING)),
				new Document("$set", new Document("sentAt", new Date()).append("status", CallRequest.REFUSED)));
		if(doc == null){
			throw new NullPointerException();
		}
	}

	public void requesterDelete(String phone, String id) throws Exception{
		Document doc = requests.find(new Document("_id", new ObjectId(id)).append("requester", phone)).first();
		int stats = doc.getInteger("status").intValue(); //throw NullPointerException if doc is null
		if(stats >= CallRequest.REQUESTEE_DELETED){
			requests.findOneAndDelete(doc);
		}else if(stats == CallRequest.WAITING){
			requests.findOneAndUpdate(doc, new Document("$set", new Document("sentAt", new Date()).append("status", CallRequest.REQUESTER_DELETED)));
		}else if(stats == CallRequest.WITHDRAWL){
			requests.findOneAndUpdate(doc, new Document("$set", new Document("status", CallRequest.REQUESTER_DELETED)));
		}else if(stats == CallRequest.DONE){
			requests.findOneAndUpdate(doc, new Document("$set", new Document("status", CallRequest.DONE_REQUESTER_DELETED)));
		}else if(stats == CallRequest.REFUSED){
			requests.findOneAndUpdate(doc, new Document("$set", new Document("status", CallRequest.REFUSED_REQUESTER_DELETED)));
		}
	}

	public void requesteeDelete(String phone, String id) throws Exception{
		Document doc = requests.find(new Document("_id", new ObjectId(id)).append("requestee", phone)).first();
		int stats = doc.getInteger("status").intValue(); //throw NullPointerException if doc is null
		if(stats <= CallRequest.REQUESTER_DELETED){
			requests.findOneAndDelete(doc);
		}else if(stats == CallRequest.WAITING){
			requests.findOneAndUpdate(doc, new Document("$set", new Document("sentAt", new Date()).append("status", CallRequest.REQUESTEE_DELETED)));
		}else if(stats == CallRequest.WITHDRAWL){
			requests.findOneAndUpdate(doc, new Document("$set", new Document("status", CallRequest.WITHDRAWL_REQUESTEE_DELETED)));
		}else if(stats == CallRequest.DONE){
			requests.findOneAndUpdate(doc, new Document("$set", new Document("status", CallRequest.DONE_REQUESTEE_DELETED)));
		}else if(stats == CallRequest.REFUSED){
			requests.findOneAndUpdate(doc, new Document("$set", new Document("status", CallRequest.REQUESTEE_DELETED)));
		}
	}
	
}
