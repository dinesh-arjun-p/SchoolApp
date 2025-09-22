package com.school.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import com.school.utils.DBUtil;
import com.school.model.*;
import com.school.dao.*;

@WebServlet("/CreateRulePageServlet")
public class CreateRulePageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		DAO dao=new DAO();
        List<UserInfo> teachers = dao.getTeacher();
        
		List<String> attributes = dao.getAllAttributes();
		Map<String, List<String>> operators = dao.getAttributeOperators();
		Map<String, List<String>> values = dao.getAttributeValues();

        request.setAttribute("attributes", attributes);
		request.setAttribute("operators", operators);
		request.setAttribute("values", values);
		
		String editRuleIdStr = request.getParameter("editRule");
        if (editRuleIdStr != null && !editRuleIdStr.isEmpty()) {
            int editRuleId = Integer.parseInt(editRuleIdStr);
            Rule rule = dao.getRuleById(editRuleId); 
            request.setAttribute("editRule", rule);
        }
        
        request.setAttribute("teachers", teachers);

        // Forward to JSP inside WEB-INF
        request.getRequestDispatcher("createRule.jsp").forward(request, response);
    }
}
