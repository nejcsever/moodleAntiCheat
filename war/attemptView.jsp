<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.sever.diploma.entities.*"%>
<%@ page import="com.sever.diploma.dao.CheatDao"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.TimeZone"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.google.appengine.api.datastore.Text"%>
<%@ page import="java.lang.NullPointerException" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Goljuf - pregled poskusa</title>

<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/mainStyle.css">
<link rel="stylesheet" type="text/css"
	href="css/jquery-ui-1.9.2.custom.css">

<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.2.custom.min.js"></script>

</head>
<body>
	<div class="container">
		<%@ include file="page-elements/authentication.jsp"%>
		<h1>Pregled poskusa</h1>
		<hr class="dark-hr" />
		<div class="text-center">
			<div class="attempt-info-container">
				<%
					Attempt attempt = (Attempt) request.getAttribute("attempt");
					long gmtOffset = TimeZone.getTimeZone("GMT+2").getRawOffset();
					/* Parsing dates */
					SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
					String startTime = timeFormat.format(attempt.getStartTime().getTime() + gmtOffset);
					String endTime = timeFormat.format(attempt.getEndTime().getTime() + gmtOffset);
					SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.yyyy");
					String startDate = dateFormat.format(attempt.getStartTime().getTime());
					String endDate = dateFormat.format(attempt.getEndTime().getTime());
					
					// if infinite attempt
					if (!attempt.isTimeLimited()) {
						endTime = "";
						endDate = "Brez časovne omejitve";
					}
					
					String quizUrl = attempt.getQuiz().getUrl();
					boolean hasUrl = true;
					if (quizUrl == null || quizUrl.equals(""))
						hasUrl = false;
				%>
				<% if (hasUrl) { %>
				<a href="<%= quizUrl %>" target="_blank">
				<% } %>
				
				<h3><%=attempt.getQuiz().getSubject() + (attempt.getQuiz().getSubject().equals("") ? "" : " - ")
					+ attempt.getQuiz().getName()%></h3>
					
				<% if (hasUrl) { %>
				</a>
				<% } %>
				<hr />
				<i class="icon-user"></i>
				<h4><%=attempt.getStudent().getName()%></h4>
				<div class="attempt-time">
					<span class="time"><%=startTime%></span><span
						class="attempt-time-details"><i>ZAČETEK</i><span class="date"><%=startDate%></span></span>
				</div>
				<div class="attempt-time">
					<span class="time"><%=endTime%></span><span
						class="attempt-time-details"><i>KONEC</i><span class="date"><%=endDate%></span></span>
				</div>
				<hr />
				<div class="attempt-details">
					<div class="browser-details">
						<p>BRSKALNIK</p>
						<div class="browser-logo <%= attempt.getBrowser() %>"></div>
					</div>
					<div class="cheat-count-details">
						<p>ŠTEVILO GOLJUFIJ</p>
						<span class="cheat-count"><%= attempt.getSevereCheatCount() %></span>
					</div>
				</div>
			</div>
			<%
				List<Cheat> cheats = (List<Cheat>) request.getAttribute("cheats");
				timeFormat = new SimpleDateFormat("HH:mm:ss");
			%>
			<div class="attempt-timeline">
				<div class="cheat-container">
					<div class="time"><i class="icon-time"></i> <%= timeFormat.format(cheats.get(0).getTimestamp().getTime() + gmtOffset) %></div>
					<div class="cheat-info">
						<h4>Začetek reševanja</h4>
					</div>
					<p class="cheat-ip"><%= (cheats.get(0).getIpv6() == null) ? cheats.get(0).getIpv4() : cheats.get(0).getIpv6() %></p>
				</div>
				<%
				int i = 0;
				for (Cheat c : cheats) {
				%>
					<%
					if (c.getType().equals("ready") && i == 0) {
						i++;
						continue;
					}
					%>
					<div class="cheat-container">
						<div class="time"><i class="icon-time"></i> <%= timeFormat.format(c.getTimestamp().getTime() + gmtOffset) %></div>
						<div class="cheat-info">
							<h4><%= CheatDao.getDescription(c.getType()) %></h4>
							<%= (c.getType().equals("CTRL+C")) ? "<div class=\"key-ctrl\"></div> <div class=\"key-c\"></div>" : "" %>
							<%= (c.getType().equals("CTRL+V")) ? "<div class=\"key-ctrl\"></div> <div class=\"key-v\"></div>" : "" %>
							<%= (c.getType().equals("CTRL+A")) ? "<div class=\"key-ctrl\"></div> <div class=\"key-a\"></div>" : "" %>
							<%= (c.getType().equals("CTRL+X")) ? "<div class=\"key-ctrl\"></div> <div class=\"key-x\"></div>" : "" %>
							<%= (c.getType().equals("CTRL+S")) ? "<div class=\"key-ctrl\"></div> <div class=\"key-s\"></div>" : "" %>
							<%= (c.getType().equals("CTRL+U")) ? "<div class=\"key-ctrl\"></div> <div class=\"key-u\"></div>" : "" %>
							<%= (c.getType().equals("prntScrn")) ? "<div class=\"key-prntScrn\"></div>" : "" %>
							<%= (c.getType().equals("rightClick")) ? " <div class=\"key-rightClick\"></div>" : "" %>
							<% if (c.getAdditionalData() != null && !c.getAdditionalData().equals("")){ %>
								<textarea rows="4"><%= c.getAdditionalData().getValue() %></textarea>
							<% i++; } %>
						</div>
						<p class="cheat-ip"><%= (c.getIpv6() == null) ? c.getIpv4() : c.getIpv6() %></p>
					</div>
				<%
				}
				%>
			</div>
		</div>
	</div>
</body>
</html>