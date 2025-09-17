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

<h2>Create New Rule</h2>

<%
    // Teachers list passed from servlet
    List<UserInfo> teachers = (List<UserInfo>) request.getAttribute("teachers");
%>

<form action="CreateRuleServlet" method="post">

    <!-- Priority -->
    <label>Priority:</label>
    <input type="number" name="priority" required><br><br>

    <!-- Status Limit -->
    <label>Status Limit:</label>
    <input type="number" id="statusLimit" name="status_limit" min="0" max="<%= teachers.size() %>" required>
    <br><br>

    <!-- Reviewers -->
    <label>Reviewers (select at least the status limit):</label><br>
    <% for (UserInfo t : teachers) { %>
        <input type="checkbox" name="reviewers" value="<%= t.getRollNo() %>"> <%= t.getName() %><br>
    <% } %>
    <br>

    <!-- Executor (must be exactly one) -->
    <label>Executor:</label><br>
    <select name="executer" required>
        <option value="">-- Select Executor --</option>
        <% for (UserInfo t : teachers) { %>
            <option value="<%= t.getRollNo() %>"><%= t.getRollNo() %></option>
        <% } %>
    </select>
    <br><br>
	
	<h3>Conditions</h3>
	<div id="conditions">
    <div class="condition">
        <label>Attribute:</label>
        <select name="attribute" onchange="loadOperators(this)" required>
            <option value="">--Select Attribute--</option>
            <% 
                List<String> attributes = (List<String>) request.getAttribute("attributes");
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
		</div>
	</div>


	
    <button type="submit">Create Rule</button>
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







function lockCondition(div) {
    // make inputs readonly
    div.querySelectorAll("input, select").forEach(el => {
        if (el.name !== "logic_op") {
            el.setAttribute("readonly", true);
            el.setAttribute("disabled", true);
        } else {
            el.setAttribute("disabled", true); // lock AND/OR too
        }
    });
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

</script>


	
</body>
</html>