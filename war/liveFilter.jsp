<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.sever.diploma.entities.*"%>
<%@ page import="java.util.List"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Goljuf - Goljufije v živo</title>
	
	<% List<Classroom> classrooms = (List<Classroom>) request.getAttribute("classrooms");%>
	
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
		<%@ include file="page-elements/header.jsp"%>
		<%@ include file="page-elements/menu.jsp"%>
		
		<div class="row-fluid">
			<div class="white-container span6 offset3">
				<h3 class="text-center">Nastavitve</h3>
				<hr />
				<form class="form-horizontal" method="get" action="liveStream" target="_blank">
					<input type="hidden" name="action" value="start" />
					<div class="control-group">
						<label for="subject" class="control-label">Učilnica: </label>
						<div class="controls">
							<select id="classroom" name="classroom" <%= classrooms.size() == 0 ? "disabled=\"disabled\"" : "" %>>
								<% if (classrooms.size() == 0) {%>
									<option>Ni učilnic</option>
								<% } %>
								<% for (Classroom c : classrooms) { %>
									<option value="<%= c.getId().getId() %>"><%= c.getName() %></option>
								<% } %>
							</select>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="radios">Način prikaza:</label>
						<div class="controls">
							<label class="radio" for="hangman">
								<input name="display-type" id="hangman" value="hangman" checked="checked" type="radio">
								Vislice
							</label>
							<small class="muted">6 stopenj</small>
						<label class="radio" for="statusbar">
							<input name="display-type" id="statusbar" value="statusbar" type="radio">
							Statusna vrstica
						</label>
						<small class="muted">Št. stopenj: </small><input name="cheatCount" id="cheatCount" type="text" class="input-cheats-count" value="4">
						</div>
					</div>
					<div class="control-group">
					  <div class="controls">
					    <label class="checkbox" for="otherUsers">
					      <input name="otherUsers" id="otherUsers" value="true" type="checkbox">
					      Prikaži goljufije izven učilnice
					    </label>
					  </div>
					</div>
					<div class="control-group">
						<div class="controls">
							<button type="submit" class="btn btn-success">
								<i class="icon-facetime-video icon-white"></i> Zaženi prenos v živo
							</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</body>
</html>