package com.school.servlets;

import com.school.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.school.dao.*;
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
		int pageSize=100,pageNumber=1;
		String pageParam = request.getParameter("page");
		if (pageParam != null&&!pageParam.isEmpty()) {
			pageNumber = Integer.parseInt(pageParam);
		}
		request.setAttribute("pageNumber",pageNumber);
		request.setAttribute("totalNoOfPages",dao.getTotalNoOfPages(pageSize));
		List<Logs> logs=dao.getLogs(pageNumber,pageSize);
		List<RequestAccess> requests=dao.getAllRequest();
		List<UserInfo> superiors=dao.getSuperior();
		List<Rule> rules=dao.getRules();
		request.setAttribute("logs",logs);
        request.setAttribute("users", users);
		request.setAttribute("rules",rules);
		request.setAttribute("requests",requests);
		session.setAttribute("superiors",superiors);
        request.getRequestDispatcher("/WEB-INF/SuperAdminHome.jsp").forward(request, response);
    }
}
