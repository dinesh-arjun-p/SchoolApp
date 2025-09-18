package com.school.model;

import java.sql.*;
import java.util.List;
import com.school.servlets.*;
import com.school.dao.*;
public class RequestAccess {
    private int requestId;
    private Date requestDate;
    private String action;
    private String requestedBy;
    private int status;     
    private List<String> assignedTo;  
	private String role;
	private String actionFor;
	private String actionValue;
	


	public RequestAccess setRequestAccess(ResultSet rs) throws SQLException{
	    this.setRequestId(rs.getInt("request_id"));
	    this.setRequestDate(rs.getDate("request_date"));
	    this.setAction(rs.getString("action"));
	    this.setRequestedBy(rs.getString("requested_by"));
	    this.setStatus(rs.getInt("status"));
		this.setRole(rs.getString("role"));
		this.setActionFor(rs.getString("action_for"));
		this.setActionValue(rs.getString("action_value"));
		return this;
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

    public List<String> getAssignedTo() {
        return assignedTo;
    }
    public void setAssignedTo(List<String> assignedTo) {
        this.assignedTo = assignedTo;
    }
	
	public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
	
	public String getActionFor() {
        return actionFor;
    }
    public void setActionFor(String actionFor) {
        this.actionFor = actionFor;
    }
	
	public String getActionValue() {
        return actionValue;
    }
    public void setActionValue(String actionValue) {
        this.actionValue = actionValue;
    }
}
