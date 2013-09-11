<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.sever.diploma.dao.UserDao"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Goljuf - login</title>

<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/mainStyle.css">

<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>

</head>
<body>

	<%
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		String loginUrl = userService.createLoginURL(request
				.getRequestURI());
		if (user != null && UserDao.INSTANCE.exists(user.getEmail())) {
			String redirectURL = request.getContextPath() + "/index.jsp";
			session.invalidate();
			response.sendRedirect(redirectURL);
			return;
		}
	%>
	<div class="container">
		<%@ include file="page-elements/header.jsp"%>
		<div class="white-container">
			<h4>
				Potrebna je <a href="<%=loginUrl%>">prijava</a> v sistem.
			</h4>
			<p class="muted">Če imate težave s prijavo v sistem, se obrnite na administratorja.</p>
		</div>
		<%@ include file="page-elements/footer.jsp"%>
	</div>
</body>
</html>