package com.school.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.school.dao.*;
import com.school.model.*;

@WebServlet("/EditRule")
public class EditRuleServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

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

        String[] reviewers = request.getParameterValues("reviewers"); 
        String executer = request.getParameter("executer");


        String[] attributes = request.getParameterValues("attribute");
        String[] operators = request.getParameterValues("operator");
        String[] values = request.getParameterValues("value");
        String[] logicOps = request.getParameterValues("logic_op");
		int ruleId=Integer.parseInt(request.getParameter("ruleId"));
		DAO dao=new DAO();
		Audit_LogsDAO al=new Audit_LogsDAO();
		
        Rule rule=dao.getRuleById(ruleId);	
		dao.inactiveRule(ruleId);
		rule.setPriority(priority);
		rule.setStatusLimit(statusLimit);
		boolean success=dao.updateRuleCondition(ruleId,attributes,operators,values,logicOps);
		success=success&&dao.updateRuleAndRequestExecuter(ruleId,executer);
		success=success&&dao.updateReviewers(ruleId,reviewers);
		try{
			success=success&&dao.updateRule(rule);
			if (success) {
				al.recordUpdateRule(session.getAttribute("rollNo").toString(),ruleId);
				response.sendRedirect("Home.jsp?msg=Rule+Updated+successfully");
			} else {
				response.sendRedirect("Home.jsp?error=Rule+Update+failed");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("Home.jsp?error=" + e.getMessage());
		}
		
    }
}
