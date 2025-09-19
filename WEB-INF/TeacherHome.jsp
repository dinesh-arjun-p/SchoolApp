<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.school.model.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Teacher Home</title>
</head>
<body>
	<%
	String msg = request.getParameter("msg");
    String error = request.getParameter("error");
    if (msg != null) {
	%>
        <p style="color:green;"><%= msg %></p>
	<%
    }
    if (error != null) {
	%>
        <p style="color:red;"><%= error %></p>
	<%
    }
	%>
	<form action="logout">
    <input type="submit" value="logout">
	</form>
<%@ include file="\WEB-INF\Profile.jsp" %>

<form action="requestAccess" method="post">
    <label for="action">Action:</label>
    <select name="action" id="action" required>
        <option value="">-- Select Action --</option>
        <option value="changePhoneNumber">Change Phone Number</option>
        <option value="changeName">Change Name</option>
        <option value="changeClass">Change Class</option>
    </select>
    <br><br>

    <% 
       List<UserInfo> students = (List<UserInfo>) request.getAttribute("students");
       if ( students != null) { 
    %>
        <label for="student">Select Student:</label>
        <select name="action_for" id="student">
			<option value="">-- Select Action --</option>
            <% for (UserInfo s : students) { %>
                <option value="<%=s.getRollNo()%>"><%=s%></option>
            <% } %>
        </select>
        <br><br>
    <% } %>



    <!-- Fields for new values -->
    <input type="text" name="action_value"><br>
    <button type="submit">Request</button>
	</form>

	
	
	<br>
<h3>Notifications</h3>
<%
    List<Notification> notifications = (List<Notification>) request.getAttribute("notifications");
    if (notifications != null && !notifications.isEmpty()) {
        for (Notification n : notifications) {
%>
   <p>
    Your request for <b><%= n.getAction() %></b> on <b><%= n.getRequestDate() %></b> has been 
    <b><%= n.getStatus() %></b> by <b><%= n.getReviewedBy() %></b>.
    
    <form action="DeleteNotification" method="post" style="display:inline; margin-left:5px;">
        <input type="hidden" name="notificationId" value="<%= n.getNotificationId() %>">
        <button type="submit" style="border:none; background:none; cursor:pointer;">x</button>
    </form>
</p>

<%
        }
    } else {
%>
    <p>No notifications.</p>
<%
    }
%>

<br>
<br>

<h3>Your Requests</h3>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Date</th>
        <th>Action</th>
		<th>Action Value</th>
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
            <td><% if(r.getRole().equals("Executer")){out.print("Execution Remaining");}
					else {out.print(r.getStatus()+" Reviewed");}%></td>
			
            <td><% for(String req:r.getAssignedTo()) out.print(req+" "); %></td>
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
<br>
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
</body>
</html>
