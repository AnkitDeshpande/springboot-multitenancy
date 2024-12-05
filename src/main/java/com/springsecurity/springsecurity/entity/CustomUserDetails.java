package com.springsecurity.springsecurity.entity;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails extends User implements UserDetails {

	@Serial
	private static final long serialVersionUID = 1L;
	private final String username;
	private final String password;
	Collection<? extends GrantedAuthority> authorities;

	public CustomUserDetails(User byUsername) {
		this.username = byUsername.getUsername();
		this.password = byUsername.getPassword();
		List<GrantedAuthority> auths = new ArrayList<>();

		for (Role role : byUsername.getRoles()) {

			auths.add(new SimpleGrantedAuthority(role.getName().toUpperCase()));
		}
		this.authorities = auths;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

}