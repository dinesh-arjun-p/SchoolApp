

   <%

        List<RequestAccess> reqs = (List<RequestAccess>) request.getAttribute("reviewRequests");

        if (reqs == null || reqs.isEmpty()) {
    %>
        <p>No Review requests.</p>
    <%
        } else {
    %>
		<p>Review requests.</p>
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
                for (RequestAccess req : reqs) {
            %>
                <tr>
                    <td><%= req.getRequestId() %></td>
                    <td><%= req.getRequestDate() %></td>
                    <td><%= req.getAction() %></td>
					<td><%= req.getActionValue() %></td>
					<td><%= req.getActionFor() %></td>
                    <td><%= req.getRequestedBy() %></td>
                    <td>
                        <form action="UpdateRequestStatus" method="post" style="display:inline;">
                            <input type="hidden" name="requestId" value="<%= req.getRequestId() %>">
                            <button type="submit" name="action" value="Approved">Approve</button>
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