package com.sever.diploma.entities;

import javax.persistence.*;

import com.google.appengine.api.datastore.Key;

/**
 * Presents user who has access to service via google account. User has Google
 * email and role (user | admin)
 * 
 * @author Nejc Sever
 */
@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	private String email;
	private String role; // "user" or "admin"

	public User(String email, String role) {
		super();
		this.email = email;
		this.role = role;
	}

	/* Getters and setters */

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Key getId() {
		return id;
	}
}