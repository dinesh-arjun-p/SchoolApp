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

<% UserInfo editUser=(UserInfo)request.getAttribute("editUser"); %>
<h2><%= (editUser == null ? "Create New User" : "Edit User") %></h2>

<form action="<%= (editUser == null ? "createUser" : "updateUser") %>" method="post">
    Name: <input type="text" name="name" value="<%= (editUser != null ? editUser.getName() : "") %>"><br>
    Email: <input type="email" name="email" value="<%= (editUser != null ? editUser.getEmail() : "") %>"><br>
	<% if (editUser==null){%>
    Password: <input type="password" name="pass"><br>
	<% } %>
    Role:
    <select name="role_id">
		
        <option value="1" <%= (editUser != null && "Admin".equalsIgnoreCase(editUser.getRole()) ? "selected" : "") %>>Admin</option>
        <option value="2" <%= (editUser != null && "Teacher".equalsIgnoreCase(editUser.getRole()) ? "selected" : "") %>>Teacher</option>
        <option value="3" <%= (editUser != null && "Student".equalsIgnoreCase(editUser.getRole()) ? "selected" : "") %>>Student</option>
    </select><br>
    Class: <input type="text" name="class" value="<%= (editUser != null ? editUser.getClassNo() : "") %>"><br>
    Phone Number: <input type="text" name="phone_number" maxlength="10" value="<%= (editUser != null ? editUser.getPhoneNumber() : "") %>"><br>

    <% 
       List<UserInfo> superiors = (List<UserInfo>) session.getAttribute("superiors");
       if (superiors != null) { 
    %>
        <label for="superior">Select Superior:</label>
        <select name="superior" id="superior">
            <option value="">-- Select Action --</option>
            <% for (UserInfo s : superiors) { %>
                <option value="<%=s.getRollNo()%>" <%= (editUser != null && s.getRollNo().equals(editUser.getSuperior()) ? "selected" : "") %>>
                    <%=s.getRollNo()%>
                </option>
            <% } %>
        </select>
        <br><br>
    <% } %>

    <% if (editUser != null) { %>
        <input type="hidden" name="rollNo" value="<%= editUser.getRollNo() %>">
        <input type="submit" value="Update">
    <% } else { %>
        <input type="submit" value="Create">
    <% } %>
</form>


	
</body>
</html>