package com.school.model;

import java.sql.*;
public class UserInfo {
	 private String rollNo;	
	 private String userid;
	 private String name;
	 private String password;
	 private String email;
    private String role;
    private int role_id;
	private String phone_number;
	private int classNo;
	private String superior;
	
	public UserInfo setUserInfo(ResultSet rs)throws SQLException{
		this.setRollNo(rs.getString("roll_no"));
		this.setName(rs.getString("name"));
		this.setPass(rs.getString("pass"));
		this.setRole(rs.getString("role_name"));
		this.setUserId(rs.getString("userid"));
		this.setEmail(rs.getString("email"));
		this.setPhoneNumber(rs.getString("phone_number"));
		this.setClassNo(rs.getInt("class"));
		this.setSuperior(rs.getString("superior"));
		return this;
	}
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
	
	public String getPhoneNumber() { return phone_number; }
    public void setPhoneNumber(String phone_number) { this.phone_number = phone_number; }
	
	public int getClassNo() { return classNo; }
    public void setClassNo(int classNo) { this.classNo = classNo; }
	
	public String getSuperior() { return superior; }
    public void setSuperior(String superior) { this.superior = superior; }
  
  
	public String toString(){
		return "Roll No:"+this.getRollNo()+" Name:"+this.getName();
	}
}
