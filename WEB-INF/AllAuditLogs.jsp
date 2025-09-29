

<h2>All Audit Logs</h2>
<div style="width:100%">
<form action="ClearAuditLogs" method="post"  style="display:inline-block;float:left">
                <button type="submit">clear</button>
            </form>
<form method="post"  style="display:inline-block;float:right">
	<button type="submit" formaction="ExportAuditLogs">Export As Pdf</button>
    <button type="submit" formaction="ExportAuditLogsAsExcel">Export As Excel</button>
</form>
</div>
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
<div style="display:flex; align-items:center; width:100%; position:relative; ">
<%if ((Integer)request.getAttribute("pageNumber") > 1){%>
    <a href="Home.jsp?page=${pageNumber - 1}" style="position:absolute; left:0;" >Previous</a>
<% }%>

<form action="Home.jsp"style="margin:5 auto; text-align:center;">
Page 
	<input name="page" value="${pageNumber}" style="width:50px;border:0px"/>
of ${totalNoOfPages}
</form>
<%if ((Integer)request.getAttribute("pageNumber") < (Integer)request.getAttribute("totalNoOfPages")){ %>
    <a href="Home.jsp?page=${pageNumber + 1}" style="position:absolute; right:0" >Next</a>
<% } %>
</div>