# GradGoals Budget Tool

A single-file Java program that helps college students easily calculate and track their monthly budget.
The tool collects income and expense information, estimates credit card payoff time, calculates student loan payments, and exports everything to a CSV file that opens cleanly in Microsoft Excel — no extra libraries or setup required.

---

## Overview

**Filename:** `BudgetTool.java`
**Language:** Java 17+
**Dependencies:** None (uses only standard Java libraries)
**Output:** CSV file (`budget_report_YYYYMMDD_HHMMSS.csv`)

---

## Features

* Interactive input for income, expenses, credit card, and student loan details
* Calculates net monthly balance (income − expenses)
* Simulates credit card payoff based on balance, APR, and monthly payment
* Computes student loan monthly payment using amortization formulas
* Automatically exports results to an Excel-friendly CSV file
* Fully standalone — no Maven, Gradle, or external packages

---

## How It Works

### 1. Income and Expenses

Prompts the user for total monthly income and a list of expense items.
Calculates the total monthly expenses and the resulting net income.

### 2. Credit Card Payoff

Simulates the number of months and total interest required to pay off a credit card balance, given an APR and monthly payment.
If the payment is too low to reduce the balance, it warns the user.

### 3. Student Loan Payment

Estimates the monthly payment for a student loan based on principal, APR, and repayment term in years using a standard amortization formula.

### 4. CSV Export

Writes all inputs and calculated results into a timestamped CSV file such as:

```
budget_report_20251103_104512.csv
```

Each section (Income, Expenses, Credit Card, Student Loan) is labeled for readability in Excel.

---

## How to Run

### Compile

Open a terminal in the same folder as `BudgetTool.java`:

```bash
javac BudgetTool.java
```

### Run interactively

```bash
java BudgetTool
```

You’ll be prompted for:

* Monthly income
* Number of expenses
* Expense names and amounts
* Credit card balance, APR, and payment
* Student loan principal, APR, and term

---

## Running Tests

See [`tests/README_tests.md`](tests/README_tests.md) for detailed test scenarios and expected outputs.

---

## Example Output (Terminal)

```
========== SUMMARY ==========
Income: $2000.00 /mo
Total Expenses: $1200.00 /mo
Net (Income - Expenses): $800.00 /mo

Credit Card:
  Balance: $0.00 | APR: 18.00% | Payment: $50.00
  Months to payoff: 0 | Total interest: $0.00

Student Loan:
  Principal: $0.00 | APR: 5.00% | Term: 10 years
  Estimated monthly payment: $0.00
```

**CSV exported:**

```
budget_report_20251103_170920.csv
```

---

## Technical Notes

* **Functions:**

  * `simulateCreditCardPayoff()` – models month-by-month credit card reduction
  * `studentLoanMonthlyPayment()` – amortization formula for loans
  * `exportCsv()` – writes structured CSV output
  * `round2()` – helper to round decimals to 2 digits

* **Error Handling:**

  * Validates non-negative numeric inputs
  * Rejects empty strings for names
  * Handles impossible payoff cases gracefully

---

## Future Enhancements

* Add graphical visualization of expenses
* Support `.xlsx` file export via Apache POI
* Integrate into the GradGoals web platform as a budgeting microservice
* Include categories for common college expenses (Rent, Food, Tuition, etc.)
* Allow saving and reloading past budgets

---

## Author

**Kathryn Sullivan**
Finance and Computer Science Major — Tulane University
Project: GradGoals — Student Budgeting & Financial Literacy Tool
