package com.sever.diploma.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.google.appengine.api.datastore.Key;

/**
 * Classroom presents grid of seats, which has name, width and height;
 * 
 * @author Nejc Sever
 */
@Entity
public class Classroom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	private String name;
	private long rowCount;
	private long columnCount;
	@OneToMany(targetEntity = Seat.class, mappedBy = "classroom", cascade = CascadeType.ALL)
	private List<Seat> seats = new ArrayList<Seat>();

	/**
	 * Entity constructor.
	 */
	public Classroom(String name, long rowCount, long columnCount,
			List<Seat> seats) {
		super();
		this.name = name;
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		this.seats = seats;
	}

	public void addSeat(Seat seat) {
		seats.add(seat);
	}

	/* Getters and setters. */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public long getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public List<Seat> getSeats() {
		return seats;
	}

	public void setSeats(List<Seat> seats) {
		this.seats = seats;
	}

	public Key getId() {
		return id;
	}
}
