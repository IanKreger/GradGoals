const fs = require('fs');
const path = require('path');
const { JSDOM } = require('jsdom');

//
// Helpers
//

function loadBudgetDom() {
  // Arrange
  const html = `
    <!DOCTYPE html>
    <html>
      <head></head>
      <body>
        <div id="content"></div>
      </body>
    </html>
  `;

  // Act
  const dom = new JSDOM(html, {
    url: "http://localhost",
    runScripts: "outside-only"
  });

  // Load script
  const scriptPath = path.join(__dirname, '..', 'js', 'budget.js');
  const scriptContent = fs.readFileSync(scriptPath, 'utf-8');
  dom.window.eval(scriptContent);

  return dom;
}

//
// Tests for budget.js
//

describe('budgetTool.js behavior', () => {

  test('UI renders main Budget Tool heading', () => {
    // Arrange
    const dom = loadBudgetDom();
    const { document, main } = dom.window;

    // Act
    main();
    const h1 = document.querySelector('h1');

    // Assert
    expect(h1).not.toBeNull();
    expect(h1.textContent).toBe('Budget Tool');
  });

  test('addRowToTable inserts a new table row', () => {
    // Arrange
    const dom = loadBudgetDom();
    const { document, main, addRowToTable } = dom.window;
    main();

    const item = { id: "1", type: "income", category: "Job", amount: 3000 };

    // Act
    addRowToTable(item);
    const rows = document.querySelectorAll('#budgetTable tr');

    // Assert
    expect(rows.length).toBe(2); // header + row
  });

  test('addItem rejects invalid input and triggers alert', async () => {
    // Arrange
    const dom = loadBudgetDom();
    const { document, main, addItem } = dom.window;
    main();

    let alerted = false;
    dom.window.alert = () => (alerted = true);

    document.getElementById('itemCategory').value = "";
    document.getElementById('itemAmount').value = "abc";

    // Act
    await addItem();

    // Assert
    expect(alerted).toBe(true);
  });

  test('addItem sends POST request', async () => {
    // Arrange
    const dom = loadBudgetDom();
    const { document, main, addItem } = dom.window;
    main();

    let called = false;
    dom.window.fetch = (...args) => {
      called = args;
      return Promise.resolve({ json: async () => [] });
    };

    document.getElementById('itemCategory').value = "Food";
    document.getElementById('itemAmount').value = "10";
    document.getElementById('itemType').value = "expense";

    // Act
    await addItem();

    // Assert
    expect(called[0]).toContain('/add-item');
  });

  test('deleteItem sends DELETE request', async () => {
    // Arrange
    const dom = loadBudgetDom();
    const { deleteItem } = dom.window;

    let deleteUsed = false;
    dom.window.fetch = (url, opts) => {
      if (opts.method === "DELETE") deleteUsed = true;
      return Promise.resolve({});
    };

    // Act
    await deleteItem("123");

    // Assert
    expect(deleteUsed).toBe(true);
  });

  test('updateSummary displays formatted summary', async () => {
    // Arrange
    const dom = loadBudgetDom();
    const { document, main, updateSummary } = dom.window;
    main();

    dom.window.fetch = () => Promise.resolve({
      json: async () => ({ income: 3000, expenses: 500, net: 2500 })
    });

    // Act
    await updateSummary();
    const html = document.getElementById('summaryBox').innerHTML;

    // Assert
    expect(html).toContain('$3000.00');
    expect(html).toContain('$500.00');
    expect(html).toContain('$2500.00');
  });

  test('calcCreditCard handles impossible payoff', async () => {
    // Arrange
    const dom = loadBudgetDom();
    const { document, main, calcCreditCard } = dom.window;
    main();

    document.getElementById('ccBalance').value = "1000";
    document.getElementById('ccAPR').value = "20";
    document.getElementById('ccPayment').value = "0";

    dom.window.fetch = () => Promise.resolve({
      json: async () => ({ payoffPossible: false })
    });

    // Act
    await calcCreditCard();
    const out = document.getElementById('ccResult').textContent;

    // Assert
    expect(out).toContain('Payment too small');
  });

  test('calcLoan formats monthly payment', async () => {
    // Arrange
    const dom = loadBudgetDom();
    const { document, main, calcLoan } = dom.window;
    main();

    document.getElementById('loanPrincipal').value = "5000";
    document.getElementById('loanAPR').value = "5";
    document.getElementById('loanYears').value = "10";

    dom.window.fetch = () => Promise.resolve({
      json: async () => ({ monthlyPayment: 53.34 })
    });

    // Act
    await calcLoan();
    const text = document.getElementById('loanResult').textContent;

    // Assert
    expect(text).toBe('Monthly Payment: $53.34');
  });

  test('exportCSV updates location.href', () => {
    // Arrange
    const dom = loadBudgetDom();
    const { exportCSV } = dom.window;

    // Act
    exportCSV();

    // Assert
    expect(dom.window.location.href).toContain('/export');
  });

  test('loadItems populates budget rows', async () => {
    // Arrange
    const dom = loadBudgetDom();
    const { document, main, loadItems } = dom.window;
    main();

    dom.window.fetch = () => Promise.resolve({
      json: async () => [
        { id: "1", type: "income", category: "Job", amount: 3000 }
      ]
    });

    // Act
    await loadItems();

    const rows = document.querySelectorAll('#budgetTable tr');

    // Assert
    expect(rows.length).toBe(2); // header + row
  });

});
