package com.rooztr.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.rooztr.dao.UserDao;
import com.rooztr.exceptions.UserNotExistException;
import com.rooztr.model.CallRequest;
import com.rooztr.util.Secured;

/*
	notice:
	NullPointerException: one of the parameters, keys, or values is missing or invalid
	UserNotExistException: phone number is not found in the database
	NotAuthorizedException: code or token is not matching the database
	other Exception: other exceptions
*/
@Path("/rest")
public class Rest {
	@POST
	@Path("/initiate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response register(@FormParam("phone") String phone){
		try{
			UserDao.getInstance().sendTextMessage(phone);
			return Response.ok().build();
		}catch(NullPointerException e){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response login(@FormParam("phone") String phone, @FormParam("code") String code){
		try{
			String token = UserDao.getInstance().authenticate(phone, code);
			return Response.ok().header("token", token).build();
		}catch(NullPointerException e){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}catch(NotAuthorizedException e){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}
	
	
	@POST
	@Secured
	@Path("/request")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response request(@FormParam("toPhone") String toPhone, @FormParam("start") String start, @FormParam("end") String end, @FormParam("message") String message, @HeaderParam("phone") String fromPhone){
		try{
			String requestID = UserDao.getInstance().sendRequest(fromPhone, toPhone, start, end, message);
			return Response.ok().header("requestID", requestID).build();
		}catch(UserNotExistException e){
			System.out.println(e.getMessage());
			return Response.ok().header("error", 1).build();
		}catch(NumberFormatException e){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}catch(NullPointerException e){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@GET
	@Secured
	@Path("/waitlist")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CallRequest> allWaitList(@HeaderParam("phone") String phone, @QueryParam("from") String from, @QueryParam("to") String to){
		List<CallRequest> waitlist = new ArrayList<CallRequest>();
		try{
			waitlist = UserDao.getInstance().getWaitList(phone, from, to);
		}catch(Exception e){
			e.printStackTrace();
		}
		return waitlist;
	}
	
	@GET
	@Secured
	@Path("/requestlist")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CallRequest> allRequestList(@HeaderParam("phone") String phone, @QueryParam("from") String from, @QueryParam("to") String to){
		List<CallRequest> requestlist = new ArrayList<CallRequest>();
		try{
			requestlist = UserDao.getInstance().getRequestList(phone, from, to);
		}catch(Exception e){
			e.printStackTrace();
		}
		return requestlist;
	}
	
	
	@PUT
	@Secured
	@Path("/withdraw")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response withdraw(@HeaderParam("phone") String phone, @FormParam("id") String id){
		try{
			UserDao.getInstance().withdraw(phone, id);
			return Response.ok().build();
		}catch(NullPointerException e){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}
	
	@PUT
	@Secured
	@Path("/finish")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response finish(@HeaderParam("phone") String phone, @FormParam("id") String id){
		try{
			UserDao.getInstance().finish(phone, id);
			return Response.ok().build();
		}catch(NullPointerException e){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}
	
	@PUT
	@Secured
	@Path("/refuse")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refuse(@HeaderParam("phone") String phone, @FormParam("id") String id){
		try{
			UserDao.getInstance().refuse(phone, id);
			return Response.ok().build();
		}catch(NullPointerException e){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}
	
	@PUT
	@Secured
	@Path("/requesterdelete")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response requesterDelete(@HeaderParam("phone") String phone, @FormParam("id") String id){
		try{
			UserDao.getInstance().requesterDelete(phone, id);
			return Response.ok().build();
		}catch(NullPointerException e){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}
	
	@PUT
	@Secured
	@Path("/requesteedelete")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response requesteeDelete(@HeaderParam("phone") String phone, @FormParam("id") String id){
		try{
			UserDao.getInstance().requesteeDelete(phone, id);
			return Response.ok().build();
		}catch(NullPointerException e){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}
}
