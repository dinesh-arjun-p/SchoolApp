package com.school.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet("/createUser")
public class CreateUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null ||
                !"admin".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect("Home.jsp?error=Access+Denied");
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("pass");
        int roleId = Integer.parseInt(request.getParameter("role_id"));

        try {
			DAO dao = new DAO();
			Audit_LogsDAO al=new Audit_LogsDAO();
			String userId = dao.createOktaUser(name, email, password, roleId);

			// Save to DB
			
			boolean success = dao.createUser(name, password, roleId, email, userId);

			if (success) {
				al.recordCreateUser(session.getAttribute("rollNo").toString(),dao.getUserInfo(email).getRollNo() ,roleId);
				response.sendRedirect("Home.jsp?msg=User+created+successfully");
			} else {
				dao.deleteOktaUser(userId);
				response.sendRedirect("Home.jsp?error=DB+Insert+failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("Home.jsp?error=" + e.getMessage());
		}

    }
}
