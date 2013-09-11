package com.sever.diploma.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sever.diploma.entities.Classroom;
import com.sever.diploma.entities.Seat;
import com.sever.diploma.helpers.EMFService;

public enum ClassroomDao {
	INSTANCE;

	public void add(String name, long rowCount, long columnCount, List<Seat> seats) {
		EntityManager em = EMFService.get().createEntityManager();

		Classroom classroom = new Classroom(name, rowCount, columnCount, seats);

		em.getTransaction().begin();
		try {
			em.persist(classroom);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
	}
	
	public void removeAllSeats(long id) {
		
		Key key = KeyFactory.createKey(Classroom.class.getSimpleName(), id);

		EntityManager em = EMFService.get().createEntityManager();
		Classroom classroom = null;
		
		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("SELECT c FROM Classroom c WHERE c.id = :myKey");
			q.setParameter("myKey", key);
			classroom = (Classroom) q.getSingleResult();
			classroom.getSeats().clear();
			em.persist(classroom);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
	}

	public void remove(long id) {
		Key key = KeyFactory.createKey(Classroom.class.getSimpleName(), id);
		EntityManager em = EMFService.get().createEntityManager();
		Classroom classroom = null;
		
		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("SELECT c FROM Classroom c WHERE c.id = :myKey");
			q.setParameter("myKey", key);
			classroom = (Classroom) q.getSingleResult();
			em.remove(classroom);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
	}

	public Classroom getById(long id) {
		Key key = KeyFactory.createKey(Classroom.class.getSimpleName(), id);

		EntityManager em = EMFService.get().createEntityManager();
		Classroom classroom = null;
		
		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("SELECT c FROM Classroom c WHERE c.id = :myKey");
			q.setParameter("myKey", key);
			classroom = (Classroom) q.getSingleResult();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
		return classroom;			
	}

	@SuppressWarnings("unchecked")
	public List<Classroom> getClassrooms() {
		EntityManager em = EMFService.get().createEntityManager();
		List<Classroom> classrooms = null;
		
		em.getTransaction().begin();
		try {
			Query q = em.createQuery("select c from Classroom c");
			classrooms = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
		return classrooms;
	}
}