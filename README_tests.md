# BudgetTool Test Cases

This folder contains files (`t1.txt`–`t10.txt`) used to test the BudgetTool program.  
Each file simulates user input that the Java program reads through standard input.

---

## Test Overview

| Test | Scenario | Key Inputs | Expected Results |
|------|-----------|-------------|------------------|
| **t1.txt** | Base case (income + 3 expenses, no debt) | Income $2000; 3 expenses totaling $1200 | Net = $800; Credit Card & Loan = $0 |
| **t2.txt** | Deficit month + small credit card balance | Income $1000; Expenses $1100; CC $500 @ 24%, $510 payment | Net = -$100; CC payoff in 1 month; Loan ≈ $133/mo |
| **t3.txt** | Payment too low (no payoff) | CC $1000 @ 24%, $20 payment | “Payoff not possible” message |
| **t4.txt** | Zero-APR credit card | CC $600 @ 0%, $100 payment | 6 months to payoff, $0 interest |
| **t5.txt** | Decimal handling | Income $1234.56; Expense $34.56 | Correct rounding; CC 1-month payoff |
| **t6.txt** | Many expenses including zero | 4 expenses (one is $0) | Total = $900; Loan = $208.33/mo |
| **t7.txt** | Large student loan, long term | $30,000 @ 7%, 20 yrs | Loan ≈ $232.59/mo |
| **t8.txt** | No expenses; small loan | Income $800; Loan $0 | CC payoff in 4 months |
| **t9.txt** | Rounding test | Expenses sum to exactly $1000 | Net = $1000; Loan ≈ $265.61/mo |
| **t10.txt** | High APR but sufficient payment | CC $1200 @ 36%, $1236 payment | 1-month payoff, $36 interest |

---

### How to Run Tests
From the terminal run ./run_tests.sh