if (currentPage === "Budget.html") {
  console.log("Budget page loaded.");
}

document.addEventListener("DOMContentLoaded", () => {
    console.log("Budget page loaded.");

    loadBudgetItems();

    // Add Row Button
    const addBtn = document.createElement("button");
    addBtn.textContent = "Add Item";
    addBtn.onclick = addRow;
    document.body.appendChild(addBtn);

    // Table
    const table = document.createElement("table");
    table.id = "budgetTable";
    table.innerHTML = `
        <tr>
            <th>Type</th>
            <th>Category</th>
            <th>Amount</th>
            <th>Save</th>
        </tr>
    `;
    document.body.appendChild(table);
});

// Load items from backend
async function loadBudgetItems() {
    try {
        const res = await fetch("http://localhost:8080/budget/items");
        const items = await res.json();

        items.forEach(addLoadedRow);
    } catch (e) {
        console.error("Error loading budget items:", e);
    }
}

function addLoadedRow(item) {
    const table = document.getElementById("budgetTable");
    const row = table.insertRow();

    row.innerHTML = `
        <td>${item.type}</td>
        <td>${item.category}</td>
        <td>${item.amount}</td>
        <td></td>
    `;
}

// Creates a new editable row
function addRow() {
    const table = document.getElementById("budgetTable");
    const row = table.insertRow();

    const typeCell = row.insertCell();
    const catCell = row.insertCell();
    const amountCell = row.insertCell();
    const saveCell = row.insertCell();

    typeCell.innerHTML = `
        <select>
            <option value="income">Income</option>
            <option value="expense">Expense</option>
        </select>
    `;

    catCell.innerHTML = `<input type="text" placeholder="Category">`;
    amountCell.innerHTML = `<input type="number" step="0.01" placeholder="Amount">`;

    const saveBtn = document.createElement("button");
    saveBtn.textContent = "Save";
    saveBtn.onclick = () => saveRow(row);
    saveCell.appendChild(saveBtn);
}

// Sends row data to backend
async function saveRow(row) {
    const type = row.cells[0].querySelector("select").value;
    const category = row.cells[1].querySelector("input").value;
    const amount = parseFloat(row.cells[2].querySelector("input").value);

    const item = { type, category, amount };

    try {
        await fetch("http://localhost:8080/budget/add-item", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(item)
        });

        alert("Saved!");

        // reload the table
        document.getElementById("budgetTable").innerHTML = `
            <tr>
                <th>Type</th>
                <th>Category</th>
                <th>Amount</th>
                <th>Save</th>
            </tr>
        `;
        loadBudgetItems();

    } catch (e) {
        console.error("Save failed:", e);
    }
}
