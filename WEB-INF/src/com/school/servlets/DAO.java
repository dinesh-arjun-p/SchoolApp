package com.school.servlets;

import java.sql.*;
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
	
	
	public String findRoleName(int roleId){
		switch(roleId){
			case 1:
				return "Admin";
			case 2:
				return "Teacher";
			case 3:
				return "Student";
			default:
				return "Guests";
		}
	}
	
	public int findRoleId(String roleName){
		switch(roleName){
			case "Admin":
				return 1;
			case "Teacher":
				return 2;
			case "Student":
				return 3;
			default:
				return -1;
		}
	}
	
	
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
	
	
	public UserInfo getUserInfo(String email) {
	    UserInfo userInfo = null;

	    String sql = "SELECT * FROM person p JOIN role r ON p.role_id = r.role_id " +
	                 "WHERE p.email=? ";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, email);
	        ResultSet rs = st.executeQuery();

	        if (rs.next()) {
	            userInfo = new UserInfo();
				userInfo=userInfo.setUserInfo(rs);
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
	    String sql = "SELECT * FROM person p JOIN role r ON p.role_id = r.role_id";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql);
	         ResultSet rs = st.executeQuery()) {

	        while (rs.next()) {
	            UserInfo u = new UserInfo();
	            users.add(u.setUserInfo(rs));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return users;
	}
	
	public UserInfo getUserByRollNo(String rollNo) {
	    UserInfo user = null;

	      String sql = "SELECT * FROM person p JOIN role r ON p.role_id = r.role_id " +
	                 "WHERE p.roll_no=? ";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, rollNo);
	        try (ResultSet rs = st.executeQuery()) {
	            if (rs.next()) {
	                user = new UserInfo();
					user=user.setUserInfo(rs);
	               
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

	public boolean isRuleTrue(ResultSet rs){
		return false;
	}
	public int getRule(){
		String sql="Select * from rule where priority>-1 order by priority";
		try(Connection con=DBUtil.getConnection();
		PreparedStatement st=con.prepareStatement(sql)){
			ResultSet rs=st.executeQuery();
			while(rs.next()){
				if(isRuleTrue(rs))
					return rs.getInt("rule_id");
			}
		}
		catch(Exception e){
			return 1;
		}
		return 1;
	}
	
	
	public int changeRoleInRequest(int requestId) throws SQLException{
		String sql = "UPDATE request_access ra JOIN rule r ON ra.rule_id = r.rule_id " +
             "JOIN rule_work_flow rwf ON rwf.rule_id = ra.rule_id " +
             "SET ra.role = 'Executer' " +
             "WHERE ra.request_id = ? " +
             "AND ra.status >= r.status_limit";

		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, requestId);
			return ps.executeUpdate(); 
		}
		catch(Exception e){
			return 0;
		}

	}
	
	public int assignToReviewers(int requestId)throws SQLException{
		String insertSql = "INSERT INTO request_reviewer (request_id, reviewer_roll_no,role) " +
                       "SELECT ra.request_id, rwf.incharge,rwf.role " +
                       "FROM request_access ra " +
                       "JOIN rule_work_flow rwf ON ra.rule_id = rwf.rule_id " +
                       "WHERE ra.request_id = ? ";

		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(insertSql)) {
			ps.setInt(1, requestId);
			return ps.executeUpdate();   
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public String checkConstraint(String action,String action_value){
		if(action.equals("changePhoneNumber")&&action_value.length()!=10){
			return "Phone Number is not valid";
		}
		return null;
	}
	
	public boolean createRequest(int rule, String action, String action_value,String action_for, String rollNo) {
			String sql = "INSERT INTO request_access (action, requested_by, rule_id, action_value,action_for) VALUES (?, ?, ?, ?,?)";

			try (Connection con = DBUtil.getConnection();
				PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

				st.setString(1, action);
				st.setString(2, rollNo);
				st.setInt(3, rule);
				st.setString(4, action_value); 
				st.setString(5,action_for);

				int affectedRows = st.executeUpdate();

				if (affectedRows > 0) {
					try (ResultSet rs = st.getGeneratedKeys()) {
					if (rs.next()) {
						assignToReviewers(rs.getInt(1));
						return true;
					}
				}
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false; // return -1 if failed
	}

	
	public List<String> getAssignedToFunc(int requestId, String role) {
		List<String> res = new ArrayList<>();
		String sql = "SELECT rr.reviewer_roll_no " +
					"FROM request_reviewer rr " +
					"WHERE rr.request_id = ? " +
					"AND rr.role = ? " +
					"AND rr.decision = 'Pending'";
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, requestId);
			ps.setString(2, role);
	
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					res.add(rs.getString("reviewer_roll_no"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	
	public List<RequestAccess> getAllRequest(){
		List<RequestAccess> requests = new ArrayList<>();
        String sql = "SELECT * FROM request_access " ;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RequestAccess req = new RequestAccess();
                    requests.add(req.setRequestAccess(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
	}
	
	public List<RequestAccess> getRequestedByStudent(String rollNo) {
        List<RequestAccess> requests = new ArrayList<>();
        String sql = "SELECT * FROM request_access WHERE requested_by = ? order by request_id desc" ;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rollNo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RequestAccess req = new RequestAccess();
					changeRoleInRequest(rs.getInt("request_id"));
                    requests.add(req.setRequestAccess(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }
	
	public List<RequestAccess> getReviewRequests(String teacherRollNo) {
	    List<RequestAccess> requests = new ArrayList<>();
	    String sql = "SELECT * FROM request_reviewer rr join request_access ra on rr.request_id=ra.request_id and rr.role=ra.role "
		+"WHERE rr.role = 'Reviewer' and rr.reviewer_roll_no=?";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);){
			 ps.setString(1,teacherRollNo);
	         ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            RequestAccess req = new RequestAccess();
	            requests.add(req.setRequestAccess(rs));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return requests;
	}
	
	public List<RequestAccess> getExecuteRequests(String teacherRollNo) {
	    List<RequestAccess> requests = new ArrayList<>();
	   String sql = "SELECT * FROM request_reviewer rr join request_access ra on rr.request_id=ra.request_id and rr.role=ra.role "
		+"WHERE rr.role = 'Executer' and rr.reviewer_roll_no=?";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);){
			 ps.setString(1,teacherRollNo);
	         ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            RequestAccess req = new RequestAccess();
	            requests.add(req.setRequestAccess(rs));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return requests;
	}
	
	
	
	public void setRole(Connection con,int requestId)throws SQLException{
		String sql = "Update request_access ra join rule r on ra.rule_id=r.rule_id set ra.role='Executer' "+
		"where request_id=? and ra.status>=r.status_limit" ;

	    try (PreparedStatement ps = con.prepareStatement(sql);){
			 ps.setInt(1,requestId);
	         ps.executeUpdate();
			

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void setStatus(Connection con,int requestId)throws SQLException{
		
		
		String sql ="UPDATE request_access ra " +
             "SET ra.status = (SELECT COUNT(*) FROM request_reviewer rr " +
             "                 WHERE rr.request_id = ra.request_id AND rr.decision = 'Approved') " +
             "WHERE ra.request_id = ?";


	    try (PreparedStatement ps = con.prepareStatement(sql);){
			 ps.setInt(1,requestId);
	         ps.executeUpdate();
			

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void setStatusAndRole(Connection con,int requestId) throws SQLException{
		
		setStatus(con,requestId);
		setRole(con,requestId);
	}
	
	
	
	public boolean updateRequestStatus(int requestId, String decision,String teacherRollNo)  {
		Audit_LogsDAO al=new Audit_LogsDAO();
		al.recordRequestStatus(teacherRollNo,decision,requestId);
		if(decision.equals("Approved")){
			String updateSql= "UPDATE request_reviewer ra set decision='Approved' where "
			+"ra.request_ID=? and ra.reviewer_roll_no=? and ra.role='Reviewer'";


			try (Connection con = DBUtil.getConnection()) {
				// turn off auto-commit for transaction safety
				con.setAutoCommit(false);

				try (PreparedStatement ps = con.prepareStatement(updateSql)) {
					ps.setInt(1, requestId);
					ps.setString(2,teacherRollNo);
					ps.executeUpdate();
					setStatusAndRole(con,requestId);
				}
				con.commit();  // commit both queries together
				return true;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		else if(decision.equals("Rejected")){
			String deleteSQL = "delete from request_access WHERE request_id=?";

			try (Connection con = DBUtil.getConnection()) {
				// turn off auto-commit for transaction safety
				con.setAutoCommit(false);

				try (PreparedStatement ps = con.prepareStatement(deleteSQL)) {
					ps.setInt(1, requestId);
					ps.executeUpdate();
				}
				con.commit();  // commit both queries together
				return true;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		else if(decision.equals("Executed")){
			executeAction(requestId);
			String deleteSQL = "delete from request_access WHERE request_id=?";

			try (Connection con = DBUtil.getConnection()) {
				// turn off auto-commit for transaction safety
				con.setAutoCommit(false);

				try (PreparedStatement ps = con.prepareStatement(deleteSQL)) {
					ps.setInt(1, requestId);
					ps.executeUpdate();
				}
				con.commit();  // commit both queries together
				return true;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		return false;
	}
	
	public void updateTable(Connection con,ResultSet rs,String field) throws SQLException{
		String sql="Update person set "+field+"=? where roll_no=?";
		try(PreparedStatement ps=con.prepareStatement(sql)){
			ps.setString(1,rs.getString("action_value"));
			ps.setString(2,rs.getString("action_for"));
			ps.executeUpdate();
		}
	}
	
	
	public void executeAction(int requestId){
		String sql="Select * from request_access where request_id=?";
		try(Connection con=DBUtil.getConnection()){
			con.setAutoCommit(false);
			try(PreparedStatement ps=con.prepareStatement(sql)){
				ps.setInt(1,requestId);
				ResultSet rs=ps.executeQuery();
				if(rs.next()){
					RequestAccess req=new RequestAccess();
					req=req.setRequestAccess(rs);
					if(rs.getString("action").equals("changePhoneNumber")){
						updateTable(con,rs,"phone_number");
					}
					else if(rs.getString("action").equals("changeName"))
						updateTable(con,rs,"name");
					else if(rs.getString("action").equals("changeClass"))
						updateTable(con,rs,"class");
				}
			}
			con.commit(); 
		}
		catch(Exception e){
		}
	}

	public List<Notification> getNotificationsForStudent(String rollNo) {
	    List<Notification> list = new ArrayList<>();
	    String sql = "SELECT * FROM notification WHERE requested_by=?  ";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, rollNo);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Notification n = new Notification();
					
	                list.add(n.setNotification(rs));
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	public boolean createNotification(int requestId,String status,String teacherRollNo){
		String sql = "insert into  notification  (requested_by ,action,reviewed_by,status)"+
		"(select requested_by,action,?,? from request_access where request_id=?)";
		try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1,teacherRollNo);
			ps.setString(2,status);
			ps.setInt(3,requestId);
			
	        return ps.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public boolean deleteNotification(int requestId) {
	    String sql = "delete from notification where notify_id=?";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setInt(1, requestId);
	        return ps.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	
	
	
	public List<Logs> getAllLogs() {
	    List<Logs> logs = new ArrayList<>();
	    String sql = "SELECT * FROM audit_logs ORDER BY id DESC";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            Logs req = new Logs();
				

	            logs.add(req.setLog(rs));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return logs;
	}
	
	public List<UserInfo> getStudentsForTeacher(String teacherName){
		List<UserInfo> students = new ArrayList<>();
	    String sql = "SELECT * FROM person p join role r on p.role_id=r.role_id where r.role_id=3 ";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            UserInfo user=new UserInfo();
				

	            students.add(user.setUserInfo(rs));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return students;
	}

}
