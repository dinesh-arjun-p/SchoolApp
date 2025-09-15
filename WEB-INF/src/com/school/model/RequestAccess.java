package com.school.model;

import java.sql.*;

public class RequestAccess {
    private int requestId;
    private Date requestDate;
    private String action;
    private String requestedBy;
    private int status;     
    private String assignedTo;  
	private String role;
	


	public RequestAccess setRequestAccess(ResultSet rs) throws SQLException{
		RequestAccess req = new RequestAccess();
	    req.setRequestId(rs.getInt("request_id"));
	    req.setRequestDate(rs.getDate("request_date"));
	    req.setAction(rs.getString("action"));
	    req.setRequestedBy(rs.getString("requested_by"));
	    req.setStatus(rs.getInt("status"));
	    req.setAssignedTo(rs.getString("assigned_to"));
		req.setRole(rs.getString("role"));
		return req;
	}
	
	
    // Getters & Setters
    public int getRequestId() {
        return requestId;
    }
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Date getRequestDate() {
        return requestDate;
    }
    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }

    public String getRequestedBy() {
        return requestedBy;
    }
    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
	
	public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
