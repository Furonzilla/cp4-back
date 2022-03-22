package com.bucketlist.dto;

import javax.validation.constraints.Size;

public class PasswordModificationDto {

	@Size(min = 8, max = 120)
	private String currentPassword;
	
	@Size(min = 8, max = 120)
	private String newPassword;

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
