package com.sever.diploma.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.google.appengine.api.datastore.Key;

/**
 * Seat presents one seat in classroom. Attribute classroom tells in which
 * classroom seat is located. Row and column are x and y coordinates in
 * classroom grid. Every seat has ipv4 or ipv6 or both of them.
 * 
 * @author Nejc Sever
 */
@Entity
public class Seat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	private long row;
	private long column;
	private String ipv4;
	private String ipv6;
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Classroom.class)
	private Classroom classroom;

	/**
	 * Construstor for both, ipv6 and ipv4.
	 * 
	 * @param row
	 * @param column
	 * @param ipv4
	 * @param ipv6
	 */
	public Seat(long row, long column, String ipv4, String ipv6) {
		super();
		this.row = row;
		this.column = column;
		this.ipv4 = (ipv4.equals("")) ? null : ipv4;
		this.ipv6 = (ipv6.equals("")) ? null : ipv6;
	}

	/* Getters and setters. */

	public long getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public long getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
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

	public Classroom getClassroom() {
		return classroom;
	}

	public void setClassroom(Classroom classroom) {
		this.classroom = classroom;
	}
}
