package com.bucketlist.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ConnectionDto {

	@NotBlank
	@Size(min = 3, max = 20)
	private String username;

	@NotBlank
	@Size(min = 8, max = 120)
	private String password;
	
	public ConnectionDto(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
