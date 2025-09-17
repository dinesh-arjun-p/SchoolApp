package com.school.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.school.dao.*;
import java.io.IOException;

@WebServlet("/requestAccess")
public class RequestAccessServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String rollNo =  (String) session.getAttribute("rollNo");
		DAO dao = new DAO();
		String error=dao.checkConstraint(req.getParameter("action"),req.getParameter("action_value"));
		if (error != null) {
			String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8.toString());
			resp.sendRedirect("Home.jsp?error=" + encodedError);
			return;
		}

        String action = req.getParameter("action");
		String action_value=req.getParameter("action_value");
		String action_for=req.getParameter("action_for");
		req.setAttribute("action",action);
		req.setAttribute("action_value",action_value);
		if(action_for==null||action_for.equals("")){
			action_for=(String)session.getAttribute("rollNo");
			
		}
		req.setAttribute("action_for",action_for);
		
       
		int rule=dao.getRule(session,req);
		Audit_LogsDAO al=new Audit_LogsDAO();
        if (dao.createRequest(rule,action,action_value,action_for, rollNo)) {
			al.recordCreateRequest(rollNo,action);
            resp.sendRedirect("Home.jsp?msg=Request Submitted");
        } else {
            resp.sendRedirect("Home.jsp?error=Failed to Submit");
        }
    }
}
