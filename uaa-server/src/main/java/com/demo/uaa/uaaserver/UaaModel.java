package com.demo.uaa.uaaserver;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tokenInformation")
public class UaaModel {
	
	@Id
	private String token;//token be the Id 
	private String publicKey;
	
	public UaaModel(String publicKey, String token) {
		super();
		this.publicKey = publicKey;
		this.token = token;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	@Override
	public String toString() {
		return "UaaModel [publicKey=" + publicKey + ", token=" + token + "]";
	}
	
}
