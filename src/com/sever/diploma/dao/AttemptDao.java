package com.sever.diploma.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.sever.diploma.entities.*;
import com.sever.diploma.helpers.EMFService;

public enum AttemptDao {
	INSTANCE;

	/**
	 * Checks if attemp with given parameters already exist. If attempt exists:
	 * return existing Attempt If attempt doesn't exist: add to database and
	 * return instance
	 * 
	 * @param endTime
	 *            - Attempt end time
	 * @param postTime
	 *            - Time when cheat was posted
	 */
	public Attempt uniqueAdd(Date postTime, Date endTime, Quiz quiz,
			boolean timeLimited, Student student, String browser) {
		Attempt a = exists(postTime, quiz, timeLimited, student);
		if (a == null) {
			return add(postTime, endTime, quiz, timeLimited, student, browser);
		} else {
			return a;
		}
	}
	
	/**
	 * Removes attempt and cheats belonging to this attempt.
	 */
	public void remove(long id, long quizId) {
		Key parentKey = KeyFactory
				.createKey(Quiz.class.getSimpleName(), quizId);
		Key key = KeyFactory.createKey(parentKey,
				Attempt.class.getSimpleName(), id);
		
		EntityManager em = EMFService.get().createEntityManager();
		Attempt attempt = null;
		Quiz quiz = null;
		Student student = null;
		
		try {
			// get attempt
			Query q = em
					.createQuery("SELECT a FROM Attempt a WHERE a.id = :myKey");
			q.setParameter("myKey", key);
			attempt = (Attempt) q.getSingleResult();
			// get quiz
			q = em.createQuery("SELECT q FROM Quiz q WHERE q.id = :myKey");
			q.setParameter("myKey", parentKey);
			quiz = (Quiz) q.getSingleResult();
			// get student
			q = em.createQuery("SELECT s FROM Student s WHERE s.id = :myKey");
			q.setParameter("myKey", attempt.getStudentKey());
			student = (Student) q.getSingleResult();
			
			// remove attempt from quiz and student + clear cache
			quiz.getAttempts().remove(attempt);
			student.getAttemptKeys().remove(key);
			MemcacheServiceFactory.getMemcacheService().delete("a*" + quiz.getId().getId() + "*" + student.getId().getId());
			
			em.getTransaction().begin();
			// handle quiz (if empty attempts -> delete quiz)
			if (quiz.getAttempts().size() > 0) {
				em.persist(quiz);
			} else {
				// first remove from cache
				MemcacheServiceFactory.getMemcacheService().delete("q*" + quiz.getName() + "*" + quiz.getSubject() + "*" + quiz.getUrl());
				em.remove(quiz);
			}
			em.getTransaction().commit();
			
			em.getTransaction().begin();
			// handle student (if empty student -> delete student)
			if (student.getAttemptKeys().size() > 0) {
				em.persist(student);
			} else {
				// first remove from cache
				MemcacheServiceFactory.getMemcacheService().delete("s*" + student.getMoodleId() + "*" + student.getName());
				em.remove(student);
			}
			em.getTransaction().commit();
			
			em.getTransaction().begin();
			em.remove(attempt);
			em.getTransaction().commit();
		} catch(Exception e) {
			//
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
	}

	/**
	 * Checks if there is existing attemp where: postTime > startTime AND
	 * postTime < endTime
	 * 
	 * @param postTime
	 *            - Time when cheat was posted
	 */
	@SuppressWarnings("unchecked")
	private Attempt exists(Date postTime, Quiz quiz, boolean timeLimited,
			Student student) {
		// check memcache first (attempt key in cache = a*quizId*studentId)
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		Attempt attempt = (Attempt) cache.get("a*" + quiz.getId().getId() + "*" + student.getId().getId());
		if (attempt != null) {
			// check if time matches
			if (attempt.isTimeLimited() != timeLimited || postTime.after(attempt.getEndTime())) {
				attempt = null;
				// remove, so new one can be inserted after datastore read below
				cache.delete("a*" + quiz.getId().getId() + "*" + student.getId().getId());
			} else {
				return attempt;
			}
		}
		// if not in memcache -> make read operation
		EntityManager em = EMFService.get().createEntityManager();
		List<Attempt> attempts = null;

		em.getTransaction().begin();
		try {
			String endOfQuery = " AND a.timeLimited = false";
			if (timeLimited) {
				endOfQuery = " AND a.endTime >= :postTime";
			}
			Query q = em
					.createQuery("SELECT a FROM Attempt a WHERE a.quiz = :quiz AND a.studentKey = :studentKey" + endOfQuery);
			q.setParameter("quiz", quiz);
			q.setParameter("studentKey", student.getId());
			q.setParameter("postTime", postTime);
			attempts = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		}
		attempt = (attempts != null && attempts.size() > 0) ? attempts.get(0) : null;
		if (attempt != null) {
			// store it to cache
			cache.put("a*" + quiz.getId().getId() + "*" + student.getId().getId(), attempt);
		}
		return attempt;
	}

	/**
	 * Adds new attempt to database and returns it's instance. Attempt iz added
	 * in Quiz entity and in Student entity.
	 */
	private Attempt add(Date startTime, Date endTime, Quiz quiz,
			boolean timeLimited, Student student, String browser) {
		EntityManager em = EMFService.get().createEntityManager();

		Attempt a = new Attempt(startTime, endTime, timeLimited, student,
				browser);

		/*
		 * Adding attempt to Quiz entity...also adds attempt to datastore, we
		 * don't need persist.
		 */
		em.getTransaction().begin();
		try {
			// This line must be here, because adding to instance doesn't work
			Quiz q = em.find(Quiz.class, quiz.getId());
			q.addAttempt(a);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		}
		em.getTransaction().begin();
		/* Adding attempt to Student */
		try {
			Student s = em.find(Student.class, student.getId());
			s.addAttemptKey(a.getId());
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

		}
		return a;
	}

	/**
	 * Gets all attempts and it's properties. Must be return Object[] because if
	 * Attemp, a.quiz returns null instance of quiz.
	 * 
	 * @return Object[0] = Attempt instance, Object[1] = Attempt.Quiz instance,
	 *         Object[2] = cheat count, Object[3] = student
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllAttempts() {
		EntityManager em = EMFService.get().createEntityManager();
		List<Object[]> attempts = null;

		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("select a, a.quiz, a.cheats from Attempt a");
			attempts = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

		}
		return attempts;
	}

	public List<Object[]> getAllByFilters(String dateFrom, String dateTo,
			String subject, String quizName, String studentName) {
		// handle dates
		Date parsedDateFrom = null;
		Date parsedDateTo = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, hh:mm");
		long gmtOffset = TimeZone.getTimeZone("GMT+2").getRawOffset();
		try {
			parsedDateFrom = new Date(dateFormat.parse(dateFrom).getTime() - gmtOffset);
		} catch (Exception e) {// date remains null
		}
		
		try {
			parsedDateTo = new Date(dateFormat.parse(dateTo).getTime() - gmtOffset);
		} catch (Exception e) {// date remains null
		}
		
		List<Object[]> attempts = getAllAttempts();
		List<Object[]> result = new ArrayList<Object[]>();
		// filter attempts
		for (Object[] a : attempts) {
			Attempt attempt = (Attempt) a[0];
			Quiz q = (Quiz) a[1];
			Student s = attempt.getStudent();
			
			// dates
			boolean dateMatch = (parsedDateFrom == null || attempt.getStartTime().getTime() > parsedDateFrom.getTime()) && (parsedDateTo == null || attempt.getStartTime().getTime() < parsedDateTo.getTime());
			// other filters
			boolean subjectMatch = subject != null && !subject.equals("") ? q
					.getSubject().equals(subject) : true;
			boolean quizMatch = quizName != null && !quizName.equals("") ? q
					.getName().equals(quizName) : true;
			boolean studentMatch = studentName != null
					&& !studentName.equals("") ? s.getName()
					.equals(studentName) : true;
			boolean match = dateMatch && subjectMatch && quizMatch
					&& studentMatch;
			if (match) {
				result.add(a);
			}
		}
		return result;
	}

	/**
	 * Gets all cheats for attempt.
	 */
	@SuppressWarnings("unchecked")
	public List<Cheat> getAllCheats() {
		EntityManager em = EMFService.get().createEntityManager();
		List<Cheat> cheats = null;

		em.getTransaction().begin();
		try {
			Query q = em.createQuery("select a.cheats from Attempt a");
			cheats = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

		}
		return cheats;
	}

	public Attempt getById(long id, long quizId) {
		Key parentKey = KeyFactory
				.createKey(Quiz.class.getSimpleName(), quizId);
		Key key = KeyFactory.createKey(parentKey,
				Attempt.class.getSimpleName(), id);

		EntityManager em = EMFService.get().createEntityManager();
		Attempt attempt = null;
		em.getTransaction().begin();
		try {
			attempt = em.find(Attempt.class, key);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

		}
		return attempt;
	}

	@SuppressWarnings("unchecked")
	public List<String> getQuizNamesBySubject(String subject) {
		EntityManager em = EMFService.get().createEntityManager();
		List<Quiz> quizzes = null;

		em.getTransaction().begin();
		try {
			Query q = em.createQuery("SELECT DISTINCT a.quiz FROM Attempt a");
			quizzes = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

		}
		// Filter by hands because Google App engine is not able to do it by
		// query....
		Set<String> names = new HashSet<String>();
		for (Quiz q : quizzes) {
			if (q.getSubject().equals(subject))
				names.add(q.getName());
		}
		return new ArrayList<String>(names);
	}

	@SuppressWarnings("unchecked")
	public List<String> getStudentNamesByQuizName(String quizName) {
		EntityManager em = EMFService.get().createEntityManager();
		List<Object[]> attempts = null;

		em.getTransaction().begin();
		try {
			Query q = em.createQuery("SELECT a.quiz, a.student FROM Attempt a");
			attempts = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

		}
		// Filter by hands because Google App engine is not able to do it by
		// query....
		Set<String> names = new HashSet<String>();
		for (Object[] a : attempts) {
			if (((Quiz) a[0]).getName().equals(quizName))
				names.add(((Student) a[1]).getName());
		}

		return new ArrayList<String>(names);
	}
}