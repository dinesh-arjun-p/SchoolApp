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

        List<RequestAccess> reqs = (List<RequestAccess>) session.getAttribute("reviewRequests");

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

    <br>
	<%

        List<RequestAccess> exes = (List<RequestAccess>) session.getAttribute("executeRequests");

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
</body>
</html>
