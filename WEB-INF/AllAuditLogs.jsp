
<h2>All Audit Logs</h2>
<form action="ClearAuditLogs" method="post" >
                <button type="submit">clear</button>
            </form>
<table >
    <tr>
        <th>Id</th>
        <th>UserName</th>
        <th>Event</th>
        <th>Reg</th>
		<th>Date</th>
		<th>Time</th>
    </tr>
    <%
        List<Logs> logs = (List<Logs>) request.getAttribute("logs");
        if (logs != null) {
            for (Logs log : logs) {
    %>
    <tr>
        <td><%= log.getId() %></td>
        <td><%= log.getUserName() %></td>
        <td><%= log.getEvent() %></td>
        <td><%= log.getReg() %></td>
		<td><%= log.getDate() %></td>
		<td><%= log.getTime() %></td>
    </tr>
    <%
            }
        }
    %>
</table>