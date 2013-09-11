package com.sever.diploma.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * Cheat presents cheating in quiz e.g. pressing ctrl+c or loosing focus with
 * browser window. Every cheat has ipv4 or ipv6 from where cheat was executed.
 * Cheat has also browser name and timestamp. Every cheat belongs to one
 * attempt.
 * 
 * @author Nejc Sever
 */
@Entity
public class Cheat implements Serializable {
	
	private static final long serialVersionUID = 2399842394296328684L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	private String ipv4;
	private String ipv6;
	private String type;
	private Text additionalData;
	private Date timestamp;
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Attempt.class)
	private Attempt attempt;

	public Cheat(String ipv4, String ipv6, String type,
			Date timestamp, Text additionalData) {
		super();
		this.ipv4 = ipv4;
		this.ipv6 = ipv6;
		this.type = type;
		this.timestamp = timestamp;
		this.additionalData = additionalData;
	}

	/* Getters and setters */
	
	public Text getAdditionalData() {
		return additionalData;
	}
	
	public void setAdditionalData(Text additionalData) {
		this.additionalData = additionalData;
	}

	public String getIpv4() {
		return ipv4;
	}

	public void setIpv4(String ipv4) {
		this.ipv4 = ipv4;
	}

	public String getIpv6() {
		return ipv6;
	}

	public void setIpv6(String ipv6) {
		this.ipv6 = ipv6;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Attempt getAttempt() {
		return attempt;
	}

	public void setAttempt(Attempt attempt) {
		this.attempt = attempt;
	}

	public Key getId() {
		return id;
	}

	public int compareTo(Cheat c) {
		return this.timestamp.compareTo(c.timestamp);
	}
}
