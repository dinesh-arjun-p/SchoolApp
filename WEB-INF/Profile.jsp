<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
<h2>Welcome Roll No=<%= session.getAttribute("rollNo") %>
<br> Name=<%= session.getAttribute("uname") %>
<br> Email=<%= session.getAttribute("email") %>
<br> role_name= <%= session.getAttribute("role") %>
<% if(session.getAttribute("phone_number")!=null){%>
<br> phone number= <%= session.getAttribute("phone_number") %>
<% }%>
<% if((int)session.getAttribute("class")!=0){%>
<br> class= <%= session.getAttribute("class") %>
<% } %>
<% if(session.getAttribute("superior")!=null){%>
<br> Superior= <%= session.getAttribute("superior") %></h2>
<% } %>
<br>