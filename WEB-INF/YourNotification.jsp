<h3>Notifications</h3>

<style>
    body {
        font-family: Arial, sans-serif;
        background-color: #f9f9f9;
        padding: 20px;
    }

    .notification {
        background-color: #fff;
        border: 1px solid #ddd;
        border-radius: 6px;
        padding: 12px 16px;
        margin-bottom: 10px;
        box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .notification p {
        margin: 0;
        font-size: 14px;
        color: #333;
    }

    .notification b {
        color: #555;
    }

    .notification form button {
        background-color: #ff4d4d;
        color: white;
        border: none;
        border-radius: 50%;
        width: 22px;
        height: 22px;
        line-height: 20px;
        text-align: center;
        cursor: pointer;
        font-weight: bold;
        font-size: 12px;
        padding: 0;
    }

    .notification form button:hover {
        background-color: #e60000;
    }

    .no-notifications {
        font-style: italic;
        color: #888;
    }
</style>

<%
    List<Notification> notifications = (List<Notification>) request.getAttribute("notifications");
    if (notifications != null && !notifications.isEmpty()) {
        for (Notification n : notifications) {
%>
    <div class="notification">
        <p>
            Your request for <b><%= n.getAction() %></b> on <b><%= n.getRequestDate() %></b> has been 
            <b><%= n.getStatus() %></b> by <b><%= n.getReviewedBy() %></b>.
        </p>
        <form action="DeleteNotification" method="post">
            <input type="hidden" name="notificationId" value="<%= n.getNotificationId() %>">
            <button type="submit">x</button>
        </form>
    </div>
<%
        }
    } else {
%>
    <p class="no-notifications">No notifications.</p>
<%
    }
%>
