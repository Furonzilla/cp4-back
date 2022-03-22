package com.bucketlist.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bucketlist.dto.ConnectionDto;
import com.bucketlist.dto.ConnectionResponseDto;
import com.bucketlist.dto.UserDto;
import com.bucketlist.security.jwt.JWTUtils;
import com.bucketlist.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthService authService;

	@Autowired
	JWTUtils jwtUtils;

	@PostMapping("/signin")
	public ConnectionResponseDto connect(@Valid @RequestBody(required = true) ConnectionDto connectionDto) {
		return authService.signIn(connectionDto);
	}

	@PostMapping("/signup")
	public Map<String, String> create(@Valid @RequestBody(required = true) UserDto userDto) {
		return authService.signUp(userDto);
	}

}
