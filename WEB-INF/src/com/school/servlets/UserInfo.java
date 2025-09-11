package com.school.servlets;

public class UserInfo {
	 private String rollNo;	
	 private String name;
	 private String password;
    private String role;
    

    // getters & setters
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

  
}
