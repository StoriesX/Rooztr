package com.rooztr.exceptions;

public class UserNotExistException extends Exception{
	private static final long serialVersionUID = -5382382953295787898L;
	private String user;
	
	public UserNotExistException(String phone){
		this.user = phone;
	}

	@Override
	public String getMessage(){
		return user+" does not exist in the users database";
	}
}
