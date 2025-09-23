<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.school.model.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Create User</title>
<style>
    body {
        font-family: Arial, sans-serif;
        background-color: #f5f5f5;
        margin: 0;
        padding: 0;
    }
    .container {
        max-width: 500px;
        margin: 50px auto;
        padding: 25px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 6px rgba(0,0,0,0.1);
    }
    h2 {
        text-align: center;
        margin-bottom: 20px;
        color: #333;
    }
    form label {
        display: block;
        margin: 12px 0 5px;
        font-weight: bold;
        color: #555;
    }
    form input[type="text"],
    form input[type="email"],
    form input[type="password"],
    form select {
        width: 100%;
        padding: 8px 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }
    form input[type="submit"] {
        width: 100%;
        padding: 10px;
        margin-top: 20px;
        border: none;
        border-radius: 4px;
        background-color: #4CAF50;
        color: white;
        font-size: 16px;
        cursor: pointer;
    }
    form input[type="submit"]:hover {
        background-color: #45a049;
    }
</style>
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
<div class="container">
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

</div>
	
</body>
</html>