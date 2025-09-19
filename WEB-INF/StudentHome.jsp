	<%@ page import="java.util.List" %>
	<%@ page import="com.school.model.*" %>
<%@ include file="\WEB-INF\Style.jsp" %>
	
	<html>
	<head>
		<meta charset="UTF-8">
		<title>Student Home</title>
	</head>
	<%
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
	<br>
	<%@ include file="\WEB-INF\Profile.jsp" %>

	<%@ include file="\WEB-INF\RequestForm.jsp" %>




	<%@ include file="\WEB-INF\YourNotification.jsp" %>
	<%@ include file="\WEB-INF\YourRequest.jsp" %>
	</table>
	</html>