package com.school.servlets;

import com.school.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.school.dao.*;
import java.util.*;
import java.sql.*;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.sql.SQLException;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

@WebServlet("/ExportAuditLogsAsExcel")
public class ExportAuditLogsAsExcelServlet extends HttpServlet {
	
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws FileNotFoundException,ServletException,IOException{
        HttpSession session = request.getSession(false);
        if (session == null || !"Admin".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }
		int row=0;
		long startTime = System.currentTimeMillis(); 
		response.setContentType("application/xls");
        response.setHeader("Content-Disposition", "attachment; filename=AuditLogs.xls");
		
		
		try{
        Class.forName("com.mysql.cj.jdbc.Driver"); 
		}catch (Exception e) {
            e.printStackTrace();
        }
		String url = "jdbc:mysql://localhost:3306/school";
		String user = "root";
		String pass = "DineshArjun@2004";
		
		
		
	
		try(SXSSFWorkbook wb=new  SXSSFWorkbook(1000);
		Connection con=DriverManager.getConnection(url,user,pass);
		Statement st=con.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);){
			st.setFetchSize(Integer.MIN_VALUE);
			Sheet s=wb.createSheet("AuditLogs");
			try(ResultSet rs=st.executeQuery("Select * from audit_logs")){
				s.setColumnWidth(0, 15*256);
				s.setColumnWidth(1, 25*256);
				s.setColumnWidth(2, 70*256);
				s.setColumnWidth(3, 15*256);
				s.setColumnWidth(4, 15*256);
				Row h=s.createRow(row++);
				h.createCell(0).setCellValue("Id");
				h.createCell(1).setCellValue("Username");
				h.createCell(2).setCellValue("Event");
				h.createCell(3).setCellValue("Date");
				h.createCell(4).setCellValue("Time");
				while(rs.next()){
						Row r=s.createRow(row++);
						r.createCell(0).setCellValue(rs.getInt("id"));
						r.createCell(1).setCellValue(rs.getString("username"));
						r.createCell(2).setCellValue(getString(rs.getString("event"), rs.getString("reg")));
						r.createCell(3).setCellValue(rs.getDate("log_date").toString());
						r.createCell(4).setCellValue(rs.getTime("log_time").toString());
					
				}
				wb.write(response.getOutputStream());
				wb.close();
				wb.dispose();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		

		
		long endTime = System.currentTimeMillis(); 
		long durationMs = endTime - startTime;

		

		System.out.println("Download duration: " + durationMs + " milliseconds ");
		Audit_LogsDAO al=new Audit_LogsDAO();
		al.recordExportAuditLogs((String)session.getAttribute("rollNo"),"Excel");
    }
	
	
	
	static String getString(String name,String name2){
		if(name2!=null&&!name2.equals(""))
			return name+"("+name2+")";
		return name;
	}
	

}













