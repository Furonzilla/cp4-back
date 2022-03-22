package com.bucketlist.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateIdeaDto {

	@NotBlank
	@Size(min = 2, max = 100)
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
