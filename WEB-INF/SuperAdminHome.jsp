<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
<%@ page import="java.util.*" %>
<%@ page import="com.school.model.*" %> 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Zoho School</title>
<style>
	table,th,td{
		border: 1px solid black;
	}
</style>
</head>
<body bgcolor="cyan">
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0); 

    session = request.getSession(false);
    if (session == null || session.getAttribute("rollNo") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String role = (String) session.getAttribute("role");
    String uname = (String) session.getAttribute("uname");
	String email = (String) session.getAttribute("email");
	String rollNo = (String) session.getAttribute("rollNo");
    
    String msg = request.getParameter("msg");
    String error = request.getParameter("error");
    if (msg != null) {
	%>
        <p style="color:green;"><%= msg %></p>
	<%
    }
    if (error != null) {
	%>
        <p style="color:red;"><%= error %></p>
	<%
    }

%>

<form action="logout">
    <input type="submit" value="logout">
</form>

Welcome to Zoho School Mr ${uname} ${rollNo} ${role} ${email}
<br>


<% if ("Admin".equalsIgnoreCase(role) ) { %>
    <form action="createUser.jsp" method="post" style="display:inline;">
        <button type="submit">➕ Create New User</button>
    </form>
<% } %>

<% if ("Admin".equalsIgnoreCase(role) ) { %>
    <form action="CreateRulePageServlet" method="post" style="display:inline;">
        <button type="submit">➕ Create Rule</button>
    </form>
<% } %>

<br><br>

<h2>All Users</h2>

<table style="border: 1px solid black;">
    <tr>
        <th>Roll No</th>
        <th>Name</th>
        <th>Role</th>
        <th>Action</th>
    </tr>
    <%
        List<UserInfo> users = (List<UserInfo>) request.getAttribute("users");
        if (users != null) {
            for (UserInfo u : users) {
    %>
    <tr>
        <td><%= u.getRollNo() %></td>
        <td><%= u.getName() %></td>
        <td><%= u.getRole() %></td>
        <td>
            <form action="deleteUser" method="post">
                <input type="hidden" name="rollNo" value="<%= u.getRollNo() %>">
                <button type="submit">Delete</button>
            </form>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>
<p>All requests.</p>
<%

        List<RequestAccess> exes = (List<RequestAccess>) request.getAttribute("requests");

        if (exes == null || exes.isEmpty()) {
    %>
        <p>No  requests.</p>
    <%
        } else {
    %>
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th>Request ID</th>
                <th>Date</th>
                <th>Department</th>
                
                
				<th>Action Value</th>
				<th>Action For</th>
				<th>Assigned To</th>
				<th>Requested By</th>
				<th>Action</th>
            </tr>
            <%
                for (RequestAccess exe : exes) {
            %>
                <tr>
                    <td><%= exe.getRequestId() %></td>
                    <td><%= exe.getRequestDate() %></td>
                    <td><%= exe.getAction() %></td>
					<td><%= exe.getActionValue() %></td>
					<td><%= exe.getActionFor() %></td>
					 <td><% for(String req:exe.getAssignedTo()) out.print(req+" "); %></td>
                    <td><%= exe.getRequestedBy() %></td>
					
					
                    <td>
                        <form action="UpdateRequestStatus" method="post" style="display:inline;">
                            <input type="hidden" name="requestId" value="<%= exe.getRequestId() %>">
                            <button type="submit" name="action" value="Executed">Execute</button>
                            <button type="submit" name="action" value="Rejected">Reject</button>
                        </form>
                    </td>
                </tr>
            <%
                }
		}
            %>
        </table>

<h2>All Audit Logs</h2>
<form action="ClearAuditLogs" method="post">
                <button type="submit">clear</button>
            </form>
<table >
    <tr>
        <th>Id</th>
        <th>UserName</th>
        <th>Event</th>
        <th>Reg</th>
		<th>Date</th>
		<th>Time</th>
    </tr>
    <%
        List<Logs> logs = (List<Logs>) request.getAttribute("logs");
        if (logs != null) {
            for (Logs log : logs) {
    %>
    <tr>
        <td><%= log.getId() %></td>
        <td><%= log.getUserName() %></td>
        <td><%= log.getEvent() %></td>
        <td><%= log.getReg() %></td>
		<td><%= log.getDate() %></td>
		<td><%= log.getTime() %></td>
    </tr>
    <%
            }
        }
    %>
</table>


</body>
</html>
