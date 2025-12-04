// Check if the current page URL contains the word "budget"
// This tells the script to run ONLY on the Budget page
if (currentPage.toLowerCase().includes("budget")) {

    // Start by checking whether the user is logged in
    checkAuthAndRender();

    function checkAuthAndRender() {
        // Get logged-in username from localStorage
        const userId = localStorage.getItem('gradGoalsUser');
        
        const warningEl = document.getElementById('login-warning');
        const contentEl = document.getElementById('content');

        // If a username exists, show the Budget Tool
        if (userId && userId.trim() !== "") {
            console.log("Logged in as:", userId);
            
            if (warningEl) warningEl.style.display = 'none';

            if (contentEl) {
                contentEl.style.display = 'block'; 
                // Initialize the Budget Tool with the user's ID
                initializeBudgetTool(contentEl, userId);
            }
        } else {
            // If not logged in, hide content and show warning
            if (warningEl) warningEl.style.display = 'block';
            if (contentEl) contentEl.style.display = 'none';
        }
    }

    // Budget Tool setup (UI + event handlers + API calls)
    function initializeBudgetTool(contentEl, userId) {
        
        console.log("Initializing Budget for User:", userId);

        // Insert all UI elements into the page
        contentEl.innerHTML = `
          <div class="page-header">
            <h1>Budget Tool</h1>
          </div>

          <div class="card">
            <h2>Add Item</h2>
            <div id="addItemForm" class="form-row">
              <select id="itemType">
                <option value="income">Income</option>
                <option value="expense">Expense</option>
              </select>
              <input id="itemCategory" placeholder="Description" />
              <input id="itemAmount" type="number" step="0.01" placeholder="Amount" />
              <button id="addItemBtn" class="btn-primary">Add</button>
            </div>
          </div>

          <div class="card">
            <h2>Budget Table</h2>
            <table id="budgetTable" border="1" cellpadding="8">
              <tr>
                <th>Type</th>
                <th>Description</th>
                <th>Amount</th>
                <th>Delete</th>
              </tr>
            </table>
          </div>

          <div class="card">
            <h2>Summary</h2>
            <p id="summaryBox">Loading...</p>
          </div>

          <div class="card calculator">
            <h2>Credit Card Payoff</h2>
            <div class="form-row">
              <input id="ccBalance" type="number" placeholder="Balance" />
              <input id="ccAPR" type="number" placeholder="APR %" />
              <input id="ccPayment" type="number" placeholder="Monthly Payment" />
              <button id="calcCC" class="btn-primary">Calculate</button>
            </div>
            <p id="ccResult"></p>
          </div>

          <div class="card calculator">
            <h2>Student Loan Monthly Payment</h2>
            <div class="form-row">
              <input id="loanPrincipal" type="number" placeholder="Principal" />
              <input id="loanAPR" type="number" placeholder="APR %" />
              <input id="loanYears" type="number" placeholder="Years" />
              <button id="calcLoan" class="btn-primary">Calculate</button>
            </div>
            <p id="loanResult"></p>
          </div>

          <div class="card">
            <button id="exportCSV" class="btn-primary">Export CSV</button>
          </div>
        `;

        const API = "https://gradgoals-i74s.onrender.com/budget";

        // Load all stored budget items for this user
        async function loadItems() {
            const table = document.getElementById("budgetTable");

            // Reset table header
            table.innerHTML = `
              <tr>
                <th>Type</th>
                <th>Description</th>
                <th>Amount</th>
                <th>Delete</th>
              </tr>
            `;

            // Retrieve items for the logged-in user
            const res = await fetch(`${API}/items?userId=${userId}`);
            const items = await res.json();

            // Add each item to the table
            items.forEach(addRowToTable);
        }

        // Adds a single row to the table
        function addRowToTable(item) {
            const table = document.getElementById("budgetTable");
            const row = table.insertRow();

            row.insertCell().textContent = item.type;
            row.insertCell().textContent = item.category;
            row.insertCell().textContent = "$" + item.amount.toFixed(2);

            // Create delete button
            const deleteCell = row.insertCell();
            const delBtn = document.createElement("button");
            delBtn.textContent = "X";

            // Delete item when clicked
            delBtn.onclick = () => deleteItem(item.id);

            deleteCell.appendChild(delBtn);
        }

        // Add new income/expense item
        async function addItem() {
            const type = document.getElementById("itemType").value;
            const category = document.getElementById("itemCategory").value.trim();
            const amount = parseFloat(document.getElementById("itemAmount").value);

            // Basic validation
            if (!category || isNaN(amount)) {
                alert("Enter valid description and amount");
                return;
            }

            // Send item to the server
            await fetch(`${API}/add-item`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ type, category, amount, userId })
            });

            loadItems();
            updateSummary();

            // Clear inputs
            document.getElementById("itemCategory").value = "";
            document.getElementById("itemAmount").value = "";
        }

        // Delete an item by ID
        async function deleteItem(id) {
            await fetch(`${API}/delete/${id}?userId=${userId}`, { method: "DELETE" });
            loadItems();
            updateSummary();
        }

        // Update summary totals (income, expenses, net)
        async function updateSummary() {
            const res = await fetch(`${API}/summary?userId=${userId}`);
            const s = await res.json();

            document.getElementById("summaryBox").innerHTML = `
              Income: $${s.income.toFixed(2)}<br>
              Expenses: $${s.expenses.toFixed(2)}<br>
              <b>Net: $${s.net.toFixed(2)}</b>
            `;
        }

        // Calculate credit card payoff
        async function calcCreditCard() {
            const balance = parseFloat(document.getElementById("ccBalance").value);
            const apr = parseFloat(document.getElementById("ccAPR").value);
            const payment = parseFloat(document.getElementById("ccPayment").value);

            const res = await fetch(`${API}/credit-card`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ balance, apr, payment })
            });

            const out = await res.json();

            if (!out.payoffPossible) {
                document.getElementById("ccResult").textContent =
                    "Payment too small — balance will never be paid off.";
                return;
            }

            document.getElementById("ccResult").textContent =
                `Months: ${out.months}, Total Interest: $${out.totalInterest.toFixed(2)}`;
        }

        // Calculate student loan monthly payment
        async function calcLoan() {
            const principal = parseFloat(document.getElementById("loanPrincipal").value);
            const apr = parseFloat(document.getElementById("loanAPR").value);
            const years = parseInt(document.getElementById("loanYears").value);

            const res = await fetch(`${API}/student-loan`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ principal, apr, years })
            });

            const out = await res.json();

            document.getElementById("loanResult").textContent =
                `Monthly Payment: $${out.monthlyPayment.toFixed(2)}`;
        }

        // Export the user's CSV data
        function exportCSV() {
            window.location.href = `${API}/export?userId=${userId}`;
        }

        // Attach all event listeners
        document.getElementById("addItemBtn").onclick = addItem;
        document.getElementById("calcCC").onclick = calcCreditCard;
        document.getElementById("calcLoan").onclick = calcLoan;
        document.getElementById("exportCSV").onclick = exportCSV;

        // Load user's existing data immediately
        loadItems();
        updateSummary();
    }
}
