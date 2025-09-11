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

    if (session == null ||session.getAttribute("uname")==null) {
        response.sendRedirect("login.jsp");
    }
%>

<form action="logout">
    <input type="submit" value="logout">
</form>

Welcome to Zoho School Mr ${uname}
</body>
</html>
