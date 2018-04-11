package com.demo.uaa.uaaserver;

public class UserInformation {

	private String userId;

	private String password;
	private String role;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public UserInformation() {
		super();

	}

	public UserInformation(String userId, String password, String role) {
		super();
		this.userId = userId;
		this.password = password;
		this.role = role;
	}

	@Override
	public String toString() {
		return "UserInformation [userId=" + userId + ", password=" + password + ", role=" + role + "]";
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
