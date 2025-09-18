package com.school.model;



public class ReviewerInfo {
    private UserInfo user;
    private String role;

    public ReviewerInfo(UserInfo user, String role) {
        this.user = user;
        this.role = role;
    }

    public UserInfo getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }
	
	public String toString(){
		return user+"\n Role:"+role+"\n";
	}
}
