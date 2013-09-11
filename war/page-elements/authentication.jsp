<%@ page import="com.sever.diploma.dao.UserDao"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<%
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();

	String logoutUrl = null;
	if (user == null || !UserDao.INSTANCE.exists(user.getEmail())) {
		String redirectURL = request.getContextPath() + "/login.jsp";
		session.invalidate();
		response.sendRedirect(redirectURL);
		return;
	} else if (request.getParameter("hideLogout") == null){
		logoutUrl = userService
				.createLogoutURL(request.getRequestURI());
		%><small class="pull-right">Prijavljen uporabnik <strong><%= user.getEmail() %></strong> - <a href="<%= logoutUrl != null ? logoutUrl : "" %>">Odjava</a></small><%
	}
%>