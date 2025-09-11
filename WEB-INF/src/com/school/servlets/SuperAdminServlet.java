package com.school.servlets;

import com.school.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.*;
import java.io.IOException;

/**
 * Servlet implementation class SuperAdminServlet
 */
@WebServlet("/SuperAdminServlet")
public class SuperAdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"Admin".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }
        DAO dao = new DAO();
        List<UserInfo> users = dao.getAllUsers();

        request.setAttribute("users", users);
        request.getRequestDispatcher("/WEB-INF/SuperAdminHome.jsp").forward(request, response);
    }
}
