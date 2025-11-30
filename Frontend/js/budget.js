const API = "http://localhost:8080/budget";

// ---------------------------
// Load items at startup
// ---------------------------
document.addEventListener("DOMContentLoaded", () => {
    loadItems();
    updateSummary();

    document.getElementById("addItemBtn").onclick = addItem;
    document.getElementById("calcCC").onclick = calcCreditCard;
    document.getElementById("calcLoan").onclick = calcLoan;
    document.getElementById("exportCSV").onclick = exportCSV;
});

// ---------------------------
// GET all items
// ---------------------------
async function loadItems() {
    const table = document.getElementById("budgetTable");

    // Clear all rows except header
    table.innerHTML = `
      <tr>
        <th>Type</th>
        <th>Description</th>
        <th>Amount</th>
        <th>Delete</th>
      </tr>
    `;

    const res = await fetch(`${API}/items`);
    const items = await res.json();

    items.forEach(addRowToTable);
}

// ---------------------------
// Add row to DOM
// ---------------------------
function addRowToTable(item) {
    const table = document.getElementById("budgetTable");
    const row = table.insertRow();

    row.insertCell().textContent = item.type;
    row.insertCell().textContent = item.category;
    row.insertCell().textContent = "$" + item.amount.toFixed(2);

    const deleteCell = row.insertCell();
    const delBtn = document.createElement("button");
    delBtn.textContent = "X";
    delBtn.onclick = () => deleteItem(item.id);
    deleteCell.appendChild(delBtn);
}

// ---------------------------
// POST add item
// ---------------------------
async function addItem() {
    const type = document.getElementById("itemType").value;
    const category = document.getElementById("itemCategory").value.trim();
    const amount = parseFloat(document.getElementById("itemAmount").value);

    if (!category || isNaN(amount)) {
        alert("Enter valid description and amount");
        return;
    }

    await fetch(`${API}/add-item`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ type, category, amount })
    });

    loadItems();
    updateSummary();

    document.getElementById("itemCategory").value = "";
    document.getElementById("itemAmount").value = "";
}

// ---------------------------
// DELETE item
// ---------------------------
async function deleteItem(id) {
    await fetch(`${API}/delete/${id}`, { method: "DELETE" });

    loadItems();
    updateSummary();
}

// ---------------------------
// SUMMARY
// ---------------------------
async function updateSummary() {
    const res = await fetch(`${API}/summary`);
    const s = await res.json();

    document.getElementById("summaryBox").innerHTML = `
      Income: $${s.income.toFixed(2)}<br>
      Expenses: $${s.expenses.toFixed(2)}<br>
      <b>Net: $${s.net.toFixed(2)}</b>
    `;
}

// ---------------------------
// CREDIT CARD CALCULATOR
// ---------------------------
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

// ---------------------------
// STUDENT LOAN CALCULATOR
// ---------------------------
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

// ---------------------------
// EXPORT CSV
// ---------------------------
function exportCSV() {
    window.location.href = `${API}/export`;
}
