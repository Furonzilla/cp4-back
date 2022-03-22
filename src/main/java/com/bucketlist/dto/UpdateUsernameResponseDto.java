package com.bucketlist.dto;

import javax.validation.constraints.Size;

public class UpdateUsernameResponseDto {

	@Size(min = 3, max = 20)
	private String username;

	private String token;

	public UpdateUsernameResponseDto(String username, String token) {
		this.username = username;
		this.token = token;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public String getToken() {
		return token;
	}
	
}
