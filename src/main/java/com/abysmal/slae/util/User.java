package com.abysmal.slae.util;

public class User {
	
	public String name;
	String passhash;
	
	public User(String name, String password) {
		this.name = name;
		passhash = password; //TODO: import hashing code.
	}
	
}