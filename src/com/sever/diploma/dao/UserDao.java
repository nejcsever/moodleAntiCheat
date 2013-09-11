package com.sever.diploma.dao;

import java.util.List;

import javax.persistence.*;

import com.sever.diploma.entities.User;
import com.sever.diploma.helpers.EMFService;

public enum UserDao {
	INSTANCE;
	
	@SuppressWarnings("unchecked")
	public void remove(String email) {	
		
		EntityManager em = EMFService.get().createEntityManager();
		List<User> users = null;
		
		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("SELECT u FROM User u WHERE u.email = :email");
			q.setParameter("email", email);
			users = q.getResultList();
			if (users.get(0) != null) {
				em.remove(users.get(0));
			}
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
	}

	@SuppressWarnings("unchecked")
	public boolean exists(String email) {	
		
		EntityManager em = EMFService.get().createEntityManager();
		List<User> users = null;
		
		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("SELECT u FROM User u WHERE u.email = :email");
			q.setParameter("email", email);
			users = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
		
		return users.size() > 0;
	}
	
	@SuppressWarnings("unchecked")
	public boolean isAdmin(String email) {	
		
		EntityManager em = EMFService.get().createEntityManager();
		List<User> users = null;
		
		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.role = 'admin'");
			q.setParameter("email", email);
			users = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
		
		return users.size() > 0;
	}

	public void add(String email, String role) {
		EntityManager em = EMFService.get().createEntityManager();

		User user = new User(email, role);

		em.getTransaction().begin();
		try {
			em.persist(user);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
	}

	@SuppressWarnings("unchecked")
	public List<User> getAll() {
		EntityManager em = EMFService.get().createEntityManager();
		List<User> users = null;
		
		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("SELECT u FROM User u");
			users = q.getResultList();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
		
		return users;
	}
}