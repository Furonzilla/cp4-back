package com.bucketlist.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.bucketlist.entity.Role;

public class GetUsernameResponseDto {

	private Long id;

	@NotBlank
	@Size(min = 3, max = 20)
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
