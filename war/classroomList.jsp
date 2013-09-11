<%@ page import="java.util.List"%>
<%@ page import="com.sever.diploma.entities.Classroom"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Goljuf - Učilnice</title>

<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/mainStyle.css">
<link rel="stylesheet" type="text/css" href="css/jquery-ui-1.9.2.custom.css">

<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.2.custom.min.js"></script>

<% List<Classroom> classroomList = (List<Classroom>) request.getAttribute("classroomList");%>
<script type='text/javascript' src='https://www.google.com/jsapi'></script>
<script type='text/javascript'>
      google.load('visualization', '1', {packages:['table']});
      google.setOnLoadCallback(drawTable);
      function drawTable() {
        var data = new google.visualization.DataTable();
		data.addColumn('string', '')
		data.addColumn('string', '');
		data.addColumn('string', 'Učilnica');
        data.addRows(<%= classroomList.size() %>);
        
        <%
        int i = 0;
        for (Classroom c : classroomList) {
        %>
        	data.setCell(<%= i %>, 0, '', '<a href="editClassroom?action=remove&id=<%= c.getId().getId() %>" class="remove-classroom"><i class="icon-trash"></i>Izbriši</a>', {style: 'width: 18%; padding: 5px;'});
        	data.setCell(<%= i %>, 1, '', '<a href="editClassroom?action=update&id=<%= c.getId().getId() %>"><i class="icon-edit"></i>Uredi</a>', {style: 'width: 18%; padding: 5px;'});
        	data.setCell(<%= i %>, 2, '<%= c.getName().toLowerCase() %>', "<%= c.getName() %>", {style: 'font-weight: bold; width: 64%; padding: 5px;'});
       		
        <%
        	i++;
        }
        %>

        var table = new google.visualization.Table(document.getElementById('classroom-table'));
        table.draw(data, {page: 'enable', pageSize: 10, sortColumn: 2, allowHtml: true, width: "100%"});
      }
      // alert message for classroom removal
   	  $(document).on('click', 'a.remove-classroom', function() {
   		  return confirm("Ali res želite izbrisati učilnico?");
   	  });
</script>

</head>
<body>
	<div class="container">
		<%@ include file="page-elements/authentication.jsp" %>
		<%@ include file="page-elements/header.jsp" %>
		<%@ include file="page-elements/menu.jsp" %>
		<div class="row-fluid">
			<div class="white-container span6 offset3">
				<h3>Seznam obstoječih učilnic</h3>
				<div id="classroom-table"></div>
				<br />
				<a href="createClassroom.jsp" class="btn btn-success"><i class="icon-plus-sign icon-white"></i> Dodaj novo učilnico</a>
			</div>
		</div>
		<%@ include file="page-elements/footer.jsp" %>
	</div>
</body>
</html>