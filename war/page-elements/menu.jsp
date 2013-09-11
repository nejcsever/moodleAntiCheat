<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>

<% String pageName = this.getClass().getSimpleName().replaceAll("_jsp", ""); %>

<div class="navbar">
	<div class="navbar-inner">
		<ul class="nav">
			<li <%= pageName.equals("index") ? "class=\"active\"" : "" %>><a href="/">Domov</a></li>
			<li <%= pageName.equals("attemptFilter") || pageName.equals("attemptView") ? "class=\"active\"" : "" %>><a href="attemptFilter">Pregled goljufij</a></li>
			<li <%= pageName.equals("quizzes") ? "class=\"active\"" : "" %>><a href="quizzes.jsp">Ustvari kviz</a></li>
			<li <%= pageName.equals("classroomList") ? "class=\"active\"" : "" %>><a href="classroomList">Učilnice</a></li>
			<li <%= pageName.equals("liveFilter") ? "class=\"active\"" : "" %>><a href="liveFilter">Goljufije v živo</a></li>
			<li <%= pageName.equals("about") ? "class=\"active\"" : "" %>><a href="about.jsp">O sistemu</a></li>
		</ul>
	</div>
</div>