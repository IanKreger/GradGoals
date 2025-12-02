/**
 * @jest-environment jsdom
 */

global.fetch = jest.fn();

import { JSDOM } from "jsdom";

describe("Budget Tool UI Tests", () => {

  // Helper: create base DOM structure
  function loadBaseDom() {
    document.body.innerHTML = `
      <div id="content"></div>
    `;
  }

  beforeEach(() => {
    jest.clearAllMocks();
    loadBaseDom();
  });

  // 1. Test that budget UI inserts correctly
  test("inserts Budget Tool UI into the DOM", () => {
    const htmlFn = require("./budgetTool"); // assume your script exports a function
    htmlFn();  // populates #content

    expect(document.querySelector("h1").textContent).toBe("Budget Tool");
    expect(document.getElementById("budgetTable")).not.toBeNull();
  });

  // 2. addRowToTable inserts a table row
  test("addRowToTable inserts row into table", () => {
    require("./budgetTool")();

    const addRowToTable = global.addRowToTable;

    const item = { type: "income", category: "Job", amount: 3000, id: "1" };
    addRowToTable(item);

    const rows = document.querySelectorAll("#budgetTable tr");
    // row count includes header row + inserted row
    expect(rows.length).toBe(2);
  });

  // 3. addItem alerts on invalid input
  test("addItem rejects invalid input", async () => {
    require("./budgetTool")();

    document.getElementById("itemCategory").value = "";
    document.getElementById("itemAmount").value = "notANumber";

    const alertMock = jest.spyOn(window, "alert").mockImplementation(() => {});
    await global.addItem();

    expect(alertMock).toHaveBeenCalled();
  });

  // 4. addItem sends POST request
  test("addItem sends POST fetch request", async () => {
    require("./budgetTool")();

    fetch.mockResolvedValueOnce({ json: async () => [] });

    document.getElementById("itemCategory").value = "Food";
    document.getElementById("itemAmount").value = "10";
    document.getElementById("itemType").value = "expense";

    await global.addItem();

    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining("/add-item"),
      expect.any(Object)
    );
  });

  // 5. deleteItem calls delete endpoint
  test("deleteItem calls DELETE fetch", async () => {
    require("./budgetTool")();

    fetch.mockResolvedValue({});

    await global.deleteItem("123");

    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining("/delete/123"),
      expect.objectContaining({ method: "DELETE" })
    );
  });

  // 6. updateSummary formats values correctly
  test("updateSummary displays formatted summary", async () => {
    require("./budgetTool")();

    fetch.mockResolvedValueOnce({
      json: async () => ({ income: 3000, expenses: 1000, net: 2000 })
    });

    await global.updateSummary();

    const text = document.getElementById("summaryBox").innerHTML;
    expect(text).toContain("Income: $3000.00");
    expect(text).toContain("Expenses: $1000.00");
    expect(text).toContain("Net: $2000.00");
  });

  // 7. calcCreditCard shows failure message
  test("calcCreditCard displays payoff impossible message", async () => {
    require("./budgetTool")();

    // setup DOM fields
    document.getElementById("ccBalance").value = "1000";
    document.getElementById("ccAPR").value = "20";
    document.getElementById("ccPayment").value = "0";

    fetch.mockResolvedValueOnce({
      json: async () => ({ payoffPossible: false })
    });

    await global.calcCreditCard();

    expect(document.getElementById("ccResult").textContent)
      .toContain("Payment too small");
  });

  // 8. calcLoan formats loan monthly payment
  test("calcLoan formats result correctly", async () => {
    require("./budgetTool")();

    document.getElementById("loanPrincipal").value = "5000";
    document.getElementById("loanAPR").value = "5";
    document.getElementById("loanYears").value = "10";

    fetch.mockResolvedValueOnce({
      json: async () => ({ monthlyPayment: 53.34 })
    });

    await global.calcLoan();

    expect(document.getElementById("loanResult").textContent)
      .toBe("Monthly Payment: $53.34");
  });

  // 9. exportCSV triggers navigation to API
  test("exportCSV sets window.location.href", () => {
    require("./budgetTool")();

    global.exportCSV();

    expect(window.location.href).toContain("/export");
  });

  // 10. loadItems populates rows from fetch
  test("loadItems populates rows returned from server", async () => {
    require("./budgetTool")();

    fetch.mockResolvedValueOnce({
      json: async () => [
        { id: "1", type: "income", category: "Job", amount: 3000 }
      ]
    });

    await global.loadItems();

    const rows = document.querySelectorAll("#budgetTable tr");
    expect(rows.length).toBe(2); // header + item
  });
});
