package com.school.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import com.school.dao.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final String CLIENT_ID =
        "0oavak3jfuTqZwEss697";
    private static final String REDIRECT_URI =
        "http://localhost:8080/School/callback"; 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		String state = java.util.UUID.randomUUID().toString();
        HttpSession session = request.getSession();
		session.setAttribute("state", state);

		String authorizationUrl = "https://trial-3599609.okta.com/oauth2/default/v1/authorize"
		+ "?client_id=" + URLEncoder.encode(CLIENT_ID, "UTF-8")
		+ "&response_type=code"
		+ "&scope=openid profile email"
		+ "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8")
		+ "&state=" + URLEncoder.encode(state, "UTF-8");
	
        response.sendRedirect(authorizationUrl);
		}
	}
