package com.school.servlets;

import com.school.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import com.school.dao.*;

@WebServlet("/deleteRule")
public class DeleteRuleServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");

        if (!"Admin".equalsIgnoreCase(role)) {
            response.sendRedirect("Home.jsp?error=Access+Denied");
            return;
        }

        int ruleId = Integer.parseInt(request.getParameter("ruleId"));
		DAO dao = new DAO();
		if(!dao.canDelete(ruleId)){
			response.sendRedirect("Home.jsp?error=Rule+cannot+be+Deleted+as+Request+Id+are+open");
			return ;
		}
		String rollNo=(String)session.getAttribute("rollNo");
        
		Audit_LogsDAO al=new Audit_LogsDAO();
        boolean deleted = dao.deleteRule(ruleId);
		UserInfo user=dao.getUserByRollNo(rollNo);
		if (deleted) {
			

				al.recordDeleteRule(ruleId,rollNo);
				response.sendRedirect("Home.jsp?msg=Rule+deleted+successfully");
			
		} else {
			response.sendRedirect("Home.jsp?error=Failed+to+delete+Rule");
		}

    }
}
