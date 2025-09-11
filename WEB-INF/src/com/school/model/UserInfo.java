package com.school.model;

public class UserInfo {
	 private String rollNo;	
	 private String userid;
	 private String name;
	 private String password;
	 private String email;
    private String role;
    private int role_id;

    // getters & setters
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
	
	public String getUserId() { return userid; }
    public void setUserId(String userid) { this.userid = userid; }
	
	public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
	
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPass() {
		return password;
	}
	public void setPass(String password) {
		this.password = password;
	}
    
    public String getRole() { return role; }
    public void setRole(String role) { 
		this.role = role;
		switch(role){
			case "Admin":
				role_id=1;
				break;
			case "Teacher":
				role_id=2;
				break;
			case "Student":
				role_id=3;
				break;
		}
		
	}
	
	public int getRoleId() { return role_id; }
  
}
