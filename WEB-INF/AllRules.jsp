<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
        List<Rule> rules = (List<Rule>) request.getAttribute("rules");
       
    %>
	<%  if (rules == null||rules.isEmpty()) {%>
	<p>No Rules</p>
	<% if ("Admin".equalsIgnoreCase(role) ) { %>
    <form action="CreateRulePageServlet" method="post" style="text-align:center;">
        <button type="submit">➕ Create Rule</button>
    </form>
<% } %>
	
	<%}
		else{ %>
         
<h2>All Rules</h2>
<% if ("Admin".equalsIgnoreCase(role) ) { %>
    <form action="CreateRulePageServlet" method="post" style="text-align:center;">
        <button type="submit">➕ Create Rule</button>
    </form>
<% } %>
 <%  for (Rule r : rules) { %>
<table style="border: 1px solid black;">
    <tr>
        <th>Rule Id</th>
        <th>Condition</th>
		<th>Reviewers</th>
		<th>Executer</th>
        <th>No of Review Needed</th>
        <th>Priority</th>
		<th>Action</th>
    </tr>
    
    <tr>
        <td><%= r.getRuleId() %></td>
        <td><% for(Condition c:r.getCondition()){out.print(c);%><br><% }%></td>
		<td><% for(ReviewerInfo c:r.getReviewers()){%><div><%out.print(c); if(c.getActiveStatus().equals("Deleted"))out.print("(Deleted)");%><div><br><% }%></td>
		<td>
		<div><%= r.getExecuter()%>
		<% if(r.getActiveStatus().equals("Deleted")){out.print("(Deleted)");}%><div>
		</td>
        <td><%= r.getStatusLimit() %></td>
		<td><%= r.getPriority() %></td>
        <td>
			<form action="CreateRulePageServlet" method="post">
                <input type="hidden" name="editRule" value="<%= r.getRuleId() %>">
                <button type="submit">Edit</button>
            </form>
            <form action="deleteRule" method="post">
                <input type="hidden" name="ruleId" value="<%= r.getRuleId() %>">
                <button type="submit">Delete</button>
            </form>
        </td>
    </tr>
    <%
            }
        }
    %>
	
</table>
