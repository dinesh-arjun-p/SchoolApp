package com.school.model;

import java.sql.Date;

public class RequestAccess {
    private int requestId;
    private Date requestDate;
    private String department;
    private String requestedBy;
    private String status;      // Pending / Approved / Rejected
    private String reviewedBy;  // teacher/admin name

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

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRequestedBy() {
        return requestedBy;
    }
    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }
    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
}
