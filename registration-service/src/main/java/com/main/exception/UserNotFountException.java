package com.main.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//response status to send
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFountException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNotFountException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
