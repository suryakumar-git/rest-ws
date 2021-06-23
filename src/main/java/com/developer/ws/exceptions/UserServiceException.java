package com.developer.ws.exceptions;

public class UserServiceException extends RuntimeException{

	private static final long serialVersionUID = 3047261994602556929L;

	public UserServiceException(String message) {
		super(message);
	}
}
