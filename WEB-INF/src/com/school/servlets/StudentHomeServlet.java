package com.school.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.school.model.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/StudentHomeServlet")
public class StudentHomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"Student".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect("login.jsp?error=Please login first");
            return;
        }


        // fetch all requests made by this student
        DAO dao = new DAO();
		UserInfo userInfo = dao.getUserByRollNo((String)session.getAttribute("rollNo"));
		if (userInfo != null) {
		        session.setAttribute("uname", userInfo.getName());
				session.setAttribute("phone_number",userInfo.getPhoneNumber());
				session.setAttribute("class",userInfo.getClassName());
		} 
        List<RequestAccess> requests = dao.getRequestedByStudent((String) session.getAttribute("rollNo"));
        List<Notification> notify = dao.getNotificationsForStudent((String) session.getAttribute("rollNo"));
		System.out.println(notify);
        // set data for JSP
        request.setAttribute("requests", requests);
        request.setAttribute("notifications", notify);
        // forward to JSP
        request.getRequestDispatcher("/WEB-INF/StudentHome.jsp").forward(request, response);
    }
}
