<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Create User</title>
</head>
<body>
	<%
    session = request.getSession(false);
	    if (session == null || session.getAttribute("uname") == null) {
	        response.sendRedirect("login.jsp");
	        return;
	    }
	    String currentRole = (String) session.getAttribute("role"); 
	    if (!"admin".equalsIgnoreCase(currentRole)) {
	        response.sendRedirect("Main.jsp");
	        return;
	    }
	%>

<h2>Create New User</h2>
<form action="createUser" method="post">
    Username: <input type="text" name="uname"><br>
    Password: <input type="password" name="pass"><br>
    Role:
    <select name="role_id">
        <option value="1">Admin</option>
        <option value="2">Teacher</option>
        <option value="3">Student</option>
    </select><br>
    <input type="submit" value="Create">
</form>
	
</body>
</html>