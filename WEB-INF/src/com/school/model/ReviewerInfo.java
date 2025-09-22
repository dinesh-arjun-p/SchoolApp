package com.school.model;



public class ReviewerInfo {
    private String user;
    private String role;
	private String activeStatus;

    public ReviewerInfo(String user, String activeStatus) {
        this.user = user;
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
	
	
	public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewerInfo)) return false;
        ReviewerInfo s = (ReviewerInfo) o;
        return this.user.equals(s.user);
    }


	public String toString(){
		return "RollNo:"+user+"\n";
	}
}
