<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.school.model.*" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Create Rule</title>
<style>
    body {
        font-family: Arial, sans-serif;
        background-color: #f5f5f5;
        margin: 0;
        padding: 0;
    }
    .container {
        max-width: 500px;
        margin: 50px auto;
        padding: 25px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 6px rgba(0,0,0,0.1);
    }
    h2 {
        text-align: center;
        margin-bottom: 20px;
        color: #333;
    }
    form label {
        display: block;
        margin: 12px 0 5px;
        font-weight: bold;
        color: #555;
    }
    form input[type="text"],
    form input[type="email"],
    form input[type="password"],
    form select {
        width: 100%;
        padding: 8px 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }
    button[type="submit"] {
        width: 100%;
        padding: 10px;
        margin-top: 20px;
        border: none;
        border-radius: 4px;
        background-color: #4CAF50;
        color: white;
        font-size: 16px;
        cursor: pointer;
    }
    button[type="submit"]:hover {
        background-color: #45a049;
    }
</style>

</head>

<body>
	<%
    session = request.getSession(false);
	    if (session == null || !session.getAttributeNames().hasMoreElements()) {
	        response.sendRedirect("login.jsp");
	        return;
	    }
	    String currentRole = (String) session.getAttribute("role"); 
	    if (!"admin".equalsIgnoreCase(currentRole)) {
	        response.sendRedirect("Home.jsp");
	        return;
	    }
	%>
	<div class="container">
	<%
	Rule editRule = (Rule) request.getAttribute("editRule");
	if(editRule==null){
	%>

<h2>Create New Rule</h2>
	<% } else { %>
<h2>Edit Rule </h2>
	<% } %>
<%
    List<UserInfo> teachers = (List<UserInfo>) request.getAttribute("teachers");
%>

<form action="<%= (editRule == null ? "CreateRule" : "EditRule") %>" method="post">
	

    <label>Priority:</label>
	<input type="number" name="priority" required
		   value="<%= editRule != null ? editRule.getPriority() : "" %>"><br><br>

	<label>No of Reviewers needed:</label>
	<input type="number" id="statusLimit" name="status_limit" min="0" max="<%= teachers.size() %>" required
		   value="<%= editRule != null ? editRule.getStatusLimit() : "" %>"><br><br>


    <label>Reviewers (select at least the status limit):</label><br>
    <% for (UserInfo t : teachers) { 
		   boolean checked = false;
		   if (editRule != null) {
			   for (ReviewerInfo r : editRule.getReviewers()) {
				   if (r.getUser().equals(t.getRollNo())) {
					   checked = true;
					   break;
				   }
			   }
		   }
	%>
	<input type="checkbox" name="reviewers" value="<%= t.getRollNo() %>" <%= checked ? "checked" : "" %> >
	<%= t.getName() %><br>
	<% } %>


    <!-- Executor (must be exactly one) -->
    <label>Executor:</label><br>
		<select name="executer" required>
		<option value="">-- Select Executor --</option>
		<% for (UserInfo t : teachers) { 
			   String selected = (editRule != null && editRule.getExecuter().equals(t.getRollNo())) ? "selected" : "";
		%>
			<option value="<%= t.getRollNo() %>" <%= selected %> ><%= t.getRollNo() %></option>
		<% } %>
	</select>
	
	<%@ include file="\WEB-INF\RuleCondition.jsp" %>

	
	<%
	if(editRule==null){
	%>

    <button type="submit">Create Rule</button>
	<% }else{ %>
	<input type="hidden" name="ruleId" value="<%= editRule.getRuleId() %>">
	<button type="submit">Edit Rule</button>
	<% } %>
</form>
</div>
<script>
document.querySelector("form").addEventListener("submit", function(e) {
    const statusLimit = parseInt(document.getElementById("statusLimit").value);
    const reviewers = document.querySelectorAll("input[name='reviewers']:checked");

    if (reviewers.length < statusLimit) {
        alert("Please select at least " + statusLimit + " reviewers.");
        e.preventDefault();
    }
});


</script>


	
</body>
</html>