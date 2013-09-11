<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Dodajanje učilnice</title>

<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/mainStyle.css">
<link rel="stylesheet" type="text/css" href="css/jquery-ui-1.9.2.custom.css">

<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.2.custom.min.js"></script>
<script type="text/javascript" src="js/classroom.js"></script>

</head>
<body>
	<div class="container">
		<%@ include file="page-elements/authentication.jsp" %>
		<%@ include file="page-elements/header.jsp" %>
		<%@ include file="page-elements/menu.jsp" %>
		<div class="row-fluid">
			<div class="white-container span6 offset3">
				<h3 class="text-center">Dodajanje učilnice</h3>
				<form class="form-horizontal" id="grid-form">
					<hr />
					<div class="control-group">
						<label for="name" class="control-label">Ime učilnice: </label>
						<div class="controls">
							<input id="name" name="name" type="text" class="input-medium"></input>
						</div>
					</div>
					<div class="control-group">
						<label for="col" class="control-label">Število stolpcev: </label>
						<div class="controls">
							<input id="col" name="col" type="text" class="input-mini"></input>
						</div>
					</div>
					<div class="control-group">
						<label for="row" class="control-label">Število vrstic: </label>
						<div class="controls">
							<input id="row" name="row" type="text" class="input-mini"></input>
						</div>
					</div>
					<div class="row-fluid text-center">
						<button type="submit" class="btn" id="grid-button">
							<i class="icon-th-large"></i> Ustvari mrežo
						</button>
					</div>
				</form>
				<h4 id="classroom-name" class="text-center"></h4>
			</div>
		</div>
		<form class="text-center" id="add-classroom-form">
			<div id="classroom-loading"></div>
			<button class="btn btn-success btn-large margin-top"><i class="icon-plus-sign icon-white"></i> Dodaj spodnjo učilnico</button>
		</form>
		<div id="grid">
		</div>
		<%@ include file="page-elements/footer.jsp" %>
	</div>
	
	<div id="dialog" title="Dodaj sedež">
		<form id="dialog-form" class="form">
			<input type="hidden" name="col"></input>
			<input type="hidden" name="row"></input>
			<div class="control-group">
				<label for="ipv4" class="control-label">IPv4</label>
				<div class="controls">
					<input id="ipv4" name="ipv4" type="text" class="input-fluid" placeholder="npr.: 192.168.1.1"></input>
				</div>
			</div>
			<div class="control-group">
				<label for="ipv6" class="control-label">IPv6</label>
				<div class="controls">
					<input id="ipv6" name="ipv6" type="text" class="input-fluid" placeholder="npr.: 2001:db8::ff00:42:8329"></input>
				</div>
			</div>
			<div class="row-fluid text-center">
				<button type="submit" class="btn" id="btn-save-seat">
					Shrani
				</button>
			</div>
		</form>
	</div>
</body>
</html>