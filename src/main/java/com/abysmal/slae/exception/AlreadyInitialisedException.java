package com.abysmal.slae.exception;

public class AlreadyInitialisedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AlreadyInitialisedException(String msg) {
		super(msg);
	}

}