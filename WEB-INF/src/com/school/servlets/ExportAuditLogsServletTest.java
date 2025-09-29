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
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.*;
import com.itextpdf.layout.border.*;
import com.itextpdf.kernel.color.*;

import com.itextpdf.layout.font.FontProvider;        
import com.itextpdf.layout.font.FontSet;             
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;


import com.itextpdf.kernel.geom.PageSize; 
import java.io.IOException;
import java.io.FileNotFoundException;
import java.sql.SQLException;

@WebServlet("/ExportAuditLogsTest")
public class ExportAuditLogsServletTest extends HttpServlet {
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws FileNotFoundException,ServletException,IOException{
        HttpSession session = request.getSession(false);
        if (session == null || !"Admin".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }
		long startTime = System.currentTimeMillis(); 
		response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=AuditLogs.pdf");
		
		
		try{
        Class.forName("com.mysql.cj.jdbc.Driver"); 
		}catch (Exception e) {
            e.printStackTrace();
        }
		String url = "jdbc:mysql://localhost:3306/school";
		String user = "root";
		String pass = "DineshArjun@2004";
		
		PdfWriter pdfWriter=new PdfWriter(response.getOutputStream());
		PdfDocument pdfDocument=new PdfDocument(pdfWriter);
		pdfDocument.setDefaultPageSize(PageSize.A4);
		Document doc=new Document(pdfDocument);
		
		float fullWidth[]=new float[]{720f};
		
		
		
		Table header=new Table(new float[]{1f});
		
		
		Table tableDivider=new Table(fullWidth);
		Border gb=new SolidBorder(Color.GRAY,1f/2f);
		tableDivider.setBorder(gb);
		Table newLine=new Table(fullWidth);
		newLine.addCell(new Cell().add("\n").setBorder(Border.NO_BORDER));
		
		
		
		header.setWidth(UnitValue.createPercentValue(100)); 
		header.addCell(new Cell().add("Audit Logs").setBold().setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
		doc.add(header);
		doc.add(newLine);
		doc.add(tableDivider);
		doc.add(newLine);	
		
		PdfFont monoFont = PdfFontFactory.createFont(StandardFonts.COURIER);

		String line = String.format("%-5s %-15s %-70s %-20s",
						"Id",
						"Username",
						"Event",
						"Date & Time"
					);
		doc.add(new Paragraph(line).setFont(monoFont).setFontSize(7).setBold());
		try(Connection con=DriverManager.getConnection(url,user,pass);
		Statement st=con.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);){
			st.setFetchSize(Integer.MIN_VALUE);
			try(ResultSet rs=st.executeQuery("Select * from audit_logs ")){
				
				while(rs.next()){
						line = String.format("%-5s %-15s %-70s %-20s",
						rs.getInt("id"),
						rs.getString("username"),
						getString(rs.getString("event"), rs.getString("reg")),
						rs.getDate("log_date").toString() + " " + rs.getTime("log_time").toString()
					);
					doc.add(new Paragraph(line).setFont(monoFont).setFontSize(7).setMultipliedLeading(0.7f));
					
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		doc.close();
		
		long endTime = System.currentTimeMillis(); 
		long durationMs = endTime - startTime;

		

		System.out.println("Download duration: " + durationMs + " milliseconds ");
		Audit_LogsDAO al=new Audit_LogsDAO();
		al.recordExportAuditLogs((String)session.getAttribute("rollNo"),"Pdf");
    }
	
	
	static String getString(String name,String name2){
		if(name2!=null&&!name2.equals(""))
			return name+"("+name2+")";
		return name;
	}
	

}
