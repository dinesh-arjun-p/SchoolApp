package com.school.servlets;
import com.school.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@WebServlet("/callback")
public class CallBackServlet extends HttpServlet {

    private static final String CLIENT_ID = "0oavak3jfuTqZwEss697";  
    private static final String CLIENT_SECRET = "T9y-1Xa7OMf2qwwkB7q26nOkJfbN3BVeSdVCTsmAu3JGbJuj2Rm7o1a60v9UgCLx";  
    private static final String REDIRECT_URI = "http://localhost:8080/School/callback";
    private static final String TOKEN_URL = "https://trial-3599609.okta.com/oauth2/default/v1/token";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String code = request.getParameter("code");
        if (code == null || code.isEmpty()) {
            response.sendRedirect("login.jsp?error=Missing+authorization+code");
            return;
        }

        try {
            // 1. Exchange code for tokens
            URI tokenUri = URI.create(TOKEN_URL);
            URL url = tokenUri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String authString = CLIENT_ID + ":" + CLIENT_SECRET;
            String basicAuth = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

            conn.setRequestProperty("Authorization", basicAuth);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String body = "grant_type=authorization_code&code=" + code + "&redirect_uri=" + REDIRECT_URI;
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            JSONObject tokenResponse = new JSONObject(sb.toString());

            // Get ID Token and decode
            String idToken = tokenResponse.getString("id_token");
            String[] parts = idToken.split("\\.");
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JSONObject payload = new JSONObject(payloadJson);

            // Extract claims
            String username = payload.optString("preferred_username", payload.optString("sub"));
            JSONArray groups = payload.optJSONArray("groups");

            String role = "Guest";
            if (groups != null) {
                for (int i = 0; i < groups.length(); i++) {
                    String g = groups.getString(i);
                    if ("Admin".equalsIgnoreCase(g)) {
                        role = "Admin";
                        break;
                    } else if ("Teacher".equalsIgnoreCase(g)) {
                        role = "Teacher";
                        break;
                    } else if ("Student".equalsIgnoreCase(g)) {
                        role = "Student";
                        break;
                    }
                }
            }

            // 2. Save to session
            HttpSession session = request.getSession(true);
			session.setAttribute("id_token", idToken);
            session.setAttribute("email", username);
            session.setAttribute("role", role);
			DAO ld=new DAO();
			UserInfo userInfo = ld.getUserInfo(username);
		    if (userInfo != null) {
		        session.setAttribute("rollNo", userInfo.getRollNo());
		        session.setAttribute("uname", userInfo.getName());
		        ld.recordLogin( userInfo.getRollNo());
		    } 
            response.sendRedirect("Home.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=Okta+login+failed");
        }
    }
}
