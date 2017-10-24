package com.thomsonreuters.trtn.wisteria.core;

public class LoginToken {
	private String username;
	private String password;
	
	public LoginToken(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
}
