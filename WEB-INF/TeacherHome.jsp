<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.school.model.RequestAccess" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Teacher Home</title>
</head>
<body>
	<form action="logout">
    <input type="submit" value="logout">
	</form>
    <h2>Welcome Teacher,Hello ${teacherName}</h2>

    <%

        List<RequestAccess> reqs = (List<RequestAccess>) session.getAttribute("requests");

        if (reqs == null || reqs.isEmpty()) {
    %>
        <p>No pending requests.</p>
    <%
        } else {
    %>
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th>Request ID</th>
                <th>Date</th>
                <th>Department</th>
                <th>Requested By</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            <%
                for (RequestAccess req : reqs) {
            %>
                <tr>
                    <td><%= req.getRequestId() %></td>
                    <td><%= req.getRequestDate() %></td>
                    <td><%= req.getDepartment() %></td>
                    <td><%= req.getRequestedBy() %></td>
                    <td><%= req.getStatus() %></td>
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
	
<h3>✅ Completed Requests</h3>

<%
    List<RequestAccess> completed = (List<RequestAccess>) session.getAttribute("allViewed");
    if (completed != null && !completed.isEmpty()) {
%>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>Request ID</th>
            <th>Date</th>
            <th>Department</th>
            <th>Requested By</th>
            <th>Status</th>
            <th>Reviewed By</th>
        </tr>
        <%
            for (RequestAccess req : completed) {
        %>
            <tr>
                <td><%= req.getRequestId() %></td>
                <td><%= req.getRequestDate() %></td>
                <td><%= req.getDepartment() %></td>
                <td><%= req.getRequestedBy() %></td>
                <td style="color:<%= "Approved".equalsIgnoreCase(req.getStatus()) ? "green" : "red" %>">
                    <%= req.getStatus() %>
                </td>
                <td><%= req.getReviewedBy() %></td>
            </tr>
        <%
            }
        %>
    </table>
<%
    } else {
%>
    <p>No completed requests yet.</p>
<%
    }
%>

	
    <br>
</body>
</html>
