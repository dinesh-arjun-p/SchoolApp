<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
	
<style>
    .profile-table {
        border-collapse: collapse;
        width: 50%;
        margin: 20px auto;
        font-family: Arial, sans-serif;
    }
    .profile-table th, .profile-table td {
        border: 1px solid #ddd;
        padding: 10px 15px;
        text-align: left;
    }
    .profile-table th {
        background-color: #f4f4f4;
        font-weight: bold;
    }
</style>
<table class="profile-table">
    <tr><th>Roll No</th><td><%= session.getAttribute("rollNo") %></td></tr>
    <tr><th>Name</th><td><%= session.getAttribute("uname") %></td></tr>
    <tr><th>Email</th><td><%= session.getAttribute("email") %></td></tr>
    <tr><th>Role</th><td><%= session.getAttribute("role") %></td></tr>

    <% if(session.getAttribute("phone_number")!=null){ %>
        <tr><th>Phone Number</th><td><%= session.getAttribute("phone_number") %></td></tr>
    <% } %>

    <% if((int)session.getAttribute("class")!=0){ %>
        <tr><th>Class</th><td><%= session.getAttribute("class") %></td></tr>
    <% } %>

    <% if(session.getAttribute("superior")!=null){ %>
        <tr><th>Superior</th><td><%= session.getAttribute("superior") %></td></tr>
    <% } %>
</table>