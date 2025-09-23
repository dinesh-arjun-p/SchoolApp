<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
<%@ page import="java.util.*" %>
<%@ page import="com.school.model.*" %>
<%@ include file="\WEB-INF\Style.jsp" %>
 
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
<body >
<%@ include file="\WEB-INF\Validate.jsp" %>
	<%
    String role = (String) session.getAttribute("role");
    String uname = (String) session.getAttribute("uname");
	String email = (String) session.getAttribute("email");
	String rollNo = (String) session.getAttribute("rollNo");
    
	%>
<%@ include file="\WEB-INF\Message.jsp" %>

<%@ include file="\WEB-INF\Logout.jsp" %>

<%@ include file="\WEB-INF\Profile.jsp" %>




<br>
<%@ include file="\WEB-INF\AllUsers.jsp" %>


<%@ include file="\WEB-INF\AllRules.jsp" %>

<%@ include file="\WEB-INF\AllRequests.jsp" %>
<%@ include file="\WEB-INF\AllAuditLogs.jsp" %>



</body>
</html>
