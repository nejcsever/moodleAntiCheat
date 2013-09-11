package com.sever.diploma.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.sever.diploma.entities.Student;
import com.sever.diploma.helpers.EMFService;

public enum StudentDao {
	INSTANCE;

	/**
	 * Checks if student with given parameters already exist. If student exists:
	 * return existing Student If student doesn't exist: add to database and
	 * return instance
	 */
	public Student uniqueAdd(String name, long moodleId) {
		Student s = getFirst(name, moodleId);
		if (s == null) {
			return add(name, moodleId);
		} else {
			return s;
		}
	}

	/**
	 * Adds new student to database and returns it's instance
	 */
	private Student add(String name, long moodleId) {
		EntityManager em = EMFService.get().createEntityManager();

		Student s = new Student(name, moodleId);

		em.getTransaction().begin();
		try {
			em.persist(s);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
		return s;
	}

	/**
	 * Returns first student with matching parameters. If there's no matching
	 * student it returns null.
	 */
	@SuppressWarnings("unchecked")
	private Student getFirst(String name, long moodleId) {
		// check cache first
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		Student student = (Student) cache.get("s*" + moodleId + "*" + name);
		if (student != null) {
			return student;
		}
		// if not in cache -> make read operation
		EntityManager em = EMFService.get().createEntityManager();
		List<Student> students = null;
		
		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("SELECT s FROM Student s WHERE s.name = :name AND s.moodleId = :moodleId");
			q.setParameter("name", name);
			q.setParameter("moodleId", moodleId);
			students = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
		student = (students != null && students.size() > 0) ? students.get(0)
				: null;
		if (student != null) {
			// store it to cache
			cache.put("s*" + moodleId + "*" + name, student);
		}
		return student;
	}
	
	/**
	 * Resturns student with matching ID.
	 */
	@SuppressWarnings("unchecked")
	public Student getById(Key key) {
		EntityManager em = EMFService.get().createEntityManager();
		List<Student> students = null;
		
		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("SELECT s FROM Student s WHERE s.id = :key");
			q.setParameter("key", key);
			students = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		}
		
		return (students.size() > 0)? students.get(0) : null;
	}
}