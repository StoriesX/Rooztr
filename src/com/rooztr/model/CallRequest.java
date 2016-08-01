package com.rooztr.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;

@XmlRootElement
public class CallRequest {
	public final static int DONE_REQUESTER_DELETED = -3; //done by requestee and deleted by requester
	public final static int REFUSED_REQUESTER_DELETED = -2; //refused by requestee and deleted by requester
	public final static int REQUESTER_DELETED = -1; //requestee will see this as requester withdrew the request
	public final static int WAITING = 0;
	public final static int WITHDRAWL = 1;
	public final static int DONE = 2;
	public final static int REFUSED = 3;
	public final static int REQUESTEE_DELETED = 4; //requester will see this as requestee refused the request
	public final static int WITHDRAWL_REQUESTEE_DELETED = 5; //withdrew by requester and deleted by requestee
	public final static int DONE_REQUESTEE_DELETED = 6; //done by requestee and deleted by requestee
	
	private String _id;
	private String requester;
	private String requestee;
	private Date start;
	private Date end;
	private int status;
	private String message;
	private Date sentAt;
	
	public CallRequest(){}
	
	public CallRequest(String _id, String requester, String requestee, Date start, Date end, int status, String message, Date sentAt){
		this._id = _id;
		this.requester = requester;
		this.requestee = requestee;
		this.start = start;
		this.end = end;
		this.status = status;
		this.message = message;
		this.sentAt = sentAt;
	}
	
	public String getRequester() {
		return requester;
	}

	public void setRequester(String requester) {
		this.requester = requester;
	}

	public String getRequestee() {
		return requestee;
	}

	public void setRequestee(String requestee) {
		this.requestee = requestee;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public Date getSentAt() {
		return sentAt;
	}

	public void setSentAt(Date sentAt) {
		this.sentAt = sentAt;
	}

	public static CallRequest parseCallRequest(Document doc) {
		return new CallRequest(doc.get("_id").toString(), doc.getString("requester"), doc.getString("requestee"), doc.getDate("start"), doc.getDate("end"), doc.getInteger("status"), doc.getString("message"), doc.getDate("sentAt"));
	}
}
