package com.school.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.school.dao.*;
import com.school.model.*;

@WebServlet("/updateUser")
public class EditUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null ||
                !"admin".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect("Home.jsp?error=Access+Denied");
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
		String className=request.getParameter("class");
		int classNo=0;
		try{
			classNo=Integer.parseInt(className);
		}
		catch(Exception e){}
		String superior=request.getParameter("superior");
		String phoneNumber=request.getParameter("phone_number");
        int roleId = Integer.parseInt(request.getParameter("role_id"));
		DAO dao=new DAO();
		Audit_LogsDAO al=new Audit_LogsDAO();
        UserInfo user=dao.getUserByRollNo(request.getParameter("rollNo"));
		if(!user.getEmail().equals(email)){
			dao.updateEmailInOkta(email,user);
		}
		if(user.getRoleId()!=roleId){
			dao.updateGroupInOkta(dao.findRoleName(roleId),user.getRole(),user);
		}
		user.setName(name);
		user.setEmail(email);
		user.setRoleId(roleId);
		user.setClassNo(classNo);
		user.setSuperior(superior);
		user.setPhoneNumber(phoneNumber);
		try{
			boolean success=dao.updateUser(user);
			if (success) {
				al.recordUpdateUser(session.getAttribute("rollNo").toString(),dao.getUserInfo(email).getRollNo() );
				al.recordUpdateUser(session.getAttribute("rollNo").toString(),dao.getUserInfo(email).getRollNo() );
				response.sendRedirect("Home.jsp?msg=User+Updated+successfully");
			} else {
				response.sendRedirect("Home.jsp?error=DB+Update+failed");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("Home.jsp?error=" + e.getMessage());
		}
		
    }
}
