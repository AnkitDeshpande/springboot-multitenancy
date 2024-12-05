package com.springsecurity.springsecurity.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Service for getting Token and Validating token.
 */
@Service
public class JwtService {

	/**
	 * Expiration constant for 10 days.
	 */
	public static final long EXPIRATION_TIME_10DAYS = 864_000_000; // 10 days

	/**
	 * Expiration constant for 1 day.
	 */
	public static final long EXPIRATION_TIME_1DAY = 864_000_000; // 1 day

	/**
	 * key for signing the token.
	 */
	@Value("${token.signing.key}")
	private String jwtSigningKey;

	/**
	 * Extract userName for token.
	 *
	 * @param token
	 * @return userName
	 */
	public String extractUserName(final String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Generates token for the user.
	 *
	 * @param userDetails
	 * @return token
	 */
	public String generateToken(final String username) {
		return generateToken(new HashMap<>(), username);
	}

	/**
	 * Validates the token for the given user.
	 *
	 * @param token
	 * @param userDetails
	 * @return TRUE if valid, else FALSE
	 */
	public boolean isTokenValid(final String token, final UserDetails userDetails) {
		final String userName = extractUserName(token);
		return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	/**
	 * Extract claim from the token.
	 *
	 * @param token
	 * @param claimsResolvers
	 * @param <T>
	 * @return claim
	 */
	private <T> T extractClaim(final String token, final Function<Claims, T> claimsResolvers) {
		final Claims claims = extractAllClaims(token);
		return claimsResolvers.apply(claims);
	}

	/**
	 * Generate token utility.
	 *
	 * @param extraClaims
	 * @param userDetails
	 * @return token
	 */
	private String generateToken(final Map<String, Object> extraClaims, final String username) {
		return Jwts.builder().setClaims(extraClaims).setSubject(username)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_1DAY))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	/**
	 * Checks of the token is expired.
	 *
	 * @param token
	 * @return TRUE if expired, else FALSE
	 */
	private boolean isTokenExpired(final String token) {
		return extractExpiration(token).before(new Date());
	}

	/**
	 * Get expiration date.
	 *
	 * @param token
	 * @return Date
	 */
	private Date extractExpiration(final String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Extrat All claims from token.
	 *
	 * @param token
	 * @return Claims
	 */
	private Claims extractAllClaims(final String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	/**
	 * Get signing key.
	 *
	 * @return Key
	 */
	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String invalidateToken(String token) {
		Claims claims = extractAllClaims(token);
		claims.setExpiration(new Date(System.currentTimeMillis()));
		return Jwts.builder().setClaims(claims).signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}
}
