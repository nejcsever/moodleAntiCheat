<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Sistem za preprečevanje goljufij</title>

<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="css/mainStyle.css">

<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>

</head>
<body>
	<div class="container">
		<%@ include file="page-elements/authentication.jsp" %>
		<%@ include file="page-elements/header.jsp" %>
		<%@ include file="page-elements/menu.jsp" %>
		<div class="jumbotron white-container">
			<h1>Dobrodošli!</h1>
			<p class="lead">Sistem <strong>goljuf</strong> je sistem proti goljufanju na spletnih kvizih sistema moogle. Onemogočenih je več vrst goljufij, kot so kopiranje teksta, izgubljanje fukusa brskalnika in še več.</p>
			<hr />
			<div class="row">
				<div class="span4">
					<h2>Goljufije v živo</h2>
					<p>Sistem podpira spremljanje goljufij v živo. Po ustvarjenem sedežnem redu, lahko izberemo eno izmed učilnic v kateri želimo prestrezati goljufije.
					Prikaz goljufij je lahko v obliki vislic ali statusne vrstice.</p>
					<br />
					<div class="hangman hangman-6"></div>
				</div>
				<div class="span4">
					<h2>Sedežni red</h2>
					<p>Ustvarimo lahko sedežni red učilnic, v katerih lahko kasneje spremljamo dogajanje v živo.
					Z enostavnim uporabniškim vmesnikom lahko z nekaj kliki postavimo sedeže v poljubno veliko mrežo.</p>
					<br />
					<div class="seat-image"></div>
				</div>
				<div class="span4">
					<h2>Analiza goljufov</h2>
					<p>Pregledna časovnica goljufij omogoča, da za vsak poskus študenta pogledamo kdaj je goljufal in na kakšen način.
					Na voljo so tudi filtri, s katerimi lažje najdemo poskuse, ki jih želimo analizirati.</p>
					<br />
					<div class="eye-image"></div>
				</div>
			</div>
		</div>
		<%@ include file="page-elements/footer.jsp" %>
	</div>
</body>
</html>