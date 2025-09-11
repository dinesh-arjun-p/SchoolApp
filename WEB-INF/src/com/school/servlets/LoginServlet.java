package com.school.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uname=request.getParameter("uname");
		String pass=request.getParameter("pass");
		DAO ld=new DAO();
		
		if(ld.verifyUser(uname, pass)) {
		    UserInfo userInfo = ld.getUserInfo(uname, pass);
		    if (userInfo != null) {
		        HttpSession session = request.getSession();
		        session.setAttribute("rollNo", userInfo.getRollNo());
		        session.setAttribute("uname", uname);
		        session.setAttribute("role", userInfo.getRole());
		        ld.recordLogin( userInfo.getRollNo());

		        response.sendRedirect("Home.jsp");
		    } 
		}else {
		        response.sendRedirect("login.jsp?error=Invalid+credentials");
		 }

	}
}
