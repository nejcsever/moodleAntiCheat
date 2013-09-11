package com.sever.diploma.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sever.diploma.dao.AttemptDao;
import com.sever.diploma.entities.Attempt;
import com.sever.diploma.entities.Cheat;
import com.sever.diploma.helpers.AuthenticationHelper;

public class AttemptViewServlet extends HttpServlet {

	private static final long serialVersionUID = 7775737133121533837L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		// authenticate user
		if (!AuthenticationHelper.userLoggedIn()) {
			RequestDispatcher rd = getServletContext().getRequestDispatcher(
					"/login.jsp");
			rd.forward(req, resp);
			return;
		}
		Attempt attempt = null;
		try {
			long id = Long.parseLong(req.getParameter("id"));
			long quizId = Long.parseLong(req.getParameter("quizId"));
			attempt = AttemptDao.INSTANCE.getById(id, quizId);
			if (attempt == null)
				throw new NullPointerException();
		} catch (Exception e) {
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/attemptFilter");
		    rd.forward(req, resp);
		    return;
		}
		req.setAttribute("attempt", attempt);
		req.setAttribute("quiz", attempt.getQuiz());
		
		// sort cheats by timestamp
		List<Cheat> cheats = attempt.getCheats();
		Collections.sort(attempt.getCheats(), new Comparator<Cheat>() {
			@Override
			public int compare(Cheat c1, Cheat c2) {
				return c1.compareTo(c2);
			}
		});
		req.setAttribute("cheats", cheats);
		
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/attemptView.jsp");
	    rd.forward(req, resp);
	}
}
