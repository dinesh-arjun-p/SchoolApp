package com.school.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/UpdateRequestStatus")
public class UpdateRequestStatusServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("uname") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String teacherRollNo = (String) session.getAttribute("rollNo");
        int requestId = Integer.parseInt(request.getParameter("requestId"));
        String action = request.getParameter("action"); 
        DAO dao = new DAO();
		String status=action;
		if(status.equals("Approved"))
			status="Reviewed";
		dao.createNotification(requestId,status,teacherRollNo);
        dao.updateRequestStatus(requestId, action,teacherRollNo);
		

        response.sendRedirect("TeacherHomeServlet?msg=Request " + action);
    }
}
