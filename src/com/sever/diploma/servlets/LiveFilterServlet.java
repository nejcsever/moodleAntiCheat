package com.sever.diploma.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sever.diploma.dao.ClassroomDao;
import com.sever.diploma.helpers.AuthenticationHelper;

public class LiveFilterServlet extends HttpServlet {

	private static final long serialVersionUID = 7601416352562779335L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		// authenticate user
		if (!AuthenticationHelper.userLoggedIn()) {
			RequestDispatcher rd = getServletContext().getRequestDispatcher(
					"/login.jsp");
			rd.forward(req, resp);
			return;
		}
		req.setAttribute("classrooms", ClassroomDao.INSTANCE.getClassrooms());
		RequestDispatcher rd = getServletContext().getRequestDispatcher(
				"/liveFilter.jsp");
		rd.forward(req, resp);
	}
}
