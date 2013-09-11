package com.sever.diploma.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.appengine.api.datastore.Text;
import com.sever.diploma.dao.AttemptDao;
import com.sever.diploma.dao.CheatDao;
import com.sever.diploma.dao.QuizDao;
import com.sever.diploma.dao.StudentDao;
import com.sever.diploma.entities.Attempt;
import com.sever.diploma.entities.Cheat;
import com.sever.diploma.entities.Quiz;
import com.sever.diploma.entities.Student;

public class AddCheatServlet extends HttpServlet {

	private static final long serialVersionUID = -4251057426120370964L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		JSONParser parser = new JSONParser();
		JSONObject data = null;
		try {
			data = (JSONObject) parser.parse(req.getParameter("data"));
		} catch (ParseException e) {
			resp.getWriter().println("Failed to parse JSON");
			return;
		}
		Student student = StudentDao.INSTANCE.uniqueAdd((String) data.get("studentName"), (long) data.get("studentMoodleId"));
		Quiz quiz = QuizDao.INSTANCE.uniqueAdd((String) data.get("quizName"), (String) data.get("quizSubject"), (String) data.get("quizUrl"));
		Attempt attempt = AttemptDao.INSTANCE.uniqueAdd(new Date((long) data.get("timestamp")), new Date((long) data.get("endTime")),
				quiz, (boolean) data.get("timeLimited"), student, (String) data.get("browserName"));
		Text additionalData = (String) data.get("additionalData") != null ? new Text((String) data.get("additionalData")) : null;
		Cheat c = new Cheat((String) data.get("ipv4"), (String) data.get("ipv6"), (String) data.get("cheatType"), new Date((long) data.get("timestamp")),
				additionalData);
		CheatDao.INSTANCE.add(c, attempt.getId().getId(), quiz.getId().getId());
	}
}
