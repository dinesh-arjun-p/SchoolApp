<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.school.model.*" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Create Rule</title>
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
	<%
	Rule editRule = (Rule) request.getAttribute("editRule");
	if(editRule==null){
	%>

<h2>Create New Rule</h2>
	<% } else { %>
<h2>Edit Rule </h2>
	<% } %>
<%
    // Teachers list passed from servlet
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

	
	<h3>Conditions</h3>
	<div id="conditions">
		<% 
		List<String> attributes=(List<String>)request.getAttribute("attributes");
	if (editRule != null && editRule.getCondition() != null) {
		for (int i = 0; i < editRule.getCondition().size(); i++) {
			Condition c = editRule.getCondition().get(i); // assuming Condition object
	%>
	<div class="condition">
		<label>Attribute:</label>
		<select name="attribute" onchange="loadOperators(this)" required>
			<option value="">--Select Attribute--</option>
			<% for(String attr : attributes) { 
				   String selected = attr.equals(c.getAttribute()) ? "selected" : "";
			%>
				<option value="<%= attr %>" <%= selected %>><%= attr %></option>
			<% } %>
		</select><br>

		<label>Operator:</label>
		<select name="operator" required>
			<option value="">--Select Operator--</option>
			<option value="is" <%= "is".equals(c.getOperator()) ? "selected" : "" %>>Is</option>
			<option value="is not" <%= "is not".equals(c.getOperator()) ? "selected" : "" %>>Is Not</option>
			<option value="contains" <%= "contains".equals(c.getOperator()) ? "selected" : "" %>>Contains</option>
		</select><br>

		<label>Value:</label>
		<input type="text" name="value" value="<%= c.getValue() %>" required><br>

		<label>Logic Op:</label>
		<select name="logic_op" onchange="checkLogicOp(this)">
			<option value="">--None--</option>
			<option value="AND" <%= "AND".equals(c.getLogicOp()) ? "selected" : "" %>>AND</option>
			<option value="OR" <%= "OR".equals(c.getLogicOp()) ? "selected" : "" %>>OR</option>
		</select><br><br>

		<button type="button" onclick="deleteCondition(this)">Delete</button><br><br>
	</div>
	<% 
		}
	} else { 
	%>
    <div class="condition">
        <label>Attribute:</label>
        <select name="attribute" onchange="loadOperators(this)" required>
            <option value="">--Select Attribute--</option>
            <% 
                for(String attr : attributes) {
            %>
                <option value="<%=attr%>"><%=attr%></option>
            <% } %>
        </select><br>
 
        <label>Operator:</label>
        <select name="operator" required>
            <option value="">--Select Operator--</option>
			<option value="is ">Is</option>
			<option value="is not">Is Not</option>
			<option value="contains">Contains</option>
        </select><br>

      
			<label>Value:</label>
			<input type="text" name="value" required><br>


        <label>Logic Op:</label>
			<select name="logic_op" onchange="checkLogicOp(this)">
				<option value="">--None--</option>
				<option value="AND">AND</option>
				<option value="OR">OR</option>
			</select><br><br>
			
			<button type="button" onclick="deleteCondition(this)">Delete</button>
			<br><br>
		</div>
		<% } %>
	</div>


	
	<%
	if(editRule==null){
	%>

    <button type="submit">Create Rule</button>
	<% }else{ %>
	<input type="hidden" name="ruleId" value="<%= editRule.getRuleId() %>">
	<button type="submit">Edit Rule</button>
	<% } %>
</form>

<script>
document.querySelector("form").addEventListener("submit", function(e) {
    const statusLimit = parseInt(document.getElementById("statusLimit").value);
    const reviewers = document.querySelectorAll("input[name='reviewers']:checked");

    if (reviewers.length < statusLimit) {
        alert("Please select at least " + statusLimit + " reviewers.");
        e.preventDefault();
    }
});


function checkLogicOp(selectElement) {
    const conditionDiv = selectElement.closest(".condition");
    const attribute = conditionDiv.querySelector("select[name='attribute']").value.trim();
    const operator = conditionDiv.querySelector("select[name='operator']").value;
    const value = conditionDiv.querySelector("input[name='value']").value.trim();

    if ((selectElement.value === "AND" || selectElement.value === "OR") &&
        (attribute === "" || operator === "" || value === "")) {
        alert("Please fill Attribute, Operator, and Value before choosing AND/OR.");
        selectElement.value = ""; // reset selection
        return;
    }

  
    const conditionsContainer = document.getElementById("conditions");
    if ((selectElement.value === "AND" || selectElement.value === "OR") &&
        conditionDiv === conditionsContainer.lastElementChild) {
        addCondition();
    }
}






function addCondition() {
    const firstCondition = document.querySelector(".condition"); // template
    const newCondition = firstCondition.cloneNode(true); // deep clone
    
    newCondition.querySelectorAll("input, select").forEach(el => {
        if (el.tagName === "SELECT") {
            el.selectedIndex = 0; // reset to first option
        } else {
            el.value = "";
        }
        el.removeAttribute("readonly");
        el.removeAttribute("disabled");
    });

    document.getElementById("conditions").appendChild(newCondition);
}

function deleteCondition(button) {
    const conditionDiv = button.closest(".condition");
    const conditionsContainer = document.getElementById("conditions");

    if (conditionsContainer.children.length > 1) {
        conditionsContainer.removeChild(conditionDiv);
    } else {
        alert("At least one condition is required.");
    }
}


</script>


	
</body>
</html>