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