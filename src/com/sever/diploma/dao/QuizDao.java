package com.sever.diploma.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.sever.diploma.entities.Quiz;
import com.sever.diploma.helpers.EMFService;

public enum QuizDao {
	INSTANCE;

	/**
	 * Checks if quiz with given parameters already exist. If quiz exists:
	 * return existing Quiz If quiz doesn't exists: add to database and return
	 * element
	 */
	public Quiz uniqueAdd(String name, String subject, String url) {
		Quiz quiz = getFirst(name, subject, url);
		if (quiz == null) {
			return add(name, subject, url);
		} else {
			return quiz;
		}
	}

	/**
	 * Adds new quiz to database and returns instance
	 */
	private Quiz add(String name, String subject, String url) {
		EntityManager em = EMFService.get().createEntityManager();

		Quiz quiz = new Quiz(name, subject, url);

		em.getTransaction().begin();
		try {
			em.persist(quiz);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
		return quiz;
	}

	/**
	 * Returns first quiz with matching parameters. If no quiz matches returns
	 * null.
	 */
	@SuppressWarnings("unchecked")
	private Quiz getFirst(String name, String subject, String url) {
		// check memcache first
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		Quiz quiz = (Quiz) cache.get("q*" + name + "*" + subject + "*" + url);
		if (quiz != null) {
			return quiz;
		}
		// if not in memcache -> make read operation
		EntityManager em = EMFService.get().createEntityManager();
		List<Quiz> quizes = null;
		
		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("SELECT q FROM Quiz q WHERE q.name = :name AND q.subject = :subject AND q.url = :url");
			q.setParameter("name", name);
			q.setParameter("subject", subject);
			q.setParameter("url", url);
			quizes = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
		
		quiz = (quizes != null && quizes.size() > 0) ? quizes.get(0) : null;
		if (quiz != null) {
			// store it to cache
			cache.put("q*" + name + "*" + subject + "*" + url, quiz);
		}
		return quiz;
	}
	
	@SuppressWarnings("unchecked")
	public List<Quiz> getQuizzesBySubject(String subject) {
		EntityManager em = EMFService.get().createEntityManager();
		List<Quiz> quizzes = null;
		
		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("SELECT q FROM Quiz q WHERE q.subject = :subject");
			q.setParameter("subject", subject);
			quizzes = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
		return quizzes;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getAllSubjects() {
		EntityManager em = EMFService.get().createEntityManager();
		List<String> subjects = null;
		
		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("SELECT DISTINCT q.subject FROM Quiz q");
			subjects = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
		return subjects;
	}
}