
	<h3>Conditions</h3>
	<div id="conditions">
		<% 
		List<String> attributes=(List<String>)request.getAttribute("attributes");
	if (editRule != null && editRule.getCondition() != null) {
		for (int i = 0; i < editRule.getCondition().size(); i++) {
			Condition c = editRule.getCondition().get(i); // assuming Condition object
	%>
	<div class="condition">
		<label>Attribute:</label>
		<select name="attribute" onchange="loadOperators(this)" required>
			<option value="">--Select Attribute--</option>
			<% for(String attr : attributes) { 
				   String selected = attr.equals(c.getAttribute()) ? "selected" : "";
			%>
				<option value="<%= attr %>" <%= selected %>><%= attr %></option>
			<% } %>
		</select><br>

		<label>Operator:</label>
		<select name="operator" required>
			<option value="">--Select Operator--</option>
			<option value="is" <%= "is".equals(c.getOperator()) ? "selected" : "" %>>Is</option>
			<option value="is not" <%= "is not".equals(c.getOperator()) ? "selected" : "" %>>Is Not</option>
			<option value="contains" <%= "contains".equals(c.getOperator()) ? "selected" : "" %>>Contains</option>
		</select><br>
		
		
		<div style="display:none;">
			<% Map<String, List<String>> values = (Map<String, List<String>>) request.getAttribute("values"); %>
			<% for (String attr : values.keySet()) { %>
				<datalist id="vals-<%= attr %>">
					<% for (String v : values.get(attr)) { %>
						<option value="<%= v %>">
					<% } %>
				</datalist>
			<% } %>
		</div>
		
		<label>Value:</label>
		<input type="text" name="value" value="<%= c.getValue() %>"  required><br>

		<label>Logic Op:</label>
		<select name="logic_op" onchange="checkLogicOp(this)" >
			<option value="">--None--</option>
			<option value="AND" <%= "AND".equals(c.getLogicOp()) ? "selected" : "" %>>AND</option>
			<option value="OR" <%= "OR".equals(c.getLogicOp()) ? "selected" : "" %>>OR</option>
		</select><br><br>

		<button type="button" onclick="deleteCondition(this)">Delete</button><br><br>
	</div>
	<% 
		}
	} else { 
	%>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    <div class="condition">
        <label>Attribute:</label>
        <select name="attribute" onchange="loadOperators(this)" required>
            <option value="">--Select Attribute--</option>
            <% 
                for(String attr : attributes) {
            %>
                <option value="<%=attr%>"><%=attr%></option>
            <% } %>
        </select><br>
 
        <label>Operator:</label>
        <select name="operator" required>
            <option value="">--Select Operator--</option>
			<option value="is ">Is</option>
			<option value="is not">Is Not</option>
			<option value="contains">Contains</option>
        </select><br>

      <div style="display:none;">
			<% Map<String, List<String>> values = (Map<String, List<String>>) request.getAttribute("values"); %>
			<% for (String attr : values.keySet()) { %>
				<datalist id="vals-<%= attr %>">
					<% for (String v : values.get(attr)) { %>
						<option value="<%= v %>">
					<% } %>
				</datalist>
			<% } %>
		</div>

			<label>Value:</label>
			<input type="text" name="value" id="valueInput" required>



        <label>Logic Op:</label>
			<select name="logic_op" onchange="checkLogicOp(this)">
				<option value="">--None--</option>
				<option value="AND">AND</option>
				<option value="OR">OR</option>
			</select><br><br>
			
			<button type="button" onclick="deleteCondition(this)">Delete</button>
			<br>
		</div>
		<% } %>
	</div>


<script>

function checkLogicOp(selectElement) {
    const conditionDiv = selectElement.closest(".condition");
    const attribute = conditionDiv.querySelector("select[name='attribute']").value.trim();
    const operator = conditionDiv.querySelector("select[name='operator']").value;
    const value = conditionDiv.querySelector("input[name='value']").value.trim();

    if ((selectElement.value === "AND" || selectElement.value === "OR") &&
        (attribute === "" || operator === "" || value === "")) {
        alert("Please fill Attribute, Operator, and Value before choosing AND/OR.");
        selectElement.value = ""; // reset selection
        return;
    }

  
    const conditionsContainer = document.getElementById("conditions");
    if ((selectElement.value === "AND" || selectElement.value === "OR") &&
        conditionDiv === conditionsContainer.lastElementChild) {
        addCondition();
    }
}

window.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll(".condition").forEach(function(cond) {
        const attrSelect = cond.querySelector("select[name='attribute']");
        const valInput = cond.querySelector("input[name='value']");
        const selectedAttr = attrSelect.value;

        if (selectedAttr && document.getElementById("vals-" + selectedAttr)) {
            valInput.setAttribute("list", "vals-" + selectedAttr);
        } else {
            valInput.removeAttribute("list");
        }
    });
});


function loadOperators(attrSelect) {
    const attr = attrSelect.value;
    const conditionDiv = attrSelect.closest(".condition");
	const valInput = conditionDiv.querySelector("input[name='value']");
	valInput.value="";
    if (document.getElementById("vals-" + attr)) {
        valInput.setAttribute("list", "vals-" + attr);
    } else {
        valInput.removeAttribute("list"); // free input if no list
    }
}


function addCondition() {
	 const conditionsContainer = document.getElementById("conditions");
	   const lastCondition = conditionsContainer.lastElementChild;
    const newCondition = lastCondition.cloneNode(true); 
    newCondition.querySelectorAll("input, select").forEach(el => {
        if (el.tagName === "SELECT") {
            el.selectedIndex = 0; // reset to first option
        } else {
            el.value = "";
        }
    });

    document.getElementById("conditions").appendChild(newCondition);
	const logicSelect = lastCondition.querySelector("select[name='logic_op']");
	if (logicSelect.options[0].value === "") {
               logicSelect.options[0].disabled = true;
    }
}

function deleteCondition(button) {
    const conditionDiv = button.closest(".condition");
    const conditionsContainer = document.getElementById("conditions");
	 
    
	if (conditionsContainer.children.length === 1) {
		conditionDiv.querySelectorAll("input, select").forEach(el => {
			if (el.tagName === "SELECT") {
				el.selectedIndex = 0; // reset to first option
			} else {
				el.value = "";
			}
		});
		return ;
    }
   
   conditionsContainer.removeChild(conditionDiv);
   const lastCondition = conditionsContainer.lastElementChild;
    if (lastCondition) {
        const logicSelect = lastCondition.querySelector("select[name='logic_op']");
        if (logicSelect) {
            logicSelect.selectedIndex = 0; 
			 logicSelect.options[0].disabled = false;

        }
		
    }

    
}

</script>