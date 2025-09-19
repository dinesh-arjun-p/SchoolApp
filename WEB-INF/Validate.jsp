<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0); 

    session = request.getSession(false);
    if (session == null || session.getAttribute("rollNo") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
	%>