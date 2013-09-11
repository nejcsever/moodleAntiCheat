package com.sever.diploma.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.google.appengine.api.datastore.Key;
import com.sever.diploma.dao.StudentDao;

/**
 * Class presents one attempt of solving quiz. Attempt can be time limited or
 * infinite (endTime = new Date(Long.MAX_VALUE)). Every attempt has one quiz
 * instance and one student instance.
 * 
 * @author Nejc Sever
 */
@Entity
public class Attempt implements Serializable {

	private static final long serialVersionUID = 6914268044528727944L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	private Date startTime;
	private Date endTime;
	private boolean timeLimited;
	private String browser;
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Quiz.class)
	private Quiz quiz;

	// Unowned relationships. Has to be done, because Google App Engine supports
	// only one parent entity.
	// Note:
	// http://thoughts.inphina.com/2010/08/07/managing-multiple-parent-persistence-problem-in-app-engine/
	@Transient
	private Student student;

	private Key studentKey;

	@OneToMany(targetEntity = Cheat.class, mappedBy = "attempt", cascade = CascadeType.ALL)
	private List<Cheat> cheats = new ArrayList<Cheat>();

	public Attempt(Date startTime, Date endTime, boolean timeLimited,
			Student student, String browser) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.studentKey = student.getId();
		this.student = student;
		this.timeLimited = timeLimited;
		this.browser = browser;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	/**
	 * Method that sets student transient field to studentKey stored in
	 * datastore. - Unowned relationships. Has to be done, because Google App
	 * Engine supports only one parent entity. Note:
	 * http://thoughts.inphina.com/
	 * 2010/08/07/managing-multiple-parent-persistence-problem-in-app-engine/
	 */
	@PostLoad
	protected void initStudent() {
		student = StudentDao.INSTANCE.getById(studentKey);
	}

	/**
	 * Adds cheat to the list of cheats.
	 */
	public void addCheat(Cheat cheat) {
		cheats.add(cheat);
	}

	/**
	 * Counts number of severe cheats. All cheats that are not 'ready' or
	 * 'unload'
	 */
	public int getSevereCheatCount() {
		if (cheats == null)
			return 0;
		int count = 0;
		for (Cheat c : cheats) {
			if(c.getType().equals("ready") || c.getType().equals("unload"))
				continue;
			count++;
		}
		return count;
	}

	/* Getters and setters. */

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public boolean isTimeLimited() {
		return timeLimited;
	}

	public void setTimeLimited(boolean timeLimited) {
		this.timeLimited = timeLimited;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Quiz getQuiz() {
		return quiz;
	}

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}

	public Key getStudentKey() {
		return studentKey;
	}

	public void setStudentKey(Key studentKey) {
		this.studentKey = studentKey;
	}

	public List<Cheat> getCheats() {
		return cheats;
	}

	public void setCheats(List<Cheat> cheats) {
		this.cheats = cheats;
	}

	public Key getId() {
		return id;
	}
}