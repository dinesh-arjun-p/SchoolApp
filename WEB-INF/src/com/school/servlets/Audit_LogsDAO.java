package com.school.servlets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.school.model.*;
import com.school.utils.*;
import java.util.*;

public class Audit_LogsDAO {
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
	
	public void recordRequestStatus(String rollNo, String action, int requestId) {
    String sql = 
        "INSERT INTO audit_logs (username, event, reg) " +
        "SELECT ?, CONCAT(?, ' Request Access requested by ', requested_by), action " +
        "FROM request_access WHERE request_id = ?";

    try (Connection con = DBUtil.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, rollNo);       // for username
        ps.setString(2, action);       // for event prefix
        ps.setInt(3, requestId);       // for WHERE condition

        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
		}
	}

}