package com.school.model;
import com.school.dao.*;
import java.sql.*;
import java.util.*;

public class Condition {
	private String attribute;
	private String operator;
	private String value;
	private String logicOp;
	
	public Condition (String attribute,String operator,String value){
		this.attribute=attribute;
		this.operator=operator;
		this.value=value;
	}
	
	public Condition (String attribute,String operator,String value,String logicOp){
		this.attribute=attribute;
		this.operator=operator;
		this.value=value;
		this.logicOp=logicOp;
	}
	
	public String getAttribute(){
		return attribute;
	}
	
	public String getOperator(){
		return operator;
	}
	
	public String getValue(){
		return value;
	}
	
	public String getLogicOp(){
		return logicOp;
	}
    
	public String toString(){
		if(logicOp==null)
			return attribute+" "+operator+" "+value+" ";
		return attribute+" "+operator+" "+value+" "+logicOp+"\n";
	}
	
	

}
