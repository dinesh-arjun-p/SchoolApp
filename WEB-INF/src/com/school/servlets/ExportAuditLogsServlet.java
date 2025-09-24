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
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.*;
import com.itextpdf.layout.border.*;
import com.itextpdf.kernel.color.*;

import com.itextpdf.kernel.geom.PageSize; 
import java.io.IOException;
import java.io.FileNotFoundException;

@WebServlet("/ExportAuditLogs")
public class ExportAuditLogsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws FileNotFoundException,ServletException,IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"Admin".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }
		
		response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=AuditLogs.pdf");
		
        DAO dao = new DAO();
		List<Logs> logs=dao.getAllLogs("asc");
		String path="AuditLogs.pdf";
		PdfWriter pdfWriter=new PdfWriter(response.getOutputStream());
		PdfDocument pdfDocument=new PdfDocument(pdfWriter);
		pdfDocument.setDefaultPageSize(PageSize.A4);
		Document doc=new Document(pdfDocument);
		float[] columnWidths=new float[]{1/2,3/2,4,2,3/2,5/4};
		float fullWidth[]=new float[]{720f};
		
		
		Table table =new Table(UnitValue.createPercentArray(columnWidths));
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
		table.setWidth(UnitValue.createPercentValue(100)); 
			table.addCell(getHeader("Id"));
			table.addCell(getHeader("UserName"));
			table.addCell(getHeader("Event"));
			table.addCell(getHeader("Regarding"));
			table.addCell(getHeader("Date"));
			table.addCell(getHeader("Time"));
		for(Logs log:logs){
			table.addCell(getValue(String.valueOf(log.getId())));
			table.addCell(getValue(String.valueOf(log.getUserName())));
			table.addCell(getValue(String.valueOf(log.getEvent())));
			table.addCell(getValue(String.valueOf(log.getReg() )));
			table.addCell(getValue(String.valueOf(log.getDate())).setFontSize(9));
			table.addCell(getValue(String.valueOf(log.getTime())).setFontSize(9));
		}
		doc.add(table);
		doc.close();
		Audit_LogsDAO al=new Audit_LogsDAO();
		al.recordExportAuditLogs((String)session.getAttribute("rollNo"));
        response.sendRedirect("Home.jsp?msg=Exported+SuccessFully");
    }
	static Cell getHeader(String name){
		return new Cell().add(name).setBold().setTextAlignment(TextAlignment.CENTER);
	}
	static Cell getValue(String name){
		return new Cell().add(name).setTextAlignment(TextAlignment.CENTER);
	}
}
