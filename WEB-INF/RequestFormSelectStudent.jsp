<tr>
            <td><label for="student">Select Student:</label></td>
            <td>
                <%
                    List<UserInfo> students = (List<UserInfo>) request.getAttribute("students");
                    if (students != null && !students.isEmpty()) {
                %>
                    <select name="action_for" id="student" >
                        <option value="">-- Select Student --</option>
                        <% for (UserInfo s : students) { %>
                            <option value="<%= s.getRollNo() %>"><%= s %></option>
                        <% } %>
                    </select>
                <%
                    } else {
                %>
                    <em>No students available</em>
                <%
                    }
                %>
            </td>
        </tr>