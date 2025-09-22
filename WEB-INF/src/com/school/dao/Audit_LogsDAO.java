package com.school.dao;

import java.sql.*;

import com.school.model.*;
import com.school.utils.*;
import com.school.servlets.*;
import java.util.*;

public class Audit_LogsDAO {
	DAO dao=new DAO();
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
	
	public void recordLogin(String rollNo) {
	    String sql = "INSERT INTO audit_logs (username,event) VALUES (?,'Login')";
	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, rollNo);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public void recordLogout(String rollNo) {
	    String sql = "INSERT INTO audit_logs (username,event) VALUES (?,'Logout')";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, rollNo);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	public void recordCreateUser(String rollNo,String userName ,int roleId){
		String roleName=findRoleName(roleId);
		String event="Created User "+userName +" as "+roleName;
		String sql = "INSERT INTO audit_logs (username,event) VALUES (?,'"+ event+"')";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, rollNo);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void recordDeleteUser(String rollNo,String userName){
		String event="Deleted User "+userName ;
		String sql = "INSERT INTO audit_logs (username,event) VALUES (?,'"+ event+"')";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, rollNo);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void recordCreateRequest(String rollNo,String action){
		String sql = "INSERT INTO audit_logs (username,event,reg) VALUES (?,'Request Access',?)";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, rollNo);
			st.setString(2,action);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void recordRequestStatus(String rollNo, String status, int requestId) {
    String sql = 
        "INSERT INTO audit_logs (username, event, reg) " +
        "SELECT ?, CONCAT(?, ' Request  requested by ', requested_by), action " +
        "FROM request_access WHERE request_id = ?";

    try (Connection con = DBUtil.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, rollNo);       // for username
        ps.setString(2, status);       // for event prefix
        ps.setInt(3, requestId);       // for WHERE condition

        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
		}
	}
	
	public void clearAuditLogs() {
		String deleteSql = "DELETE FROM audit_logs";
		String resetSql = "ALTER TABLE audit_logs AUTO_INCREMENT = 1";

		try (Connection con = DBUtil.getConnection();
         Statement st = con.createStatement()) {
        
			st.executeUpdate(deleteSql);
			st.executeUpdate(resetSql);  // reset counter

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void recordCreateRule(int ruleId,String rollNo){
		
		String event="Created Rule "+ruleId;
		String sql = "INSERT INTO audit_logs (username,event) VALUES (?,'"+ event+"')";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, rollNo);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void recordDeleteRule(int ruleId,String userName){
		String event="Deleted Rule "+ruleId;
		String sql = "INSERT INTO audit_logs (username,event) VALUES (?,'"+ event+"')";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, userName);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void recordUpdateUser(String user,String profile){
		String event="Edited User "+profile ;
		String sql = "INSERT INTO audit_logs (username,event) VALUES (?,'"+ event+"')";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, user);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void recordUpdateRule(String user,int ruleId){
		String event="Edited Rule "+ruleId ;
		String sql = "INSERT INTO audit_logs (username,event) VALUES (?,'"+ event+"')";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, user);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}