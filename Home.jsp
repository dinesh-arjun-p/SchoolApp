<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Zoho School</title>
</head>
<body bgcolor="cyan">
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0); 

    session = request.getSession(false);
	if(session==null ||!session.getAttributeNames().hasMoreElements()){
		response.sendRedirect("login.jsp");
	}
    if ( session.getAttribute("role") == null) {
%>
        <h2>No role assigned. Please login again or contact administrator.</h2>
        <a href="login.jsp">Back to Login</a>
<%
        return;
    }

    String role = (String) session.getAttribute("role");
    String email = (String) session.getAttribute("email");
    if ("Admin".equalsIgnoreCase(role)) {
        request.getRequestDispatcher("/SuperAdminServlet").forward(request, response);
    } else if ("Teacher".equalsIgnoreCase(role)) {
        request.getRequestDispatcher("/TeacherHomeServlet").forward(request, response);
    } else if ("Student".equalsIgnoreCase(role)) {
        request.getRequestDispatcher("/StudentHomeServlet").forward(request, response);
    } else {
%>
        <h2>Unknown role: <%= role %><%= email %></h2>
		<%
		
		session.invalidate();%>
        <a href="login.jsp">Back to Login</a>
<%
    }
%>
</body>
</html>
