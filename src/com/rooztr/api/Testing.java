package com.rooztr.api;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class Testing {

  public static void main(String[] args) {  
    ClientConfig config = new ClientConfig();

    Client client = ClientBuilder.newClient(config);

    WebTarget target = client.target(getBaseURI());
    //Response response = target.path("initiate").request().header("Username", "user2").header("Token", "jhgkb5o14pqc9hj8c09427a69f").get();
    //System.out.println(response.toString());
    //String plainAnswer = target.path("v1").path("register").request().accept(MediaType.TEXT_PLAIN).get(String.class);
    //String xmlAnswer = target.path("v1").path("register").request().accept(MediaType.TEXT_XML).get(String.class);
    //String htmlAnswer= target.path("v1").path("register").request().accept(MediaType.TEXT_HTML).get(String.class);
    
    int status = 4;
    if(status == 1){
    	Form form = new Form();
    	form.param("phone", "1234567893");
    	Response response = target.path("rest").path("initiate").request().post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED), Response.class);
    	System.out.println(response.toString());
    }else if(status == 2){
    	Form form = new Form();
    	form.param("phone", "1234567893");
    	form.param("code", "dpy8sy");
    	Response response = target.path("rest").path("login").request().post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED), Response.class);
    	System.out.println(response.toString());
    }else if(status == 3){
    	Form form = new Form();
    	form.param("toPhone", "1234567893");
    	form.param("start", "1469574942000");
    	form.param("end", "1469594942000");
    	form.param("message", "hello 90 to 93");
    	Response response = target.path("rest").path("request").request()
    			.header("phone", "1234567892").header("token", "45tj7i57lbfk99tm9vr19nf4c5").post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED), Response.class);
    	System.out.println(response.toString());
    	System.out.println(response.getHeaderString("error").toString());
    }else if(status == 4){
    	Response response = target.path("rest").path("waitlist").queryParam("from", "").queryParam("to", "").request()
    			.header("phone", "1234567893").header("token", "i6dbcp92a9o9mcv8oj65mp9e8g").get();
    	System.out.println(response.readEntity(String.class));
    }else if(status == 5){
    	Form form = new Form();
    	form.param("id", "579fa59789e20c14b46949b8");
    	Response response = target.path("rest").path("requesteedelete").request().header("phone", "1234567893").header("token", "i6dbcp92a9o9mcv8oj65mp9e8g")
    			.put(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED), Response.class);
    	System.out.println(response.toString());
    }else{
    	System.out.println(new Date(-1L));
    }
    
    /*
    	90: "il9bjda9rimkr260apmlvtavip"
    	91: "f0hostv76kujih6vkdvl3kdl1s"
    	92: "45tj7i57lbfk99tm9vr19nf4c5"
    	93: "i6dbcp92a9o9mcv8oj65mp9e8g"
    */
  }

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://localhost:8080/Rooztr/api/v1").build();
  }
}