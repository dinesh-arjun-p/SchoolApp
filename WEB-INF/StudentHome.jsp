<%@ page import="java.util.List" %>
<%@ page import="com.school.model.RequestAccess" %>
<%@ page import="com.school.model.Notification" %>


<form action="logout">
    <input type="submit" value="logout">
</form>
<br>
<h2>Welcome Student, <%= session.getAttribute("uname") %></h2>

<!-- Request Form -->
<form action="requestAccess" method="post">
    Request Date: <input type="date" name="request_date" required><br>
    Department: <input type="text" name="department" required><br>
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
    Your request for <b><%= n.getDepartment() %></b> on <b><%= n.getRequest_date() %></b> has been 
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



<!-- Show Past Requests -->
<h3>Your Requests</h3>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Date</th>
        <th>Department</th>
        <th>Status</th>
        <th>Reviewed By</th>
    </tr>
    <%
        List<RequestAccess> requests = (List<RequestAccess>) request.getAttribute("requests");
        if (requests != null && !requests.isEmpty()) {
            for (RequestAccess r : requests) {
    %>
        <tr>
            <td><%= r.getRequestId() %></td>
            <td><%= r.getRequestDate() %></td>
            <td><%= r.getDepartment() %></td>
            <td><%= r.getStatus() %></td>
            <td><%= r.getReviewedBy() == null ? "Pending" : r.getReviewedBy() %></td>
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