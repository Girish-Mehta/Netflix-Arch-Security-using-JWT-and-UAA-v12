package com.demo.uaa.uaaserver;

import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "userInformation")
public class DetailsModal {

	@Id
	private String userId;
	@Size(min = 6, max = 12, message = "Password should be atleast of length 6 and max of length 12")
	private String password;
	private String role;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public DetailsModal() {
		super();

	}

	@Override
	public String toString() {
		return "UserInformation [userId=" + userId + ", password=" + password + ", role=" + role + "]";
	}

	public String getUserId() {
		return userId;
	}

	public DetailsModal(String userId,
			@Size(min = 6, max = 12, message = "Password should be atleast of length 6 and max of length 12") String password,
			String role) {
		super();
		this.userId = userId;
		this.password = password;
		this.role = role;
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
