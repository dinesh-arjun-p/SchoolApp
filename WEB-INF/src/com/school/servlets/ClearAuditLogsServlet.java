package com.school.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.school.dao.*;
@WebServlet("/ClearAuditLogs")
public class ClearAuditLogsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Audit_LogsDAO al = new Audit_LogsDAO();
        al.clearAuditLogs();
        response.sendRedirect("Home.jsp");
    }
}
