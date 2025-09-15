package com.school.servlets;

import com.school.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/TeacherHomeServlet")
public class TeacherHomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null ||  !"Teacher".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String teacherName = (String) session.getAttribute("uname");
		String teacherRollNo=(String) session.getAttribute("rollNo");
        DAO dao = new DAO();
        List<RequestAccess> reviewRequests = dao.getReviewRequests(teacherRollNo); 
        
        List<RequestAccess> executeRequests = dao.getExecuteRequests(teacherRollNo); 
        session.setAttribute("teacherName", teacherName);
        session.setAttribute("reviewRequests", reviewRequests);
		session.setAttribute("executeRequests", executeRequests);

        request.getRequestDispatcher("/WEB-INF/TeacherHome.jsp").forward(request, response);
    }
}
