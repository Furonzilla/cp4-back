package com.bucketlist.security.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.bucketlist.entity.Role;
import com.bucketlist.entity.User;

public class UserDetailsImpl implements UserDetails {

	private Long id;

	private String username;

	private String password;

	private List<Role> authorities;

	public static UserDetailsImpl build(User user) {
		UserDetailsImpl userDetailsImpl = new UserDetailsImpl();
		userDetailsImpl.setUsername(user.getUsername());
		userDetailsImpl.setPassword(user.getPassword());
		userDetailsImpl.setAuthorities(user.getAuthorities());
		userDetailsImpl.setId(user.getId());
		return userDetailsImpl;
	}

	@Override
	public List<Role> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthorities(List<Role> authorities) {
		this.authorities = authorities;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
