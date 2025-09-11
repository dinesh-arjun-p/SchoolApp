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
        String department = req.getParameter("department");
        String requestDate = req.getParameter("request_date"); 
        DAO dao = new DAO();
        if (dao.createRequest(department, rollNo, requestDate)) {
            resp.sendRedirect("Home.jsp?msg=Request Submitted");
        } else {
            resp.sendRedirect("Home.jsp?error=Failed to Submit");
        }
    }
}
