package com.school.model;

import java.sql.*;
public class Notification {
    private int notificationId;
    private String studentRollNo;
    private String action;
    private String reviewedBy;
    private String status; 
    private String request_date;

	public Notification setNotification(ResultSet rs) throws SQLException{
		Notification n = new Notification();
		n.setNotificationId(rs.getInt("notify_id"));
	    n.setStudentRollNo(rs.getString("requested_by"));
	    n.setAction(rs.getString("action"));
	    n.setReviewedBy(rs.getString("reviewed_by"));
	    n.setStatus(rs.getString("status"));
	    n.setRequestDate(rs.getString("notify_date"));
		return n;
	}
    // Getters and setters
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public String getStudentRollNo() { return studentRollNo; }
    public void setStudentRollNo(String studentRollNo) { this.studentRollNo = studentRollNo; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
	
    
	public String getRequestDate() {
		return request_date;
	}
	public void setRequestDate(String request_date) {
		this.request_date = request_date;
	}
}
