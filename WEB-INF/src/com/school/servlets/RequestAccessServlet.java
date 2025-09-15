package com.school.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/requestAccess")
public class RequestAccessServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String rollNo =  (String) session.getAttribute("rollNo");
        String action = req.getParameter("action");
		session.setAttribute("action",action);
		session.setAttribute("action_value",0);
		
        DAO dao = new DAO();
		int rule=dao.getRule();
		Audit_LogsDAO al=new Audit_LogsDAO();
        if (dao.createRequest(rule,action, rollNo)) {
			al.recordCreateRequest(rollNo,action);
            resp.sendRedirect("Home.jsp?msg=Request Submitted");
        } else {
            resp.sendRedirect("Home.jsp?error=Failed to Submit");
        }
    }
}
