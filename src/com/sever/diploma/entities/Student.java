package com.sever.diploma.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.google.appengine.api.datastore.Key;

/**
 * Presents student that is solving quiz. Every student has moodleId and name.
 * 
 * @author Nejc Sever
 */
@Entity
public class Student implements Serializable {
	
	private static final long serialVersionUID = 1511397037869545124L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	private String name;
	private long moodleId;

	// Unowned relationships. Has to be done, because Google App Engine supports
	// only one parent entity.
	// Note:
	// http://thoughts.inphina.com/2010/08/07/managing-multiple-parent-persistence-problem-in-app-engine/
	@Transient
	private List<Attempt> attempts;
	@Basic(fetch = FetchType.EAGER)
	private List<Key> attemptKeys = new ArrayList<Key>();

	public Student(String name, long moodleId) {
		super();
		this.name = name;
		this.moodleId = moodleId;
	}

	/**
	 * Adds attempt key to student
	 */
	public void addAttemptKey(Key attemptKey) {
		attemptKeys.add(attemptKey);
	}

	/* Getters and setters */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMoodleId() {
		return moodleId;
	}

	public void setMoodleId(long moodleId) {
		this.moodleId = moodleId;
	}

	public List<Key> getAttemptKeys() {
		return attemptKeys;
	}

	public void setAttemptKeys(List<Key> attemptKeys) {
		this.attemptKeys = attemptKeys;
	}

	public Key getId() {
		return id;
	}
}
