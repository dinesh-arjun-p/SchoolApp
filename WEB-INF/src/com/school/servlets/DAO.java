package com.school.servlets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;


import com.school.model.*;
import com.school.utils.*;
import java.util.*;

public class DAO {
	String oktaDomain = "https://trial-3599609.okta.com";
	String apiToken = "00ORTu-qzJWpZOQKmWZxcpJYW-b4yXlVGHkxwBpOAC";

	String ADMIN_GROUP_ID = "00gvak756w9bKkWYG697";
	String TEACHER_GROUP_ID = "00gvak9np7jMPgp2x697";
	String STUDENT_GROUP_ID = "00gvakamixXcbpU0D697";
	
	
	public boolean verifyUser(String uname, String password) {
	    String sql = "SELECT * FROM person WHERE name=? AND pass=?";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, uname);
	        st.setString(2, password);
	        System.out.println("Hello"); // Debug log

	        try (ResultSet rs = st.executeQuery()) {
	            return rs.next(); // true if at least one row
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}


	
	
	public void recordLogin(String rollNo) {
	    String sql = "INSERT INTO login_history (username, login_time) VALUES (?, NOW())";
	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, rollNo);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public void recordLogout(String rollNo) {
	    String sql = "UPDATE login_history SET logout_time = NOW() " +
	                 "WHERE username=? ORDER BY id DESC LIMIT 1";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, rollNo);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	public UserInfo getUserInfo(String email) {
	    UserInfo userInfo = null;

	    String sql = "SELECT p.roll_no, p.name, p.pass, r.role_name " +
	                 "FROM person p JOIN role r ON p.role_id = r.role_id " +
	                 "WHERE p.email=? ";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, email);
	        ResultSet rs = st.executeQuery();

	        if (rs.next()) {
	            userInfo = new UserInfo();
	            userInfo.setRollNo(rs.getString("roll_no"));
	            userInfo.setName(rs.getString("name"));
	            userInfo.setPass(rs.getString("pass"));
	            userInfo.setRole(rs.getString("role_name"));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return userInfo;
	}


