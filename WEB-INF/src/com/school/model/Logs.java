package com.school.model;

import java.sql.*;

public class Logs {
    private int Id;
    private String username;
    private String event;
    private String reg;
    private Date date;  
	private Time time;

    // Getters & Setters
    public int getId() {
        return Id;
    }
    public void setId(int requestId) {
        this.Id = requestId;
    }

    public String getUserName() {
        return username;
    }
    public void setUserName(String username) {
        this.username = username;
    }

    public String getEvent() {
        return event;
    }
    public void setEvent(String Event) {
        this.event = Event;
    }

    public String getReg() {
        return reg;
    }
    public void setReg(String reg) {
        this.reg = reg;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }
    public void setTime(Time time) {
        this.time = time;
    }
}
