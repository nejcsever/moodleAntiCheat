<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.sever.diploma.entities.*"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.SimpleDateFormat"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Goljuf - Pregled poskusov</title>

<% List<Object[]> attempts = (List<Object[]>) request.getAttribute("attempts");%>
<% SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, hh:mm"); %>

<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/mainStyle.css">
<link rel="stylesheet" type="text/css"
	href="css/jquery-ui-1.9.2.custom.css">

<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.2.custom.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-timepicker-addon.js"></script>
<script type="text/javascript" src="js/timepicker-logic.js"></script>
<script type="text/javascript" src="js/attemptFilter.js"></script>


<script type='text/javascript' src='https://www.google.com/jsapi'></script>
<script type='text/javascript'>
      google.load('visualization', '1', {packages:['table']});
      google.setOnLoadCallback(drawTable);
      function drawTable() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', '');
        data.addColumn('string', 'Kviz');
		data.addColumn('string', 'Študent')
		data.addColumn('date', 'Čas začetka');
		data.addColumn('number', 'Goljufije');
		data.addColumn('string', '');
        data.addRows(<%= attempts.size() %>);
        
        <%
        int i = 0;
        for (Object[] obj : attempts) {
        	Attempt attempt = (Attempt) obj[0];
        %>
       		data.setCell(<%= i %>, 0, "", "<a href=\"attemptView?id=<%= attempt.getId().getId() %>&quizId=<%= attempt.getQuiz().getId().getId() %>\" target=\"_blank\"><i class=\"icon-eye-open\"></i></a>",  {style: 'font-size: x-small; width: 2%; padding: 5px;'});
        	data.setCell(<%= i %>, 1, "<%= attempt.getQuiz().getSubject() + (attempt.getQuiz().getSubject().equals("") ? "" : " - ") + attempt.getQuiz().getName() %>", "<%= attempt.getQuiz().getSubject() + (attempt.getQuiz().getSubject().equals("") ? "" : " - ") + attempt.getQuiz().getName() %>", {style: 'font-size: x-small; width: 50%; padding: 5px;'});
        	data.setCell(<%= i %>, 2, "<%= attempt.getStudent().getName() %>", "<%= attempt.getStudent().getName() %>", {style: 'font-size: x-small; width: 20%; font-weight: bold; padding: 5px;'});
        	data.setCell(<%= i %>, 3, new Date(<%= attempt.getStartTime().getTime() %>), new Date(<%= attempt.getStartTime().getTime() %>), {style: 'font-size: x-small; width: 25%; padding: 5px;'});
        	data.setCell(<%= i %>, 4, <%= attempt.getSevereCheatCount() %>, <%= attempt.getSevereCheatCount() %>, {style: 'text-align: center; font-weight:bold; width: 3%; padding: 5px;'});
        	data.setCell(<%= i %>, 5, "", "<a href=\"attemptFilter?action=removeAttempt&attemptId=<%= attempt.getId().getId() %>&quizId=<%= attempt.getQuiz().getId().getId() %>\"><i class=\"icon-trash\"></i></a>",  {style: 'font-size: x-small; width: 2%; padding: 5px;'});
        <%
        	i++;
        }
        %>

        var dateFormat = new google.visualization.DateFormat({pattern: "dd.MM.yyyy | HH:mm"});
        dateFormat.format(data, 3);
        var table = new google.visualization.Table(document.getElementById('attempt-table'));
        table.draw(data, {page: 'enable', pageSize: 7, sortColumn: 3,sortAscending: false, allowHtml: true, width: "100%"});
      }
</script>
</head>
<body>
	<div class="container">
		<%@ include file="page-elements/authentication.jsp"%>
		<%@ include file="page-elements/header.jsp"%>
		<%@ include file="page-elements/menu.jsp"%>
		
		<% List<Quiz> quizzes = (List<Quiz>) request.getAttribute("quizzes"); %>
		<% List<String> subjects = (List<String>) request.getAttribute("subjects"); %>
		<% List<String> quizNames = (List<String>) request.getAttribute("quizNames"); %>
		<% List<String> studentNames = (List<String>) request.getAttribute("studentNames"); %>
		<% String selectedSubject = (String) request.getAttribute("selectedSubject"); %>
		<% String selectedQuiz = (String) request.getAttribute("selectedQuiz"); %>
		<% String selectedStudent = (String) request.getAttribute("selectedStudent"); %>
		<% String dateFrom = (String) request.getAttribute("dateFrom"); %>
		<% String dateTo = (String) request.getAttribute("dateTo"); %>
		<div class="row-fluid white-container-no-padding">
			<div class="span4">
				<div class="filters">
					<h3>Omejitve prikaza</h3>
					<br />
					<form class="form-horizontal filter-form">
						<fieldset>	
						<!-- Select Basic -->
						<div class="control-group">
						  <label class="control-label" for="subject">Predmet</label>
						  <div class="controls">
						    <select id="subject" name="subject" class="input-medium">
						      <% for(String subject : subjects) { %>
						     	<option value="<%=subject%>" <%= (selectedSubject.equals(subject)) ? "selected" : "" %>><%=subject%></option>
						      <% } %>
						    </select>
						  </div>
						</div>
						<!-- Select Basic -->
						<div class="control-group">
						  <label class="control-label" for="quizName">Kviz</label>
						  <div class="controls">
						    <select id="quizName" name="quizName" class="input-medium">
						      <% for( String quizName : quizNames) { %>
						     	<option value="<%=quizName%>" <%= (selectedQuiz.equals(quizName)) ? "selected" : "" %>><%=quizName%></option>
						      <% } %>
						    </select>
						    <div class="loading" id="quiz-name-loading"></div>
						  </div>
						</div>
						<!-- Select Basic -->
						<div class="control-group">
						  <label class="control-label" for="studentName">Študent</label>
						  <div class="controls">
						    <select id="studentName" name="studentName" class="input-medium">
						      <% for(String name : studentNames) { %>
						     	<option value="<%=name%>" <%= (selectedStudent.equals(name)) ? "selected" : "" %>><%=name%></option>
						      <% } %>
						    </select>
						    <div class="loading" id="student-name-loading"></div>
						  </div>
						</div>
						<hr />
						<!-- Text input-->
						<div class="control-group">
						  <label class="control-label" for="dateFrom">Datum od</label>
						  <div class="controls">
						    <input id="dateFrom" name="dateFrom" type="text" class="input-medium" value="<%= dateFrom != null ? dateFrom : "" %>">
						    
						  </div>
						</div>
						
						<!-- Text input-->
						<div class="control-group">
						  <label class="control-label" for="dateTo">Datum do</label>
						  <div class="controls">
						    <input id="dateTo" name="dateTo" type="text" class="input-medium" value="<%= dateTo != null ? dateTo : "" %>">
						    
						  </div>
						</div>
						<hr />
						<div class="control-group">
						  <div class="controls">
						    <button class="btn"><i class="icon-refresh"></i> Filtriraj</button>
						  </div>
						</div>
						</fieldset>
					</form>
				</div>
			</div>
			<div class="span8">
				<div class="attempt-list">
					<h3>Seznam poskusov</h3>
					<div id="attempt-table"></div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>