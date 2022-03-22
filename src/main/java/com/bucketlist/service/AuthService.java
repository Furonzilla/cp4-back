package com.bucketlist.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import com.bucketlist.dto.ConnectionDto;
import com.bucketlist.dto.ConnectionResponseDto;
import com.bucketlist.dto.UserDto;
import com.bucketlist.entity.ERole;
import com.bucketlist.entity.Role;
import com.bucketlist.entity.User;
import com.bucketlist.repository.RoleRepository;
import com.bucketlist.repository.UserRepository;
import com.bucketlist.security.jwt.JWTUtils;
import com.bucketlist.security.service.UserDetailsImpl;

@Service
public class AuthService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	JWTUtils jwtUtils;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserService userService;

	public ConnectionResponseDto signIn(@Valid @RequestBody(required = true) ConnectionDto connectionDto) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(connectionDto.getUsername(), connectionDto.getPassword()));

		// Authentication is put into SecurityContext to allows its access everywhere in
		// the application
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Get user from SecurityContext
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();

		String token = jwtUtils.generateToken(userDetailsImpl);

		User user = userService.getLoggedUser();

		return new ConnectionResponseDto(userDetailsImpl.getUsername(), userDetailsImpl.getAuthorities(), token);

	}

	public Map<String, String> signUp(@Valid UserDto userDto) {

		if (userRepository.existsByUsername(userDto.getUsername()))
			throw new ResponseStatusException(HttpStatus.CONFLICT);

		Role roleUser = roleRepository.findByAuthority(ERole.ROLE_USER.name())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		User user = new User();
		user.setUsername(userDto.getUsername());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		List<Role> roles = new ArrayList<>();
		roles.add(roleUser);
		user.setAuthorities(roles);
		userRepository.save(user);
		HashMap<String,String> message = new HashMap<>();
		message.put("message", "Success");
		return message;
	}

}
