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
    if (session != null && session.getAttribute("role") != null) {
        response.sendRedirect("Home.jsp");
        return;
    }
	%>
	

	<a href="https://trial-3599609.okta.com/oauth2/default/v1/authorize?client_id=0oavak3jfuTqZwEss697&response_type=code&scope=openid%20profile%20email%20groups&redirect_uri=http://localhost:8080/School/callback&state=12345">
    Login with Okta
</a>


	
</body>
</html>