<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
</head>
<body>
	<%
	session = request.getSession(false);
    if (session != null && session.getAttribute("uname") != null) {
        response.sendRedirect("Home.jsp");
        return;
    }
	%>
	<form action="login" method="post">
		Enter Username<input type="text" name="uname"><br>
		
		Enter Password<input type="password" name="pass"><br>
		<input type="submit" value="login">
	</form>
	<% 
    String error = request.getParameter("error");
    if (error != null) {
	%>
    <p style="color:red;"><%= error %></p>
	<% 
    }
	%>
	
</body>
</html>