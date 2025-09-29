package com.school.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.school.dao.*;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final String OKTA_LOGOUT_URL =
        "https://trial-3599609.okta.com/oauth2/default/v1/logout";
    private static final String POST_LOGOUT_REDIRECT =
        "http://localhost:8080/School/login.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            String rollNo = (String) session.getAttribute("rollNo");
            if (rollNo != null) {
				Audit_LogsDAO al=new Audit_LogsDAO();
                al.recordLogout(rollNo);
            }

            String idToken = (String) session.getAttribute("id_token");

            session.invalidate();

            if (idToken != null) {
                String logoutUrl = OKTA_LOGOUT_URL
                        + "?id_token_hint=" + idToken
                        + "&post_logout_redirect_uri=" + POST_LOGOUT_REDIRECT;
                response.sendRedirect(logoutUrl);
                return;
            }
        }

        response.sendRedirect("login.jsp");
    }
}
