package com.school.servlets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.school.model.Notification;
import com.school.model.RequestAccess;
import com.school.utils.*;
import java.util.*;

public class DAO {
	
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
	
	
	public UserInfo getUserInfo(String uname, String password) {
	    UserInfo userInfo = null;

	    String sql = "SELECT p.roll_no, p.name, p.pass, r.role_name " +
	                 "FROM person p JOIN role r ON p.role_id = r.role_id " +
	                 "WHERE p.name=? AND p.pass=?";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, uname);
	        st.setString(2, password);
	        ResultSet rs = st.executeQuery();

	        if (rs.next()) {
	            userInfo = new UserInfo();
	            userInfo.setRollNo(rs.getString("roll_no"));
	            userInfo.setName(rs.getString("name"));
	            userInfo.setPassword(rs.getString("pass"));
	            userInfo.setRole(rs.getString("role_name"));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return userInfo;
	}


	
	public boolean createUser(String uname, String password, int roleId) {
	    String sql = "INSERT INTO person (roll_no, name, pass, role_id) VALUES (?, ?, ?, ?)";
	    try (Connection con = (Connection) DBUtil.getConnection()) {
	        
	        // Generate roll_no first
	        String rollNo = generateRollNo(roleId, con);

	        try (PreparedStatement st = con.prepareStatement(sql)) {
	            st.setString(1, rollNo);
	            st.setString(2, uname);
	            st.setString(3, password);
	            st.setInt(4, roleId);

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

	    String sql = "SELECT p.roll_no, p.name, p.pass, r.role_name " +
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
	                user.setPassword(rs.getString("pass"));
	                user.setRole(rs.getString("role_name"));

	               
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return user;
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
