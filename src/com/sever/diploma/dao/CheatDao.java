package com.sever.diploma.dao;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sever.diploma.entities.*;
import com.sever.diploma.helpers.EMFService;

public enum CheatDao {
	INSTANCE;
	
	private static final Map<String, String> CHEAT_DESCRIPTIONS;
    static
    {
    	CHEAT_DESCRIPTIONS = new HashMap<String, String>();
    	CHEAT_DESCRIPTIONS.put("ready", "Nadaljevanje reševanja");
    	CHEAT_DESCRIPTIONS.put("unload", "Odhod iz strani");
    	CHEAT_DESCRIPTIONS.put("rightClick", "Desni miškin klik");
    	CHEAT_DESCRIPTIONS.put("focus", "Izguba fokusa brskalnika");
    	CHEAT_DESCRIPTIONS.put("prntScrn", "Zajem zaslona");
    	CHEAT_DESCRIPTIONS.put("CTRL+C", "Kopiranje besedila");
    	CHEAT_DESCRIPTIONS.put("CTRL+A", "Ozna�?evanje besedila");
    	CHEAT_DESCRIPTIONS.put("CTRL+X", "Kopiranje besedila");
    	CHEAT_DESCRIPTIONS.put("CTRL+V", "Kopiranje besedila");
    	CHEAT_DESCRIPTIONS.put("CTRL+S", "Shranjevanje strani");
    	CHEAT_DESCRIPTIONS.put("CTRL+U", "Poskus ogleda izvorne kode");
    }
	
	/**
	 * Gets description for cheat type.
	 */
	public static String getDescription(String type) {
		return CHEAT_DESCRIPTIONS.get(type) == null ? "" : CHEAT_DESCRIPTIONS.get(type);
	}

	/**
	 * Adds cheat to database. It also adds timestamp to cheat entity.
	 * Must have quizId, so we can generate attempt id, because quiz is parent of attempt.
	 */
	public void add(Cheat cheat, Long attemptId, Long quizId) {
		Key quizKey = KeyFactory.createKey(Quiz.class.getSimpleName(), quizId);
		Key attemptKey = KeyFactory.createKey(quizKey, Attempt.class.getSimpleName(), attemptId);
		
		EntityManager em = EMFService.get().createEntityManager();
		em.getTransaction().begin();
		try {
			Attempt attempt = em.find(Attempt.class, attemptKey);
			attempt.addCheat(cheat);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
	}
	
	public Cheat getById(long id) {
		Key key = KeyFactory.createKey(Cheat.class.getSimpleName(), id);

		EntityManager em = EMFService.get().createEntityManager();
		Cheat cheat = null;
		
		em.getTransaction().begin();
		try {
			Query q = em
					.createQuery("SELECT c FROM Cheat c WHERE c.id = :myKey");
			q.setParameter("myKey", key);
			cheat = (Cheat) q.getSingleResult();
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
		}
		return cheat;
	}
}