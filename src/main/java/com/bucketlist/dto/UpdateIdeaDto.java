package com.bucketlist.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UpdateIdeaDto {

	@NotBlank
	@Size(min = 2, max = 100)
	private String title;

	@NotNull
	private Boolean deleteFile;

	public Boolean getDeleteFile() {
		return deleteFile;
	}

	public void setDeleteFile(Boolean deleteFile) {
		this.deleteFile = deleteFile;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