	public String createOktaUser(String name, String email, String password, int roleId) throws Exception {
		
		String jsonBody = "{"
				+ "\"profile\": {"
				+ "\"email\": \"" + email + "\","
				+ "\"login\": \"" + email + "\","
				+ "\"name\": \"" + name + "\""
				+ "},"
				+ "\"credentials\": {"
				+ "\"password\": { \"value\": \"" + password + "\" }"
				+ "}"
				+ "}";

		URI userUri = new URI(oktaDomain + "/api/v1/users?activate=true");
		HttpURLConnection conn = (HttpURLConnection) userUri.toURL().openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "SSWS " + apiToken);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);

		try (OutputStream os = conn.getOutputStream()) {
			os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
		}

		if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
			String errorResponse;
			try (InputStream es = conn.getErrorStream()) {
				errorResponse = new String(es.readAllBytes(), StandardCharsets.UTF_8);
			}
			throw new RuntimeException("Okta user creation failed: " + errorResponse);
		}

		
		String jsonResponse;
		try (InputStream is = conn.getInputStream()) {
			jsonResponse = new String(is.readAllBytes(), StandardCharsets.UTF_8);
		}
		JSONObject obj = new JSONObject(jsonResponse);
		String userId = obj.getString("id");

		String groupId = switch (roleId) {
			case 1 -> ADMIN_GROUP_ID;
			case 2 -> TEACHER_GROUP_ID;
			case 3 -> STUDENT_GROUP_ID;
			default -> null;
		};

		if (groupId != null) {
			URI groupUri = new URI(oktaDomain + "/api/v1/groups/" + groupId + "/users/" + userId);
			HttpURLConnection groupConn = (HttpURLConnection) groupUri.toURL().openConnection();
			groupConn.setRequestMethod("PUT");
			groupConn.setRequestProperty("Authorization", "SSWS " + apiToken);
			int groupResp = groupConn.getResponseCode();
			if (groupResp != 204) {
				deleteOktaUser(userId);
				System.out.println("Warning: failed to add user to group " + groupId + ", response=" + groupResp);
			}
		}

		return userId;
	}

	
	public boolean createUser(String name, String password, int roleId, String email,String userId) {
		String sql = "INSERT INTO person (roll_no, name, pass, role_id, email,userid) VALUES (?, ?, ?, ?, ?,?)";
    try (Connection con = DBUtil.getConnection()) {

        // Generate roll_no first
        String rollNo = generateRollNo(roleId, con);

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, rollNo);
            st.setString(2, name);
            st.setString(3, password);
            st.setInt(4, roleId);
            st.setString(5, email);
			st.setString(6,userId);

            int rows = st.executeUpdate();
            return rows > 0;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
		return false;
	}


	
	
	private String generateRollNo(int roleId, Connection con) throws Exception {
	    String prefix = "";
	    if (roleId == 1) prefix = "zohoAdmin";
	    else if (roleId == 2) prefix = "zohoTeacher";
	    else if (roleId == 3) prefix = "zohoStudent";

	    String sql = "SELECT roll_no FROM person WHERE role_id=? AND roll_no LIKE ? " +
	                 "ORDER BY CAST(SUBSTRING(roll_no, LENGTH(?) + 1) AS UNSIGNED) DESC LIMIT 1";

	    try (PreparedStatement st = con.prepareStatement(sql)) {
	        st.setInt(1, roleId);
	        st.setString(2, prefix + "%");
	        st.setString(3, prefix);
	        ResultSet rs = st.executeQuery();

	        if (rs.next()) {
	            String lastRoll = rs.getString("roll_no");
	            int num = Integer.parseInt(lastRoll.substring(prefix.length()));
	            return prefix + (num + 1);
	        } else {
	            return prefix + "1";
	        }
	    }
	}

	
	public List<UserInfo> getAllUsers() {
	    List<UserInfo> users = new ArrayList<>();
	    String sql = "SELECT p.roll_no, p.name, r.role_name " +
	                 "FROM person p JOIN role r ON p.role_id = r.role_id";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql);
	         ResultSet rs = st.executeQuery()) {

	        while (rs.next()) {
	            UserInfo u = new UserInfo();
	            u.setRollNo(rs.getString("roll_no"));
	            u.setName(rs.getString("name"));
	            u.setRole(rs.getString("role_name"));
	            users.add(u);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return users;
	}
	
	public UserInfo getUserByRollNo(String rollNo) {
	    UserInfo user = null;

	    String sql = "SELECT p.roll_no,p.userid, p.name, p.pass, p.email,r.role_name " +
	                 "FROM person p " +
	                 "JOIN role r ON p.role_id = r.role_id " +
	                 "WHERE p.roll_no = ?";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, rollNo);

	        try (ResultSet rs = st.executeQuery()) {
	            if (rs.next()) {
	                user = new UserInfo();
	                user.setRollNo(rs.getString("roll_no"));
	                user.setName(rs.getString("name"));
	                user.setPass(rs.getString("pass"));
	                user.setRole(rs.getString("role_name"));
					user.setUserId(rs.getString("userid"));
					user.setEmail(rs.getString("email"));

	               
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return user;
	}
	
	public String getOktaUserId(String rollNo){
		String sql="Select userId from person where roll_no=?";
		try(Connection con=DBUtil.getConnection();
		PreparedStatement st=con.prepareStatement(sql)){
			st.setString(1,rollNo);
			ResultSet rs=st.executeQuery();
			if(rs.next()){
				return rs.getString("userid");
			}
		}
		catch(Exception e ){
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean deleteUser(String rollNo) {
	    String sql = "DELETE FROM person WHERE roll_no=?";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {
	        st.setString(1, rollNo);
	        return st.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public void insertRollBack(UserInfo user){
		String sql="insert into person (roll_no,name,userid,pass,email,role_id)values(?,?,?,?,?,?)";
		try (Connection con = DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {
	        st.setString(1, user.getRollNo());
			st.setString(2,user.getName());
			st.setString(3,user.getUserId());
			st.setString(4,user.getPass());
			st.setString(5,user.getEmail());
			st.setInt(6,user.getRoleId());
			
	        st.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public boolean deleteOktaUser(String userId) {

		try {
			// 1. Deactivate user
			URL deactivateUrl = URI.create(oktaDomain + "/api/v1/users/" + userId + "/lifecycle/deactivate").toURL();
			HttpURLConnection deactivateConn = (HttpURLConnection) deactivateUrl.openConnection();
			deactivateConn.setRequestMethod("POST");
			deactivateConn.setRequestProperty("Authorization", "SSWS " + apiToken);
			deactivateConn.setRequestProperty("Accept", "application/json");
			deactivateConn.setDoOutput(true);

			int deactivateResponse = deactivateConn.getResponseCode();
			System.out.println("Deactivate Response: " + deactivateResponse);

			if (deactivateResponse != 200 && deactivateResponse != 204) {
				try (InputStream es = deactivateConn.getErrorStream()) {
					if (es != null) {
						System.err.println("Deactivate Error: " + new String(es.readAllBytes(), StandardCharsets.UTF_8));
					}
				}
				return false;
			}

			// 2. Delete user
			URL deleteUrl = URI.create(oktaDomain + "/api/v1/users/" + userId).toURL();
			HttpURLConnection deleteConn = (HttpURLConnection) deleteUrl.openConnection();
			deleteConn.setRequestMethod("DELETE");
			deleteConn.setRequestProperty("Authorization", "SSWS " + apiToken);
			deleteConn.setRequestProperty("Accept", "application/json");

			int deleteResponse = deleteConn.getResponseCode();
			System.out.println("Delete Response: " + deleteResponse);

			if (deleteResponse != 200 && deleteResponse != 204) {
				try (InputStream es = deleteConn.getErrorStream()) {
					if (es != null) {
						System.err.println("Delete Error: " + new String(es.readAllBytes(), StandardCharsets.UTF_8));
					}
				}
				return false;
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}




	public boolean createRequest(String department, String rollNo, String date) {
	    String sql = "INSERT INTO request_access (request_date, department, requested_by) VALUES (?, ?, ?)";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {
	    	st.setString(1,date);
	        st.setString(2, department);
	        st.setString(3, rollNo);
	        return st.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public List<RequestAccess> getRequestedByStudent(String rollNo) {
        List<RequestAccess> requests = new ArrayList<>();
        String sql = "SELECT * FROM request_access WHERE requested_by = ? order by request_id desc" ;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
        	System.out.println(rollNo);
            ps.setString(1, rollNo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RequestAccess req = new RequestAccess();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setRequestDate(rs.getDate("request_date"));
                    req.setDepartment(rs.getString("department"));
                    req.setRequestedBy(rs.getString("requested_by"));
                    req.setStatus(rs.getString("status"));
                    req.setReviewedBy(rs.getString("reviewed_by"));
                    requests.add(req);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }
	
	public List<RequestAccess> getPendingRequests() {
	    List<RequestAccess> requests = new ArrayList<>();
	    String sql = "SELECT * FROM request_access WHERE status = 'Pending'";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            RequestAccess req = new RequestAccess();
	            req.setRequestId(rs.getInt("request_id"));
	            req.setRequestDate(rs.getDate("request_date"));
	            req.setDepartment(rs.getString("department"));
	            req.setRequestedBy(rs.getString("requested_by"));
	            req.setStatus(rs.getString("status"));
	            req.setReviewedBy(rs.getString("reviewed_by"));

	            requests.add(req);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return requests;
	}
	
	
	
	public boolean updateRequestStatus(int requestId, String status, String reviewedBy) {
	    String updateSql = "UPDATE request_access SET status=?, reviewed_by=? WHERE request_id=?";
	    String insertSql = "INSERT INTO notification (student_roll_no, department, reviewed_by, status, request_date) " +
	                       "SELECT requested_by, department, ?, ?, request_date " +
	                       "FROM request_access WHERE request_id=?";

	    try (Connection con = DBUtil.getConnection()) {
	        // turn off auto-commit for transaction safety
	        con.setAutoCommit(false);

	        // 1. Update request_access
	        try (PreparedStatement ps = con.prepareStatement(updateSql)) {
	            ps.setString(1, status);      // Approved / Rejected
	            ps.setString(2, reviewedBy);  // teacher/admin roll no
	            ps.setInt(3, requestId);
	            ps.executeUpdate();
	        }

	        // 2. Insert into notification (also fetches request_date)
	        try (PreparedStatement ps = con.prepareStatement(insertSql)) {
	            ps.setString(1, reviewedBy);  // reviewed_by
	            ps.setString(2, status);      // Approved / Rejected
	            ps.setInt(3, requestId);
	            ps.executeUpdate();
	        }

	        con.commit();  // commit both queries together
	        return true;

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}


	public List<Notification> getNotificationsForStudent(String rollNo) {
	    List<Notification> list = new ArrayList<>();
	    String sql = "SELECT * FROM notification WHERE student_roll_no=?";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, rollNo);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Notification n = new Notification();
	                n.setNotificationId(rs.getInt("notification_id"));
	                n.setStudentRollNo(rs.getString("student_roll_no"));
	                n.setDepartment(rs.getString("department"));
	                n.setReviewedBy(rs.getString("reviewed_by"));
	                n.setStatus(rs.getString("status"));
	                n.setRequest_date(rs.getString("request_date"));
	                list.add(n);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	
	public boolean deleteNotification(int notificationId) {
	    String sql = "DELETE FROM notification WHERE notification_id=?";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setInt(1, notificationId);
	        return ps.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public List<RequestAccess> getAllViewedRequests() {
	    List<RequestAccess> requests = new ArrayList<>();
	    String sql = "SELECT * FROM request_access where status ='Approved' or status='Rejected' ORDER BY request_id DESC";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            RequestAccess req = new RequestAccess();
	            req.setRequestId(rs.getInt("request_id"));
	            req.setRequestDate(rs.getDate("request_date"));
	            req.setDepartment(rs.getString("department"));
	            req.setRequestedBy(rs.getString("requested_by"));
	            req.setStatus(rs.getString("status"));
	            req.setReviewedBy(rs.getString("reviewed_by"));

	            requests.add(req);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return requests;
	}


}
