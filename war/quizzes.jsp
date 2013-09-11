<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Generiranje kviza</title>

<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/mainStyle.css">
<link href='http://fonts.googleapis.com/css?family=Inconsolata' rel='stylesheet' type='text/css'>

<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/quiz.js"></script>

</head>
<body>
	<div class="container">
		<%@ include file="page-elements/authentication.jsp" %>
		<%@ include file="page-elements/header.jsp" %>
		<%@ include file="page-elements/menu.jsp" %>
		<div class="row-fluid">
			<div class="white-container span6 offset3">
				<h3 class="text-center">Ustvari nov kviz</h3>
				<hr />
				<form class="form-horizontal" id="quiz-form">
					<div class="control-group">
						<label for="subject" class="control-label">Kratica predmeta:</label>
						<div class="controls">
							<input id="subject" name="subject" type="text" class="input-medium" placeholder="npr.: OPB"></input>
						</div>
					</div>
					<div class="control-group">
						<label for="name" class="control-label">Ime kviza:</label>
						<div class="controls">
							<input id="name" name="name" type="text" class="input-medium" placeholder="npr.: 1. pisni izpit"></input>
						</div>
					</div>
					<div class="control-group">
						<label for="url" class="control-label">Povezava do kviza:</label>
						<div class="controls">
							<input id="url" name="url" type="text" class="input-medium" placeholder="npr.: http://ucilnica.fri.uni-lj.si/mod/quiz/view.php?id=12298"></input>
						</div>
					</div>
					<div class="row-fluid text-center">
						<button type="submit" class="btn" id="grid-button">
							<i class="icon-plus-sign"></i> Ustvari kviz
						</button>
					</div>
				</form>
				<div class="code-container">
					<h5>Generirana javascript koda:</h5>
					<textarea id="code-area">
					</textarea>
				</div>
			</div>
		</div>
		<%@ include file="page-elements/footer.jsp" %>
	</div>
</body>
</html>