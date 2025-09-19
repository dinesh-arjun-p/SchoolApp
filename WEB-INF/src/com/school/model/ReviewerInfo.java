package com.school.model;



public class ReviewerInfo {
    private String user;
    private String role;
	private String activeStatus;

    public ReviewerInfo(String user, String role,String activeStatus) {
        this.user = user;
        this.role = role;
		this.activeStatus=activeStatus;
    }

    public String getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }
	
	public String getActiveStatus() {
        return activeStatus;
	}
	
	public String toString(){
		return "RollNo:"+user+"\n";
	}
}
