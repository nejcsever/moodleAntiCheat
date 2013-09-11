package com.sever.diploma.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.google.appengine.api.datastore.Key;

/**
 * Quiz presents moodle quiz, which has name, subject and URL to quiz. Those
 * attributes are set when javascript on server is generated and not parsed from
 * moodle. Quiz can have many attempts.
 * 
 * @author Nejc Sever
 */
@Entity
public class Quiz implements Serializable {

	private static final long serialVersionUID = 7863276890320502553L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	private String name;
	private String subject;
	private String url;
	@OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, targetEntity = Attempt.class)
	private List<Attempt> attempts = new ArrayList<Attempt>();

	public Quiz(String name, String subject, String url) {
		super();
		this.name = name;
		this.subject = subject;
		this.url = url;
	}

	/**
	 * Adds attempt to attempt list.
	 */
	public void addAttempt(Attempt a) {
		attempts.add(a);
	}

	/* Getters and setters */

	public String getName() {
		return name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Attempt> getAttempts() {
		return attempts;
	}

	public void setAttempts(List<Attempt> attempts) {
		this.attempts = attempts;
	}

	public Key getId() {
		return id;
	}
}
