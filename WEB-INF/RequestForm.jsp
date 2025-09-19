<% boolean isTeacher = "Teacher".equals((String) session.getAttribute("role")); %>

<form action="requestAccess" method="post">
    <table border="1" cellpadding="8" cellspacing="0" style="border-collapse: collapse; width: 60%; font-family: Arial, sans-serif;">
        <tr>
            <th colspan="2" style="background-color: #f2f2f2; text-align: left;">Request Form</th>
        </tr>
        <tr>
            <td><label for="action">Action:</label></td>
            <td>
                <select name="action" id="action" required>
                    <option value="">-- Select Action --</option>
                    <option value="changePhoneNumber">Change Phone Number</option>
                    <option value="changeName">Change Name</option>
                    <% if (isTeacher) { %>
                        <option value="changeClass">Change Class</option>
                    <% } %>
                </select>
            </td>
        </tr>

        <% if (isTeacher) { %>
        <%@ include file="\WEB-INF\RequestFormSelectStudent.jsp" %>
        <% } %>

        <tr>
            <td><label for="action_value">Value:</label></td>
            <td><input type="text" name="action_value" id="action_value" required></td>
        </tr>

        <tr>
            <td colspan="2" style="text-align: center;">
                <button type="submit">Submit Request</button>
            </td>
        </tr>
    </table>
</form>
