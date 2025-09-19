    <br>
	<%

        List<RequestAccess> exes = (List<RequestAccess>) request.getAttribute("executeRequests");

        if (exes == null || exes.isEmpty()) {
    %>
        <p>No Executable requests.</p>
    <%
        } else {
    %>
		<p>Executable requests.</p>
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th>Request ID</th>
                <th>Date</th>
                <th>Department</th>
				<th>Action Value</th>
				<th>Action For</th>
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
                    <td><%= exe.getRequestedBy() %></td>
                    <td>
                        <form action="UpdateRequestStatus" method="post" style="display:inline;">
                            <input type="hidden" name="requestId" value="<%= exe.getRequestId() %>">
                            <button type="submit" name="action" value="Executed">Execute</button>
                            <button type="submit" name="action" value="Rejected">Reject</button>
                        </form>
                    </td>
                </tr>
            <%
                }
            %>
        </table>
    <%
        }
    %>