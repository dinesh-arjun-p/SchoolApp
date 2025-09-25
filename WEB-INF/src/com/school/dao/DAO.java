package com.school.dao;

import java.sql.*;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import jakarta.servlet.http.*;
import com.school.model.*;
import com.school.servlets.*;
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
	
	public String findGroupId(String roleName){
		switch(roleName){
			case "Admin":
				return ADMIN_GROUP_ID;
			case "Teacher":
				return TEACHER_GROUP_ID;
			case "Student":
				return STUDENT_GROUP_ID;
			default:
				return "";
		}
	}
	
	
	public boolean verifyUser(String uname, String password) {
	    String sql = "SELECT * FROM person WHERE name=? AND pass=?";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, uname);
	        st.setString(2, password);

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
	
	public void updateEmailInOkta(String newEmail,  UserInfo user) {
		try {
			

			String userId = getOktaUserId(user.getRollNo());
			if (userId == null) {
				System.out.println("Okta user not found for rollNo: " + user.getRollNo());
				return;
			}
			URI userUri = new URI(oktaDomain + "/api/v1/users/" + userId);
			HttpURLConnection conn = (HttpURLConnection) userUri.toURL().openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "SSWS " + apiToken);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

		  
			String jsonBody = "{ \"profile\": { " +
							  "\"email\": \"" + newEmail + "\"," +
							  "\"login\": \"" + newEmail + "\"" +
							  "} }";

			try (OutputStream os = conn.getOutputStream()) {
				os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
			}

			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				System.out.println("Okta email updated successfully for userId: " + userId);
			} else {
				System.out.println("Failed to update email in Okta. Response: " + responseCode);
				try (InputStream err = conn.getErrorStream()) {
					if (err != null) {
						System.out.println(new String(err.readAllBytes(), StandardCharsets.UTF_8));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateGroupInOkta(  String newRole,String oldRole,UserInfo user) {
		try {
			
			String userId = getOktaUserId(user.getRollNo());
			if (userId == null) {
				System.out.println("User not found in Okta for email: ");
				return;
			}
			if (oldRole != null) {
				String oldGroupId = findGroupId(oldRole);
				URI userUri = new URI(oktaDomain + "/api/v1/groups/" + oldGroupId + "/users/" + userId);
				HttpURLConnection conn = (HttpURLConnection) userUri.toURL().openConnection();
				
				conn.setRequestMethod("DELETE");
				conn.setRequestProperty("Authorization", "SSWS " + apiToken);

				int resp = conn.getResponseCode();
				if (resp == 204) {
					System.out.println("Removed user from old role group: " + oldRole);
				}
			}

			// 3. Add to new role group
			if (newRole!=null) {
				String newGroupId = findGroupId(newRole);
				URI userUri = new URI(oktaDomain + "/api/v1/groups/" + newGroupId + "/users/" + userId);
				HttpURLConnection conn = (HttpURLConnection) userUri.toURL().openConnection();
				
				conn.setRequestMethod("PUT");
				conn.setRequestProperty("Authorization", "SSWS " + apiToken);

				int resp = conn.getResponseCode();
				if (resp == 204) {
					System.out.println("Added user to new role group: " + newRole);
				} else {
					System.out.println("Failed to add to group, response: " + resp);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	public boolean createUser(String name, String password, int roleId, String email,String userId,int classNo,String phoneNumber,String superior) {
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
			sql="Update person set class=? where roll_no=?";
			PreparedStatement ps=con.prepareStatement(sql);;
			if(classNo!=0){
				ps.setInt(1,classNo);
				ps.setString(2,rollNo);
				ps.executeUpdate();
			}
			sql="Update person set superior=? where roll_no=?";
			ps=con.prepareStatement(sql);
			if(superior==null||!superior.equals("")){
				ps.setString(1,superior);
				ps.setString(2,rollNo);
				ps.executeUpdate();
			}
			sql="Update person set phone_number=? where roll_no=?";
			ps=con.prepareStatement(sql);
			if(phoneNumber==null||!phoneNumber.equals("")){
				ps.setString(1,phoneNumber);
				ps.setString(2,rollNo);
				ps.executeUpdate();
			}
			ps.close();
            return rows > 0;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
		return false;
	}

	public boolean updateUser(UserInfo user){
		String sql = "UPDATE person SET name=?, pass=?, email=?, role_id=?, phone_number=?, superior=?, class=? WHERE roll_no=?";
    
		try (Connection con = DBUtil.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setString(1, user.getName());
			ps.setString(2, user.getPass());
			ps.setString(3, user.getEmail());
			ps.setInt(4, user.getRoleId());
			ps.setString(5, user.getPhoneNumber());
			if(user.getSuperior()==null||user.getSuperior().equals(""))
				ps.setNull(6, java.sql.Types.VARCHAR); 
			else
				ps.setString(6, user.getSuperior());

			if (user.getClassNo() == 0) {
				ps.setNull(7, java.sql.Types.VARCHAR); 
			} else {
				ps.setInt(7, user.getClassNo());
			}
			
			ps.setString(8, user.getRollNo());

			int rows = ps.executeUpdate();
			return rows > 0;

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
	    String sql = "SELECT * FROM person p JOIN role r ON p.role_id = r.role_id order by p.role_id";

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
	    String sql = "DELETE FROM person WHERE roll_no=? or email=?";
		String sql1="update rule_work_flow set active_status='Deleted' where incharge=?";
		String sql2="update request_reviewer set updated='no' where reviewer_roll_no=?";
	    try (Connection con = DBUtil.getConnection()) {
			PreparedStatement st = con.prepareStatement(sql);
	        st.setString(1, rollNo);
			 st.setString(2, rollNo);
	        boolean temp= st.executeUpdate() > 0;
			if(temp){
				st=con.prepareStatement(sql1);
				st.setString(1,rollNo);
				st.executeUpdate();
				st=con.prepareStatement(sql2);
				st.setString(1,rollNo);
				st.executeUpdate();
			}
			st.close();
			return temp;
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

	public boolean isRuleTrue(ResultSet rs,HttpSession session,HttpServletRequest req) throws SQLException{
		RuleConditionCheck rc=new RuleConditionCheck();
		return rc.checkRule(rs,session,req);
	}
	public int getRule(HttpSession session,HttpServletRequest req){
		String sql="Select * from rule where priority>-1 order by priority desc,rule_id";
		try(Connection con=DBUtil.getConnection();
		PreparedStatement st=con.prepareStatement(sql)){
			ResultSet rs=st.executeQuery();
			while(rs.next()){
				if(isRuleTrue(rs,session,req))
					return rs.getInt("rule_id");
			}
		}
		catch(Exception e){
			return -1;
		}
		return -1;
	}
	
	public Rule getRuleById(int ruleId) {
    Rule r = null;
    String sql = "SELECT * FROM rule WHERE rule_id = ?";
		try (Connection con = DBUtil.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, ruleId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				r = new Rule();
				r.setRule(rs);
				r.setCondition(getConditions(r.getRuleId()));
			    r.setReviewers(getReviewers(r.getRuleId()));
			    r.setExecuter(getExecuter(r.getRuleId()));
				r.setActiveStatus(getActiveStatusOfExecuter(r.getRuleId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	
	public int changeRoleInRequest(int requestId) throws SQLException{
		String sql = "UPDATE request_access ra  "+
             " JOIN rule r ON ra.rule_id = r.rule_id "+
             " SET ra.role = 'Executer' "+
             " WHERE ra.request_id = ? "+ 
             " AND ra.status >= r.status_limit ";


		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, requestId);
			return ps.executeUpdate(); 
		}
		catch(Exception e){
			return 0;
		}

	}
	
	public void changeRoleInRequest(Rule rule) throws SQLException{
		String sql = "UPDATE request_access ra  "+
             " JOIN rule r ON ra.rule_id = r.rule_id "+
             " SET ra.role = 'Executer' "+
             " WHERE ra.rule_id = ? "+ 
             " AND ra.status >= r.status_limit ";
		String sql1 = "UPDATE request_access ra  "+
             " JOIN rule r ON ra.rule_id = r.rule_id "+
             " SET ra.role = 'Reviewer' "+
             " WHERE ra.rule_id = ? "+ 
             " AND ra.status < r.status_limit ";


		try (Connection con = DBUtil.getConnection()) {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, rule.getRuleId());
			 ps.executeUpdate(); 
			 ps = con.prepareStatement(sql1);
			ps.setInt(1, rule.getRuleId());
			 ps.executeUpdate(); 
			 ps.close();
			return;
			
		}
		catch(Exception e){
			return ;
		}

	}
	
	public int assignToReviewers(int requestId)throws SQLException{
		String deleteSql="delete from request_reviewer where request_id=?";
		String insertSql = "INSERT INTO request_reviewer (request_id, reviewer_roll_no,role) " +
                       "SELECT ra.request_id, rwf.incharge,rwf.role " +
                       "FROM request_access ra " +
                       "JOIN rule_work_flow rwf ON ra.rule_id = rwf.rule_id " +
                       "WHERE ra.request_id = ? ";

		try (Connection con = DBUtil.getConnection()) {
			PreparedStatement ps = con.prepareStatement(deleteSql);
			ps.setInt(1, requestId);
			ps.executeUpdate();
			ps=con.prepareStatement(insertSql);
			ps.setInt(1, requestId);
			int temp=ps.executeUpdate();   
			ps.close();
			return temp;
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
				if (rule == -1) {
					st.setNull(3, java.sql.Types.INTEGER);
				} else {
					st.setInt(3, rule);
				}
					
				st.setString(4, action_value); 
				st.setString(5,action_for);

				int affectedRows = st.executeUpdate();

				if (affectedRows > 0) {
					try (ResultSet rs = st.getGeneratedKeys()) {
					if (rs.next()) {
						if(rule==-1){
							executeRequest(rs.getInt(1));
							return true;
						}
						else{
							assignToReviewers(rs.getInt(1));
							changeRoleInRequest(rs.getInt(1));
							return true;
						}
					}
				}
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false; // return -1 if failed
	}

	
	public List<AssignedTo> getAssignedToFunc(int requestId, String role) {
		List<AssignedTo> res = new ArrayList<>();
		String sql = "SELECT rr.reviewer_roll_no,updated " +
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
					AssignedTo assignedto=new AssignedTo(rs.getString("reviewer_roll_no"),rs.getString("updated"));
					res.add(assignedto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	
	public List<RequestAccess> getAllRequest(){
		List<RequestAccess> requests = new ArrayList<>();
        String sql = "SELECT * FROM request_access  where state='Pending'" ;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    changeRoleInRequest(rs.getInt("request_id"));
					RequestAccess req = new RequestAccess().setRequestAccess(rs);
					req.setAssignedTo(getAssignedToFunc(req.getRequestId(),req.getRole()));
				
                    requests.add(req);
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
                    
					changeRoleInRequest(rs.getInt("request_id"));
					RequestAccess req = new RequestAccess().setRequestAccess(rs);
					req.setAssignedTo(getAssignedToFunc(req.getRequestId(),req.getRole()));
                    requests.add(req);
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
		+"WHERE rr.role = 'Reviewer' and rr.reviewer_roll_no=? and decision='Pending' and ra.state='Pending'";

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
	   String sql = "SELECT ra.* FROM request_access ra "+
				" JOIN request_reviewer rr ON rr.request_id = ra.request_id "+
				" WHERE rr.reviewer_roll_no = ?  and rr.role='Executer' AND  ra.role = 'Executer' and "+
				" ra.state='Pending' ";
          

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
		String sql = "UPDATE request_access ra  "+
             " JOIN rule r ON ra.rule_id = r.rule_id "+
             " SET ra.role = 'Executer' "+
             " WHERE ra.request_id = ? "+ 
             " AND ra.status >= r.status_limit ";

	    try (PreparedStatement ps = con.prepareStatement(sql);){
			 ps.setInt(1,requestId);
	         ps.executeUpdate();
			

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void setStatus(Connection con,int requestId)throws SQLException{
		
		
		String sql ="UPDATE request_access ra " +
             "SET ra.status = ra.status+1 " +
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
	
	public boolean executeRequest(int requestId){
			executeAction(requestId);
			String executeSQL = "update request_access set state='Executed' where request_id=?";
			String deleteSQL="delete from request_reviewer where request_id=?";
			try (Connection con = DBUtil.getConnection()) {
				con.setAutoCommit(false);

				try (PreparedStatement ps = con.prepareStatement(executeSQL)) {
					ps.setInt(1, requestId);
					ps.executeUpdate();
				}
				try (PreparedStatement ps = con.prepareStatement(deleteSQL)) {
					ps.setInt(1, requestId);
					ps.executeUpdate();
				}
				con.commit(); 
				return true;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
	}
	
	public boolean updateRequestStatus(int requestId, String decision,String teacherRollNo,String role)  {
		Audit_LogsDAO al=new Audit_LogsDAO();
		al.recordRequestStatus(teacherRollNo,decision,requestId);
		if(decision.equals("Approved")){
			if(role.equals("Admin")){
				
				try (Connection con = DBUtil.getConnection()) {
					// turn off auto-commit for transaction safety
					con.setAutoCommit(false);

						setStatusAndRole(con,requestId);
					con.commit();  
					return true;

				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
			else{
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
		}
		else if(decision.equals("Rejected")){
			String rejectSQL = "update request_access set state='Rejected' where request_id=?";
			String deleteSQL="delete from request_reviewer where request_id=?";
			try (Connection con = DBUtil.getConnection()) {
				// turn off auto-commit for transaction safety
				con.setAutoCommit(false);

				try (PreparedStatement ps = con.prepareStatement(rejectSQL)) {
					ps.setInt(1, requestId);
					ps.executeUpdate();
				}
				try (PreparedStatement ps = con.prepareStatement(deleteSQL)) {
					ps.setInt(1, requestId);
					ps.executeUpdate();
				}
				con.commit(); 
				return true;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		else if(decision.equals("Executed")){
			executeRequest(requestId);
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
	
	
	
	
	public List<Logs> getLogs(int pageNumber,int pageSize) {
	    List<Logs> logs = new ArrayList<>();
	    String sql = "SELECT * FROM audit_logs  ORDER BY id desc limit ? offset ?";
		int offSet=(pageNumber-1)*pageSize;
	    try (Connection con = DBUtil.getConnection(); 
	         PreparedStatement ps = con.prepareStatement(sql)){
			 ps.setInt(1,pageSize);
			 ps.setInt(2,offSet);
	        try(ResultSet rs = ps.executeQuery()){
				while (rs.next()) {
					Logs req = new Logs();
					

					logs.add(req.setLog(rs));
				}
			}
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return logs;
	}
	
	public int getTotalNoOfPages(int pageSize){
		int count=0;
		String sql = "SELECT count(*) FROM audit_logs";
	    try (Connection con = DBUtil.getConnection(); 
	         Statement ps = con.createStatement()){
	        try(ResultSet rs = ps.executeQuery(sql)){
				if (rs.next()) {
					count=(int)Math.ceil(rs.getInt(1)*1.0/pageSize);
				}
			}
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return count;
	}
	
	public List<UserInfo> getSuperior(){
		List<UserInfo> superior = new ArrayList<>();
	    String sql = "SELECT * FROM person p join role r on p.role_id=r.role_id where r.role_id<3 ";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            UserInfo user=new UserInfo();
				

	            superior.add(user.setUserInfo(rs));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return superior;
	}
	
	public List<UserInfo> getStudentsForTeacher(String teacherRollNo){
		List<UserInfo> students = new ArrayList<>();
	    String sql = "SELECT * FROM person p join role r on p.role_id=r.role_id where p.role_id=3 and (p.superior =? or p.superior is null)";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1,teacherRollNo);
			ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            UserInfo user=new UserInfo();
				

	            students.add(user.setUserInfo(rs));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return students;
	}
	
	
	public List<UserInfo> getTeacher(){
		List<UserInfo> teachers=new ArrayList<>();
		try (Connection con = DBUtil.getConnection()) {

            
            String sqlTeachers = "SELECT * FROM person p JOIN role r ON p.role_id = r.role_id WHERE r.role_name = 'Teacher'";
            try (PreparedStatement ps = con.prepareStatement(sqlTeachers);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserInfo u = new UserInfo();
                    teachers.add(u.setUserInfo(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
		return teachers;
	}


	public List<String> getAllAttributes() {
		List<String> attributes = new ArrayList<>();
		String sql = "SELECT attribute FROM attribute";
	
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				attributes.add(rs.getString("attribute"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attributes;
	}

	public Map<String, List<String>> getAttributeOperators() {
    Map<String, List<String>> map = new HashMap<>();
    String sql = "SELECT attribute, attribute_operator FROM attribute_operator";
    try (Connection con = DBUtil.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            String attr = rs.getString("attribute");
            String op = rs.getString("attribute_operator");
            map.computeIfAbsent(attr, k -> new ArrayList<>()).add(op);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return map;
}

public Map<String, List<String>> getAttributeValues() {
    Map<String, List<String>> map = new HashMap<>();
    String sql = "SELECT attribute, attribute_value FROM attribute_value";
    try (Connection con = DBUtil.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            String attr = rs.getString("attribute");
            String val = rs.getString("attribute_value");
            map.computeIfAbsent(attr, k -> new ArrayList<>()).add(val);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return map;
}

	
	public int createRule(int statusLimit,int priority){
		String sqlRule = "INSERT INTO rule (status_limit, priority) VALUES (?, ?)";
		try(Connection con=DBUtil.getConnection();
		PreparedStatement psRule=con.prepareStatement(sqlRule, Statement.RETURN_GENERATED_KEYS)){
            psRule.setInt(1, statusLimit);
            psRule.setInt(2, priority);
            psRule.executeUpdate();

            ResultSet rs = psRule.getGeneratedKeys();
            int ruleId = 0;
            if (rs.next()) {
				
                return rs.getInt(1);
            }
			return -1;
		}
		catch(Exception e){
			return -1;
		}
	}
	
	public boolean updateRule(Rule rule){
		String sql = "UPDATE rule SET priority=?, status_limit=? WHERE rule_id=?";
    
		try (Connection con = DBUtil.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setInt(1, rule.getPriority());
			ps.setInt(2, rule.getStatusLimit());
			
			
			ps.setInt(3, rule.getRuleId());

			int rows = ps.executeUpdate();
			changeRoleInRequest(rule);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	public boolean updateRuleExecuter(int ruleId,String executer){
		String sql = "UPDATE rule_work_flow SET incharge=?,active_status='Active' WHERE rule_id=? and role='Executer'";
    
		try (Connection con = DBUtil.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setString(1, executer);
			ps.setInt(2, ruleId);
			

			int rows = ps.executeUpdate();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean updateRequestExecuter(int ruleId,String executer){
		String sql = "UPDATE request_reviewer rr join request_access ra on rr.request_id "
		+"= ra.request_id SET rr.reviewer_roll_no=?,updated='yes' WHERE ra.rule_id=? and rr.role='Executer'";
    
		try (Connection con = DBUtil.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setString(1, executer);
			ps.setInt(2, ruleId);
			

			int rows = ps.executeUpdate();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean updateRuleAndRequestExecuter(int ruleId,String executer){
		boolean temp1=updateRuleExecuter(ruleId,executer);

		boolean temp2=updateRequestExecuter(ruleId,executer);
		return temp1 && temp2;
	}
	
	public boolean insertRule(int ruleId,String r){
		String sql="insert into rule_work_flow (rule_id,incharge,role)values "+
					" (?,?,'Reviewer') ";
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1,ruleId);
				ps.setString(2,r);
				return ps.executeUpdate()>0;
				
			}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean insertReviewer(int ruleId,String r){
		String sql="insert into request_reviewer (request_id,reviewer_roll_no,role)"+
					" Select request_id,? ,'Reviewer' from request_access where rule_id=?";
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1,r);
				ps.setInt(2,ruleId);
				return ps.executeUpdate()>0;
				
			}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean ChangeRule(int ruleId,String[] reviewers){
		if(reviewers==null)
				return true;
		for(String r: reviewers){
			String sql = "UPDATE rule_work_flow SET active_status='Active' WHERE rule_id=? and incharge=? and role='Reviewer'";
			int rows=0;
			try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
				
				ps.setInt(1, ruleId);
				ps.setString(2, r);
				

				rows=ps.executeUpdate();
				if(rows==0)
					return insertRule(ruleId,r);
				return true;
					
				
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	public boolean ChangeRequestReviewer(int ruleId,String[] reviewers){
		if(reviewers==null)
				return true;
		for(String r: reviewers){
			String sql = "UPDATE request_reviewer rr join request_access ra on rr.request_id "
		+"= ra.request_id SET updated='yes' WHERE ra.rule_id=?  and rr.reviewer_roll_no=?"
		+" and rr.role='Reviewer'";
			int rows=0;
			try (Connection con = DBUtil.getConnection();
				 PreparedStatement ps = con.prepareStatement(sql)) {
				
				ps.setInt(1, ruleId);
				ps.setString(2, r);
				

				rows=ps.executeUpdate();
				if(rows==0)
					return insertReviewer(ruleId,r);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	
	public boolean deleteInactive(int ruleId){
		String sql="delete from rule_work_flow where rule_id=? and active_status='Inactive'";
		try (Connection con = DBUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
				
				ps.setInt(1, ruleId);
				

				ps.executeUpdate();
				
				return true;
			} catch (Exception e) {
				return false;
			}
	}
	
	public boolean deleteNotUpdated(int ruleId){
		String sql="delete from request_reviewer   where updated='no' and "
		+" request_id=any(select request_id from request_access where rule_id=?)" ;

		try (Connection con = DBUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
				
				ps.setInt(1, ruleId);
				

				ps.executeUpdate();
				
				return true;
			} catch (Exception e) {
				return false;
			}
	}
	
	public boolean deleteInactiveAndNotUpdated(int ruleId){
		return deleteInactive(ruleId)&& deleteNotUpdated(ruleId);
	}
	
	public boolean ChangeReviewers(int ruleId,String[] reviewers){
		return ChangeRule(ruleId,reviewers) && ChangeRequestReviewer(ruleId,reviewers);
		
	}
	
	public boolean updateReviewers(int ruleId,String[] reviewers){
		return ChangeReviewers(ruleId,reviewers)&& deleteInactiveAndNotUpdated(ruleId);
	}
	
	public void createWorkFlow(int ruleId,String [] reviewers,String executer){
		String sqlRule ="INSERT INTO rule_work_flow (rule_id, incharge, role) VALUES (?, ?, ?)";
		try(Connection con=DBUtil.getConnection();
		PreparedStatement psWorkFlow=con.prepareStatement(sqlRule)){
            if (reviewers != null) {
                for (String r : reviewers) {
                    psWorkFlow.setInt(1, ruleId);
                    psWorkFlow.setString(2, r);
                    psWorkFlow.setString(3, "Reviewer");
                    psWorkFlow.addBatch();
                }
            }

            // Insert executor
            psWorkFlow.setInt(1, ruleId);
            psWorkFlow.setString(2, executer);
            psWorkFlow.setString(3, "Executer");
            psWorkFlow.addBatch();

            psWorkFlow.executeBatch();
		}
		catch(Exception e){
		}
		
	}
	
	
	public boolean deleteRuleCondition(int ruleId){
		
		String sql = "delete from rule_condition  WHERE rule_id=? ";
    
		try (Connection con = DBUtil.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setInt(1, ruleId);
			

			ps.executeUpdate();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean createRuleCondition(int ruleId,String [] attributes,String [] operators,String [] values,String [] logicOps){
		
		String sqlRule = "INSERT INTO rule_condition (rule_id, attribute, operator, value, logic_op, order_id) VALUES (?, ?, ?, ?, ?, ?)";
		try(Connection con=DBUtil.getConnection();
		PreparedStatement psCondition=con.prepareStatement(sqlRule)){
           for (int i = 0; i < attributes.length; i++) {
                psCondition.setInt(1, ruleId);
                psCondition.setString(2, attributes[i]);
                psCondition.setString(3, operators[i]);
                psCondition.setString(4, values[i]);
                String logic = (logicOps[i] == null || logicOps[i].isEmpty()) ? null : logicOps[i];
                psCondition.setString(5, logic);
                psCondition.setInt(6, i + 1);
                psCondition.addBatch();
				if(logicOps[i] == null || logicOps[i].isEmpty())
					break;
            }

            psCondition.executeBatch();

			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean updateRuleCondition(int ruleId,String [] attributes,String [] operators,String [] values,String [] logicOps){
		return deleteRuleCondition(ruleId)&&
		createRuleCondition(ruleId,attributes,operators,values,logicOps);
	}
	
	public boolean canDelete(int ruleId){
		String sql = "SELECT * FROM request_access WHERE rule_id = ? and state='Pending'";
		try (Connection con = DBUtil.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			 
			ps.setInt(1, ruleId);
			
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
					return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean deleteRule(int ruleId) {
		String sql1="Select request_id from request_access where rule_id=? ";
	    String sql = "DELETE FROM rule WHERE rule_id=? ";
		String max="Select max(rule_id) as maxr from rule";
	    try (Connection con = DBUtil.getConnection()) {
			PreparedStatement st = con.prepareStatement(sql1);
	        st.setInt(1, ruleId);
	        ResultSet rs=st.executeQuery();
			st=con.prepareStatement(sql);
			st.setInt(1,ruleId);
			int temp=st.executeUpdate();
			st=con.prepareStatement(max);
			ResultSet m=st.executeQuery();
			if(m.next()){
			String resetSql = "ALTER TABLE rule AUTO_INCREMENT = "+(m.getInt("maxr")+1);
			Statement alterStmt = con.createStatement();
			alterStmt.executeUpdate(resetSql);
			}
			while(rs.next()){
				assignToReviewers(rs.getInt("request_id"));
				
			}
			return temp>0;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public List<Rule> getRules(){
		
		List<Rule> rules=new ArrayList<>();
		String sqlRule = "Select * from rule where priority > 0 order by rule_id ";
		try(Connection con=DBUtil.getConnection();
		PreparedStatement ps=con.prepareStatement(sqlRule)){
		   ResultSet rs=ps.executeQuery();
		   while(rs.next()){
			   Rule r=new Rule().setRule(rs);
			   r.setCondition(getConditions(r.getRuleId()));
			   r.setReviewers(getReviewers(r.getRuleId()));
			   r.setExecuter(getExecuter(r.getRuleId()));
			   r.setActiveStatus(getActiveStatusOfExecuter(r.getRuleId()));
			   rules.add(r);
		   }

		}
		catch(Exception e){
			  e.printStackTrace();
		}
		return rules;
	}
	
	public List<ReviewerInfo> getReviewers(int ruleId){
		List<ReviewerInfo> reviewers=new ArrayList<>();
		String sqlRule = "Select * from rule_work_flow where rule_id =? and role='Reviewer' ";
		try(Connection con=DBUtil.getConnection();
		PreparedStatement ps=con.prepareStatement(sqlRule)){
           ps.setInt(1,ruleId);
		   ResultSet rs=ps.executeQuery();
		   while(rs.next()){
			   ReviewerInfo r=new ReviewerInfo(rs.getString("incharge"),rs.getString("active_status"));
			   reviewers.add(r);
		   }

		}
		catch(Exception e){
			  e.printStackTrace();
		}
		return reviewers;
	}
	
	public String getExecuter(int ruleId){
		String sqlRule = "Select * from rule_work_flow where rule_id =? and role='Executer' ";
		try(Connection con=DBUtil.getConnection();
		PreparedStatement ps=con.prepareStatement(sqlRule)){
           ps.setInt(1,ruleId);
		   ResultSet rs=ps.executeQuery();
		   while(rs.next()){
			   return rs.getString("incharge");
		   }

		}
		catch(Exception e){
			  e.printStackTrace();
		}
		return null;
	}
	
	
	public void inactiveRuleWorkFlow(int ruleId){
		
		String sql = "UPDATE rule_work_flow SET active_status='Inactive' WHERE rule_id=? ";
    
		try (Connection con = DBUtil.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setInt(1, ruleId);
			

			ps.executeUpdate();
			return;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ;
	}
	
	public void inactiveRequestReviewer(int ruleId){
		String sql = "UPDATE request_reviewer rr join request_access ra on rr.request_id "
		+"= ra.request_id SET updated='no' WHERE ra.rule_id=? ";
		
		try (Connection con = DBUtil.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setInt(1, ruleId);
			

			ps.executeUpdate();
			return;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ;
	}
	
	public void inactiveRule(int ruleId){
		inactiveRuleWorkFlow(ruleId);
		inactiveRequestReviewer(ruleId);
	}
	
	public String getActiveStatusOfExecuter(int ruleId){
		String sqlRule = "Select * from rule_work_flow where rule_id =? and role='Executer' ";
		try(Connection con=DBUtil.getConnection();
		PreparedStatement ps=con.prepareStatement(sqlRule)){
           ps.setInt(1,ruleId);
		   ResultSet rs=ps.executeQuery();
		   while(rs.next()){
			   return rs.getString("active_status");
		   }

		}
		catch(Exception e){
			  e.printStackTrace();
		}
		return null;
	}
	
	public List<Condition> getConditions(int ruleId){
		List<Condition> conditions=new ArrayList<>();
		String sqlRule = "Select * from rule_condition where rule_id =? order by order_id";
		try(Connection con=DBUtil.getConnection();
		PreparedStatement ps=con.prepareStatement(sqlRule)){
           ps.setInt(1,ruleId);
		   ResultSet rs=ps.executeQuery();
		   while(rs.next()){
			   if (rs.getString("logic_op")==null){
					Condition condition=new Condition(rs.getString("attribute"),rs.getString("operator"),rs.getString("value"));
					conditions.add(condition);
					return conditions;
			   }
			   Condition condition=new Condition(rs.getString("attribute"),rs.getString("operator"),rs.getString("value"),rs.getString("logic_op"));
				conditions.add(condition);
			   
		   }

		}
		catch(Exception e){
			  e.printStackTrace();
		}
		return conditions;
	}
}
