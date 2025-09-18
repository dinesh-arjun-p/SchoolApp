package com.school.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import com.school.dao.*;

@WebServlet("/CreateRuleServlet")
public class CreateRuleServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null ||
                !"admin".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect("Home.jsp?error=Access+Denied");
            return;
        }
		
		
        String priorityStr = request.getParameter("priority");
        String statusLimitStr = request.getParameter("status_limit");
        int priority = Integer.parseInt(priorityStr);
        int statusLimit = Integer.parseInt(statusLimitStr);

        String[] reviewers = request.getParameterValues("reviewers"); // array of rollNo
        String executer = request.getParameter("executer");

        // Get conditions
        String[] attributes = request.getParameterValues("attribute");
        String[] operators = request.getParameterValues("operator");
        String[] values = request.getParameterValues("value");
        String[] logicOps = request.getParameterValues("logic_op");
		DAO dao=new DAO();
		int ruleId=dao.createRule(statusLimit,priority);
        dao.createWorkFlow(ruleId,reviewers,executer);
		dao.createRuleCondition(ruleId,attributes,operators,values,logicOps);
          
		Audit_LogsDAO al=new Audit_LogsDAO();
		al.recordCreateRule(ruleId,(String)session.getAttribute("rollNo"));
            
            response.sendRedirect("Home.jsp?msg=Rule created successfully");

       
    }
}
