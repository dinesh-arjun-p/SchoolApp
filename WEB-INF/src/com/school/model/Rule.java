package com.school.model;
import com.school.dao.*;
import java.sql.*;
import java.util.*;

public class Rule {
    private int ruleId;
    private List<Condition> condition;
	private List<ReviewerInfo> reviewers;
	private String executer;
	private String activeStatus;
    private int statusLimit; 
    private int priority;

	public Rule setRule(ResultSet rs) throws SQLException{
		this.setRuleId(rs.getInt("rule_id"));
	    this.setStatusLimit(rs.getInt("status_limit"));
	    this.setPriority(rs.getInt("priority"));
		return this;
	}
    // Getters and setters
    public int getRuleId() { return ruleId; }
    public void setRuleId(int ruleId) { this.ruleId = ruleId; }

    public List<Condition> getCondition() { return condition; }
    public void setCondition(List<Condition> condition){ 
		this.condition=condition;
		
	}
	
	public List<ReviewerInfo> getReviewers() { return reviewers; }
    public void setReviewers(List<ReviewerInfo> reviewers){ 
		this.reviewers=reviewers;
		
	}
	
	public String getExecuter() { return executer; }
    public void setExecuter(String executer){ 
		this.executer=executer;
		
	}
	
	public String getActiveStatus() { return activeStatus; }
    public void setActiveStatus(String activeStatus){ 
		this.activeStatus=activeStatus;
		
	}

     public int getStatusLimit() { return statusLimit; }
    public void setStatusLimit(int statusLimit) { this.statusLimit = statusLimit; }
	
	public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
	
	

}
