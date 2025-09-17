package com.school.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import com.school.dao.*;

@WebServlet("/CreateRuleServlet")
public class CreateRuleServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
		int rule_id=dao.createRule(statusLimit,priority);
        dao.createWorkFlow(rule_id,reviewers,executer);
		dao.createRuleCondition(rule_id,attributes,operators,values,logicOps);
          

            
            response.sendRedirect("Home.jsp?msg=Rule created successfully");

       
    }
}
