package com.bucketlist.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.bucketlist.dto.GetUsernameResponseDto;
import com.bucketlist.dto.PasswordModificationDto;
import com.bucketlist.dto.UpdateUsernameResponseDto;
import com.bucketlist.dto.UsernameModificationDto;
import com.bucketlist.entity.User;
import com.bucketlist.repository.PictureRepository;
import com.bucketlist.repository.RoleRepository;
import com.bucketlist.repository.UserRepository;
import com.bucketlist.security.jwt.JWTUtils;
import com.bucketlist.security.service.UserDetailsImpl;
import com.bucketlist.security.service.UserDetailsServiceImpl;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	PictureRepository pictureRepository;
	
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PicturesStorageService picturesStorageService;

	@Autowired
	JWTUtils jwtUtils;

	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;

	// Methods for ROLE_USER

	public User getLoggedUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	public GetUsernameResponseDto getUsername() {

		User user = getLoggedUser();
		GetUsernameResponseDto getUsernameResponseDto = new GetUsernameResponseDto();
		getUsernameResponseDto.setUsername(user.getUsername());

		return getUsernameResponseDto;
	}

	public UpdateUsernameResponseDto updateUsername(UsernameModificationDto userModificationDto) {

		User user = getLoggedUser();
		String updatedUsername = null;
		String updatedToken = null;

		// return object with null content
		if (user.getUsername().equals(userModificationDto.getUsername())) {
			return new UpdateUsernameResponseDto(updatedUsername, updatedToken);
		}

		if (userRepository.existsByUsername(userModificationDto.getUsername()))
			throw new ResponseStatusException(HttpStatus.CONFLICT);

		if (!user.getUsername().equals(userModificationDto.getUsername())) {
			user.setUsername(userModificationDto.getUsername());
			updatedUsername = userModificationDto.getUsername();
			userRepository.save(user);
			UserDetailsImpl userDetailsImpl = userDetailsServiceImpl.loadUserByUsername(updatedUsername);
			updatedToken = jwtUtils.generateToken(userDetailsImpl);
		}

		return new UpdateUsernameResponseDto(updatedUsername, updatedToken);
	}

	public Map<String, String> updatePassword(PasswordModificationDto passwordModificationDto) {

		User user = getLoggedUser();
		
		String passwordToTest = passwordModificationDto.getCurrentPassword();
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(user.getUsername(), passwordToTest));
		
		if (authentication != null) {
			user.setPassword(passwordEncoder.encode(passwordModificationDto.getNewPassword()));
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		userRepository.save(user);

		HashMap<String, String> message = new HashMap<>();
		message.put("message", "Success");
		return message;
	}

}
