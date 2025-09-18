package com.school.dao;

import com.school.servlets.*;
import com.school.model.*;
import com.school.utils.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.sql.*;

public class RuleConditionCheck{
	
	public String normalize(String value){
		if(value==null)
			return "";
		return value.toLowerCase().replaceAll("\\s", "");
	}
	
	public String normalize(int value){
		if(value==0)
			return "";
		return String.valueOf(value).toLowerCase().replaceAll("\\s", "");
	}
	
	public boolean RuleIs(String attribute,String value,UserInfo user,String action){
		
		String normalizeValue=normalize(value);
		if(attribute.equals("action"))
			return normalize(action).equals(normalizeValue);
		if(attribute.equals("class"))
			return normalize(user.getClassNo()).equals(normalizeValue);
		if(attribute.equals("name"))
			return normalize(user.getName()).equals(normalizeValue);
		if(attribute.equals("role"))
			return normalize(user.getRole()).equals(normalizeValue);
		if(attribute.equals("superior"))
			return normalize(user.getSuperior()).equals(normalizeValue);
		
		return false;
	}
	
	public boolean RuleContains(String attribute,String value,UserInfo user,String action){
		
		String normalizeValue=normalize(value);
		if(attribute.equals("action"))
			return normalize(action).contains(normalizeValue);
		if(attribute.equals("class"))
			return normalize(user.getClassNo()).equals(normalizeValue);
		if(attribute.equals("name"))
			return normalize(user.getName()).contains(normalizeValue);
		if(attribute.equals("role"))
			return normalize(user.getRole()).contains(normalizeValue);
		if(attribute.equals("superior"))
			return normalize(user.getSuperior()).contains(normalizeValue);
		return false;
	}
	
	public boolean recursion(int ruleId,UserInfo user,String action,int order){
		String sql="Select attribute,operator,`value`,logic_op from rule_condition where rule_id=? and order_id=?";
		boolean res=false;
		String attribute="";
		String operator="";
		String value="";
		String logic="";
		try(Connection con=DBUtil.getConnection();
		PreparedStatement ps=con.prepareStatement(sql)){
			ps.setInt(1,ruleId);
			ps.setInt(2,order);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){
				attribute=rs.getString("attribute");
				operator=rs.getString("operator");
				value=rs.getString("value");
				logic=rs.getString("logic_op");
				System.out.println("operator:"+operator+"Logic:"+logic+"RuleId"+ruleId);
				if("is".equals(operator))
					res=RuleIs(attribute,value,user,action);
				else if("is not".equals(operator))
					res=!RuleIs(attribute,value,user,action);
				else if("contains".equals(operator))
					res=RuleContains(attribute,value,user,action);
		
			
			
			
				if(logic==null||logic.equals(""))
					return res;
				if(logic.equals("AND"))
					return res&&recursion(ruleId,user,action,order+1);
				if(logic.equals("OR"))
					return res||recursion(ruleId,user,action,order+1);
			}
		
		
		return false;
		}
		catch(Exception e){
			System.out.println("Error Catched");
			return false;
		}
		
	}
	
	public boolean checkRule(ResultSet rs,HttpSession session,HttpServletRequest req) throws SQLException{
		DAO dao=new DAO();
		UserInfo user=dao.getUserInfo((String)session.getAttribute("email"));
		String action=(String)req.getAttribute("action");
		
		return recursion(rs.getInt("rule_id"),user,action,1);
	}
	
	
}