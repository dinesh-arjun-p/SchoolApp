<h3>Your Requests</h3>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Date</th>
        <th>Action</th>
		<th>Action Value</th>
		<% if(((String)session.getAttribute("role")).equals("Teacher")){ %>
		<th>Action For</th>
		<% }%>
        <th>Status</th>
        <th>Assigned To</th>
		
    </tr>
    <%
        List<RequestAccess> requests = (List<RequestAccess>) request.getAttribute("requests");
        if (requests != null && !requests.isEmpty()) {
            for (RequestAccess r : requests) {
    %>
        <tr>
            <td><%= r.getRequestId() %></td>
            <td><%= r.getRequestDate() %></td>
            <td><%= r.getAction() %></td>
			<td><%= r.getActionValue() %></td>
			<% if(((String)session.getAttribute("role")).equals("Teacher")){ %>
			<td><%= r.getActionFor() %></td>
			<% }%>
            <td><% if(r.getState().equals("Executed"))out.print("Executed");
				else if(r.getState().equals("Rejected"))out.print("Rejected");
				else if(r.getRole().equals("Executer")){out.print("Execution Remaining");}
					else {out.print(r.getStatus()+" Reviewed");}%></td>
			
            <td><% for(AssignedTo req:r.getAssignedTo()){ out.print(req); %><br> <% } %></td>
        </tr>
    <%
            }
        } else {
    %>
        <tr><td colspan="5">No requests yet.</td></tr>
    <%
        }
    %>
</table>