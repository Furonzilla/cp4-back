package com.bucketlist.security.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bucketlist.security.service.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JWTUtils {

	@Value("${com.bucketlist.jwt.expirationDelay}")
	private Long expirationMs;

	@Value("${com.bucketlist.jwt.secretKey}")
	private String secretKey;

	public String generateToken(UserDetailsImpl userDetailsImpl) {
		return Jwts.builder().setSubject(userDetailsImpl.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + expirationMs))
				.signWith(SignatureAlgorithm.HS512, secretKey).compact();
	}

	public String getUsernameFromToken(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean isValidToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch (SignatureException e) {
			System.err.println("Invalid JWT signature:" + e.getMessage());
		} catch (MalformedJwtException e) {
			System.err.println("Invalid JWT token: " + e.getMessage());
		} catch (ExpiredJwtException e) {
			System.err.println("JWT token is expired: " + e.getMessage());
		} catch (UnsupportedJwtException e) {
			System.err.println("JWT token is unsupported: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			System.err.println("JWT claims string is empty: " + e.getMessage());
		}

		return false;
	}
}
