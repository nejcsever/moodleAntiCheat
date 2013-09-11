package com.sever.diploma.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sever.diploma.dao.AttemptDao;
import com.sever.diploma.dao.QuizDao;
import com.sever.diploma.helpers.AuthenticationHelper;

public class AttemptFilterServlet extends HttpServlet {

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
		if (req.getParameter("action") != null && req.getParameter("action").equals("removeAttempt")) {
			Long attemptId = Long.parseLong(req.getParameter("attemptId"));
			Long quizId = Long.parseLong(req.getParameter("quizId"));
			AttemptDao.INSTANCE.remove(attemptId, quizId);
		}
		// handle subjects
		List<String> subjects = new ArrayList<String>();
		subjects.add("");
		subjects.addAll(QuizDao.INSTANCE.getAllSubjects());
		req.setAttribute("subjects", subjects);

		// handle quiz names for select element
		List<String> quizNames = new ArrayList<String>();
		quizNames.add("");
		String selectedSubject = "";
		if (req.getParameter("subject") != null
				&& !req.getParameter("subject").equals("")) {
			selectedSubject = req.getParameter("subject");
			quizNames.addAll(AttemptDao.INSTANCE
					.getQuizNamesBySubject(selectedSubject));
		}
		req.setAttribute("quizNames", quizNames);

		// handle student names
		List<String> studentNames = new ArrayList<String>();
		studentNames.add("");
		String selectedQuiz = "";
		if (req.getParameter("quizName") != null
				&& !req.getParameter("quizName").equals("")) {
			selectedQuiz = req.getParameter("quizName");
			studentNames.addAll(AttemptDao.INSTANCE
					.getStudentNamesByQuizName(selectedQuiz));
		}
		req.setAttribute("studentNames", studentNames);

		// information for selected items for select html element
		req.setAttribute("selectedSubject", selectedSubject);
		req.setAttribute("selectedQuiz", selectedQuiz);
		req.setAttribute(
				"selectedStudent",
				req.getParameter("studentName") != null ? req
						.getParameter("studentName") : "");
		req.setAttribute("dateFrom", req.getParameter("dateFrom"));
		req.setAttribute("dateTo", req.getParameter("dateTo"));

		// filtered attempts
		req.setAttribute("attempts", AttemptDao.INSTANCE.getAllByFilters(
				req.getParameter("dateFrom"), req.getParameter("dateTo"),
				selectedSubject, selectedQuiz, req.getParameter("studentName")));

		RequestDispatcher rd = getServletContext().getRequestDispatcher(
				"/attemptFilter.jsp");
		rd.forward(req, resp);
	}

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		// authenticate user
		if (!AuthenticationHelper.userLoggedIn()) {
			RequestDispatcher rd = getServletContext().getRequestDispatcher(
					"/login.jsp");
			rd.forward(req, resp);
			return;
		}
		resp.setCharacterEncoding("utf8");
		resp.setContentType("application/json");
		
		PrintWriter out = resp.getWriter();
		JSONObject obj = new JSONObject();
		if (req.getParameter("action") != null && req.getParameter("action").equals("getQuizzes")) {
			String subject = req.getParameter("subject") != null ? req.getParameter("subject") : "";
			List<String> names = AttemptDao.INSTANCE.getQuizNamesBySubject(subject);
			JSONArray namesArray = new JSONArray();
			namesArray.addAll(names);
			obj.put("resultData", namesArray);
			out.print(obj.toString());
		} else if (req.getParameter("action") != null && req.getParameter("action").equals("getStudents")) {
			String quizName = req.getParameter("quizName") != null ? req.getParameter("quizName") : "";
			List<String> names = AttemptDao.INSTANCE.getStudentNamesByQuizName(quizName);
			JSONArray namesArray = new JSONArray();
			namesArray.addAll(names);
			obj.put("resultData", namesArray);
			out.print(obj.toString());
		}
	}
}
