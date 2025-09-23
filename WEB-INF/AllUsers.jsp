<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<h2>All Users</h2>
<% if ("Admin".equalsIgnoreCase(role) ) { %>
    <form action="createUser.jsp" method="post" style="text-align:center;">
        <button type="submit">➕ Create New User</button>
    </form>
<% } %>
<table style="border: 1px solid black;">
    <tr>
        <th>Roll No</th>
        <th>Name</th>
        <th>Role</th>
		<th>Superior</th>
        <th>Action</th>
    </tr>
    <%
        List<UserInfo> users = (List<UserInfo>) request.getAttribute("users");
        if (users != null) {
            for (UserInfo u : users) {
    %>
    <tr>
        <td><%= u.getRollNo() %></td>
        <td><%= u.getName() %></td>
        <td><%= u.getRole() %></td>
		<td><%= u.getSuperior()==null?"":u.getSuperior() %></td>
        <td>
			<form action="CreateUserPageServlet" method="post">
                <input type="hidden" name="editUser" value="<%= u.getRollNo() %>">
                <button type="submit">Edit</button>
            </form>
            <form action="deleteUser" method="post">
                <input type="hidden" name="rollNo" value="<%= u.getRollNo() %>">
                <button type="submit">Delete</button>
            </form>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>
