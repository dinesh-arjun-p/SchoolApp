<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.school.model.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Create User</title>
</head>
<body>
	<%
    session = request.getSession(false);
	    if (session == null || !session.getAttributeNames().hasMoreElements()) {
	        response.sendRedirect("login.jsp");
	        return;
	    }
	    String currentRole = (String) session.getAttribute("role"); 
	    if (!"admin".equalsIgnoreCase(currentRole)) {
	        response.sendRedirect("Home.jsp");
	        return;
	    }
	%>

<h2>Create New User</h2>
<form action="createUser" method="post">
    Name: <input type="text" name="name"><br>
    Email: <input type="email" name="email"><br>
    Password: <input type="password" name="pass"><br>
    Role:
    <select name="role_id">
        <option value="1">Admin</option>
        <option value="2">Teacher</option>
        <option value="3">Student</option>
    </select><br>
	Class:<input type="text" name="class"><br>
	Phone Number:<input type="text" name="phone_number"  maxlength="10"><br>
	 <% 
       List<UserInfo> superiors = (List<UserInfo>) session.getAttribute("superiors");
       if ( superiors != null) { 
    %>
        <label for="superior">Select Superior:</label>
        <select name="superior" id="superior">
			<option value="">-- Select Action --</option>
            <% for (UserInfo s : superiors) { %>
                <option value="<%=s.getRollNo()%>"><%=s.getRollNo()%></option>
            <% } %>
        </select>
        <br><br>
    <% } %>
    <input type="submit" value="Create">
</form>


	
</body>
</html>