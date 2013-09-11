package com.sever.diploma.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserServiceFactory;
import com.sever.diploma.dao.UserDao;
import com.sever.diploma.entities.User;
import com.sever.diploma.helpers.AuthenticationHelper;

public class UsersServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1775061909805866201L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		// authentication - if there is no existing users, then everybody can access this page
		List<User> users = UserDao.INSTANCE.getAll();
		boolean test= UserDao.INSTANCE.isAdmin(UserServiceFactory.getUserService().getCurrentUser().getEmail());
		if (users.size()  > 0 && AuthenticationHelper.userLoggedIn() && !test) {
			RequestDispatcher rd = getServletContext().getRequestDispatcher(
					"/login.jsp");
			rd.forward(req, resp);
			return;
		}
		
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		
		String email = req.getParameter("email");
		String role = req.getParameter("role");
		String action = req.getParameter("action");
		if (action != null && email != null) {
			if ( action.equals("add")) {
				UserDao.INSTANCE.add(email, role != null ? role : "user");
				resp.getWriter().println("Uspeöno dodan uporabnik: " + email + "<br/>Sprememba morda ne bo takoj vidna na seznamu uporabnikov.");
			} else if (action.equals("remove") || action.equals("delete")) {
				UserDao.INSTANCE.remove(email);
				resp.getWriter().println("Uspe≈°no izbrisan uporabnik: " + email + "<br/>Sprememba morda ne bo takoj vidna na seznamu uporabnikov.");
			}
		}
		
		resp.getWriter().println("<h3>Seznam obstojecih uporabnikov</h3>");
		resp.getWriter().println("<ul>");
		for (User u : users) {
			resp.getWriter().println("<li>Email: <strong>" + u.getEmail() + "</strong>	<br />Role: " + u.getRole() + "</li>");
		}
		resp.getWriter().println("</ul>");
		
		resp.getWriter().println("<hr /><h3>Moûne akcije</h3>");
		resp.getWriter().println("<strong>Dodajanje uporabnika:</strong> <code>/users?action=add&email=[uporabnikov-email]&role=[vloga]</code>");
		resp.getWriter().println("<br /><strong>Brisanje uporabnika:</strong> <code>/users?action=remove&email=[uporabnikov-email]</code>");
	}
}
