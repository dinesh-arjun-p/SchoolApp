 <% List<RequestAccess> exes = (List<RequestAccess>) request.getAttribute("requests");

        if (exes == null || exes.isEmpty()) {
    %>
        <p>No  requests.</p>
    <%
        } else {
    %>
<h2>All requests.</h2>

      
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th>Request ID</th>
                <th>Date</th>
                <th>Department</th>
                
                
				<th>Action Value</th>
				<th>Action For</th>
				<th>Assigned To</th>
				<th>Requested By</th>
				<th>Action</th>
            </tr>
            <%
                for (RequestAccess exe : exes) {
            %>
                <tr>
                    <td><%= exe.getRequestId() %></td>
                    <td><%= exe.getRequestDate() %></td>
                    <td><%= exe.getAction() %></td>
					<td><%= exe.getActionValue() %></td>
					<td><%= exe.getActionFor() %></td>
					 <td><% for(String req:exe.getAssignedTo()) {out.print(req);%><br><%} %></td>
                    <td><%= exe.getRequestedBy() %></td>
					
					
                    <td>
                        <form action="UpdateRequestStatus" method="post" style="display:inline;">
                            <input type="hidden" name="requestId" value="<%= exe.getRequestId() %>">
							<% if (exe.getRole().equals("Executer")){%>
                            <button type="submit" name="action" value="Executed">Execute</button>
							<%  } else{%>
							 <button type="submit" name="action" value="Approved">Review</button>
							<% } %>
                            <button type="submit" name="action" value="Rejected">Reject</button>
                        </form>
                    </td>
                </tr>
            <%
                }
		}
            %>
        </table>
