package com.bucketlist.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.bucketlist.security.service.UserDetailsServiceImpl;

public class AuthFilterToken extends OncePerRequestFilter {

	@Autowired
	JWTUtils jwtUtils;

	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;

	public String getTokenIfValid(HttpServletRequest request) {
		String token = this.getTokenFromHeader(request);
		if (token == null)
			return null;
		if (!jwtUtils.isValidToken(token))
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is invalid");
		return token;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = getTokenIfValid(request);
		if (token != null) {
			String username = jwtUtils.getUsernameFromToken(token);
			UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

			// Save user somewhere we can get it later
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
					userDetails.getAuthorities());
			auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			// Give to Spring the security context
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		filterChain.doFilter(request, response);
	}

	private String getTokenFromHeader(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		// "Authorization = 'Bearer jHghiJKhgioUoh.Hgoihmhu.jhuiytgouyigou'"
		if (authorization != null && authorization.startsWith("Bearer ")) {
			return authorization.substring(7);
		}
		return null;
	}

}
