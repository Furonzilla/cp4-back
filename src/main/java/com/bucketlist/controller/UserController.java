package com.bucketlist.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bucketlist.dto.GetUsernameResponseDto;
import com.bucketlist.dto.PasswordModificationDto;
import com.bucketlist.dto.UpdateUsernameResponseDto;
import com.bucketlist.dto.UsernameModificationDto;
import com.bucketlist.repository.UserRepository;
import com.bucketlist.security.jwt.AuthFilterToken;
import com.bucketlist.service.PicturesStorageService;
import com.bucketlist.service.UserService;

@RestController
@RequestMapping("/api/current-user")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	AuthFilterToken authFilterToken;

	@Autowired
	PicturesStorageService picturesStorageService;

	@GetMapping("/username")
	@PreAuthorize("hasRole('ROLE_USER')")
	public GetUsernameResponseDto getUsername() {
		return userService.getUsername();
	}

	@PutMapping("/username/update")
	@PreAuthorize("hasRole('ROLE_USER')")
	public UpdateUsernameResponseDto updateUsername(@Valid @RequestBody(required = true) UsernameModificationDto usernameModificationDto) {
		return userService.updateUsername(usernameModificationDto);
	}
	
	@PutMapping("/password/update")
	@PreAuthorize("hasRole('ROLE_USER')")
	public Map<String, String> updatePassword(@Valid @RequestBody(required = true) PasswordModificationDto passwordModificationDto) {
		return userService.updatePassword(passwordModificationDto);
	}

}
