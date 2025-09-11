package com.school.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/createUser")
public class CreateUserServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("uname") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"admin".equalsIgnoreCase(role)) {
            response.sendRedirect("Home.jsp?error=Access+Denied"); 
            return;
        }

        String uname = request.getParameter("uname");
        String password = request.getParameter("pass");
        String roleIdStr = request.getParameter("role_id");

        try {
            int roleId = Integer.parseInt(roleIdStr);
            DAO dao = new DAO();

            boolean success = dao.createUser(uname, password, roleId);

            if (success) {
                response.sendRedirect("Home.jsp?msg=User+created+successfully");
            } else {
                response.sendRedirect("Home.jsp?error=Failed+to+create+user");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("Home.jsp?error=" + e.getMessage());
        }
    }
}
	