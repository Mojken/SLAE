package com.abysmal.slae.message;

public class Message {
	private String message;
	private Object data;

	public Message(String message, Object data) {
		this.message = message;
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public String getMessage() {
		return message;
	}
}