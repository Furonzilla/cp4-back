package com.bucketlist.dto;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.bucketlist.entity.Role;

public class ConnectionResponseDto {

	private Long id;

	@NotBlank
	@Size(min = 2, max = 20)
	private String username;

	@NotNull
	@Size(min = 1, max = 1)
	private List<Role> authorities;

	@NotBlank
	private String token;

	public ConnectionResponseDto(String username, List<Role> authorities, String token) {
		this.username = username;
		this.authorities = authorities;
		this.token = token;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setAuthorities(List<Role> authorities) {
		this.authorities = authorities;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public List<Role> getAuthorities() {
		return authorities;
	}

	public String getToken() {
		return token;
	}

}
