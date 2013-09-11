package com.sever.diploma.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.sever.diploma.dao.ClassroomDao;
import com.sever.diploma.entities.Classroom;
import com.sever.diploma.entities.Seat;
import com.sever.diploma.helpers.AuthenticationHelper;

public class EditClassroomServlet extends HttpServlet {

	private static final long serialVersionUID = 7558342266864199590L;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		// authenticate user
		if (!AuthenticationHelper.userLoggedIn()) {
			RequestDispatcher rd = getServletContext().getRequestDispatcher(
					"/login.jsp");
			rd.forward(req, resp);
			return;
		}
		
		String action = req.getParameter("action");
		long id = 0;
		try {
			id = Long.parseLong(req.getParameter("id"));
		} catch (NumberFormatException e) {return;}
		
		if (action == null) {
			return;
		} else if (action.equals("remove")) {
			ClassroomDao.INSTANCE.remove(id);
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/classroomList");
		    rd.forward(req, resp);
		    return;
		} else if (action.equals("update")) {
			Classroom c = ClassroomDao.INSTANCE.getById(id);
			req.setAttribute("classroom", c);
			RequestDispatcher rd = getServletContext().getRequestDispatcher(
					"/editClassroom.jsp");
			rd.forward(req, resp);
		}
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

		JSONObject classroomData = (JSONObject) JSONValue.parse(req.getParameter("classroomData"));

		String name = (String) classroomData.get("name");
		long rowCount = (long) classroomData.get("rowCount");
		long columnCount = (long) classroomData.get("colCount");
		JSONArray seatsArray = (JSONArray) classroomData.get("seats");

		ArrayList<Seat> seats = new ArrayList<Seat>();
		Iterator<JSONObject> iterator = seatsArray.iterator();
		while (iterator.hasNext()) {
			JSONObject seat = iterator.next();
			long row = (long) seat.get("row");
			long column = (long) seat.get("col");
			String ipv4 = (String) seat.get("ipv4");
			String ipv6 = (String) seat.get("ipv6");
			Seat s = new Seat(row, column, ipv4, ipv6);
			seats.add(s);
		}
		// first delete classroom, then add new.
		ClassroomDao.INSTANCE.remove(Long.parseLong(req.getParameter("id")));
		ClassroomDao.INSTANCE.add(name, rowCount, columnCount, seats);
		
		resp.setCharacterEncoding("utf8");
		resp.setContentType("application/json");
		resp.getWriter().print("done");
	}
}
