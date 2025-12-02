const fs = require("fs");
const path = require("path");
const { JSDOM } = require("jsdom");

//
// Helper: Load budget.js inside a jsdom environment
//
function loadBudgetJsDom() {
  const html = `
    <!DOCTYPE html>
    <html>
      <body>
        <div id="content"></div>

        <input id="itemCategory">
        <input id="itemAmount">
        <select id="itemType"></select>

        <table id="budgetTable"><tr></tr></table>

        <div id="summaryBox"></div>

        <input id="ccBalance">
        <input id="ccAPR">
        <input id="ccPayment">
        <div id="ccResult"></div>

        <input id="loanPrincipal">
        <input id="loanAPR">
        <input id="loanYears">
        <div id="loanResult"></div>
      </body>
    </html>
  `;

  const dom = new JSDOM(html, {
    url: "http://localhost",
    runScripts: "outside-only"
  });

  // REQUIRED — budget.js checks this and will crash without it
  dom.window.currentPage = "budget";

  // localStorage mock handled by jsdom automatically

  // allow tests to intercept alerts
  dom.window.alert = () => {};

  // mock fetch so tests control responses
  dom.window.fetch = () =>
    Promise.resolve({ json: async () => ({}) });

  // Load budget.js
  const scriptPath = path.join(__dirname, "..", "js", "budget.js");
  const scriptContent = fs.readFileSync(scriptPath, "utf-8");
  dom.window.eval(scriptContent);

  return dom;
}

//
// TESTS
//

describe("budget.js behavior", () => {
  test("initializes UI into #content", () => {
    const dom = loadBudgetJsDom();
    const { document, loadBudgetTool } = dom.window;

    loadBudgetTool(); // function inside budget.js

    expect(document.querySelector("h1").textContent).toBe("Budget Tool");
    expect(document.getElementById("budgetTable")).not.toBeNull();
  });

  test("addRowToTable adds a table row", () => {
    const dom = loadBudgetJsDom();
    const { document, loadBudgetTool, addRowToTable } = dom.window;

    loadBudgetTool();

    const sample = { id: "1", type: "income", category: "Job", amount: 5000 };
    addRowToTable(sample);

    const rows = document.querySelectorAll("#budgetTable tr");
    expect(rows.length).toBe(2);
  });

  test("addItem rejects invalid input", async () => {
    const dom = loadBudgetJsDom();
    const { document, loadBudgetTool, addItem } = dom.window;

    loadBudgetTool();

    document.getElementById("itemCategory").value = "";
    document.getElementById("itemAmount").value = "abc";

    const alertSpy = jest.spyOn(dom.window, "alert");

    await addItem();

    expect(alertSpy).toHaveBeenCalled();
  });

  test("addItem performs POST fetch", async () => {
    const dom = loadBudgetJsDom();
    const { document, loadBudgetTool, addItem } = dom.window;

    loadBudgetTool();

    const mockFetch = jest.fn().mockResolvedValue({
      json: async () => []
    });
    dom.window.fetch = mockFetch;

    document.getElementById("itemCategory").value = "Food";
    document.getElementById("itemAmount").value = "10";
    document.getElementById("itemType").value = "expense";

    await addItem();

    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining("/add-item"),
      expect.any(Object)
    );
  });

  test("deleteItem performs DELETE fetch", async () => {
    const dom = loadBudgetJsDom();
    const { loadBudgetTool, deleteItem } = dom.window;

    loadBudgetTool();

    const mockFetch = jest.fn().mockResolvedValue({});
    dom.window.fetch = mockFetch;

    await deleteItem("123");

    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining("/delete/123"),
      expect.objectContaining({ method: "DELETE" })
    );
  });

  test("updateSummary displays correct text", async () => {
    const dom = loadBudgetJsDom();
    const { document, loadBudgetTool, updateSummary } = dom.window;

    loadBudgetTool();

    dom.window.fetch = jest.fn().mockResolvedValue({
      json: async () => ({ income: 3000, expenses: 1000, net: 2000 })
    });

    await updateSummary();

    const html = document.getElementById("summaryBox").innerHTML;
    expect(html).toContain("Income: $3000.00");
    expect(html).toContain("Expenses: $1000.00");
    expect(html).toContain("Net: $2000.00");
  });

  test("calcCreditCard shows message when payoff impossible", async () => {
    const dom = loadBudgetJsDom();
    const { document, loadBudgetTool, calcCreditCard } = dom.window;

    loadBudgetTool();

    document.getElementById("ccBalance").value = "1000";
    document.getElementById("ccAPR").value = "20";
    document.getElementById("ccPayment").value = "0";

    dom.window.fetch = jest.fn().mockResolvedValue({
      json: async () => ({ payoffPossible: false })
    });

    await calcCreditCard();

    expect(document.getElementById("ccResult").textContent)
      .toContain("Payment too small");
  });

  test("calcLoan formats monthly payment", async () => {
    const dom = loadBudgetJsDom();
    const { document, loadBudgetTool, calcLoan } = dom.window;

    loadBudgetTool();

    document.getElementById("loanPrincipal").value = "5000";
    document.getElementById("loanAPR").value = "5";
    document.getElementById("loanYears").value = "10";

    dom.window.fetch = jest.fn().mockResolvedValue({
      json: async () => ({ monthlyPayment: 53.34 })
    });

    await calcLoan();

    expect(document.getElementById("loanResult").textContent)
      .toBe("Monthly Payment: $53.34");
  });

  test("exportCSV sets window.location.href", () => {
    const dom = loadBudgetJsDom();
    const { exportCSV } = dom.window;

    exportCSV();

    expect(dom.window.location.href).toContain("/export");
  });

  test("loadItems inserts fetched rows", async () => {
    const dom = loadBudgetJsDom();
    const { document, loadBudgetTool, loadItems } = dom.window;

    loadBudgetTool();

    dom.window.fetch = jest.fn().mockResolvedValue({
      json: async () => [
        { id: "1", type: "income", category: "Job", amount: 3000 }
      ]
    });

    await loadItems();

    const rows = document.querySelectorAll("#budgetTable tr");
    expect(rows.length).toBe(2);
  });
});

