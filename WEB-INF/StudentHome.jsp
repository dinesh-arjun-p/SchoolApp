<%@ page import="java.util.List" %>
<%@ page import="com.school.model.RequestAccess" %>
<%@ page import="com.school.model.Notification" %>


<form action="logout">
    <input type="submit" value="logout">
</form>
<br>
<h2>Welcome Student, <br>Roll No=<%= session.getAttribute("rollNo") %>
<br> Name=<%= session.getAttribute("uname") %>
<br> Email=<%= session.getAttribute("email") %>
<br> role_name= <%= session.getAttribute("role") %>
<br> phone number= <%= session.getAttribute("phone_number") %>
<br> class= <%= session.getAttribute("class") %></h2>

<!-- Request Form -->
<form action="requestAccess" method="post">
    <label for="action">Action:</label>
    <select name="action" id="action" required>
        <option value="">-- Select Action --</option>
        <option value="changePhoneNumber">Change Phone Number</option>
        <option value="changeName">Change Name</option>
    </select>
    <br><br>

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



<h3>Your Requests</h3>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Date</th>
        <th>Action</th>
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
            <td><% if(r.getRole()=="Executer"){out.print("Execution Remaining");}
					else {out.print(r.getStatus()+" Reviewed");}%></td>
			<% DAO dao=new DAO();dao.getAllReviewer();%>
            <td><%= r.getAssignedTo()%></td>
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