// BudgetingToolToExcel.java
// Single-file Java program that converts budget + loans + cards into an .xlsx workbook.
// Sheets: Budget, StudentLoans, CreditCards, Summary.
// Formulas included: SUMIF totals, PMT for loans (with optional grace capitalization),
// payoff-months approximation for cards, and a 50/30/20 guideline on Summary.

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Paste this file into your repo, add Apache POI (poi-ooxml) dependency,
 * then run `main` to produce GradGoals_Budget.xlsx in the project folder.
 */
public class BudgetingToolToExcel {

    // ---------- Domain models (kept simple & local to this file) ----------
    public static class BudgetItem {
        public enum Type { INCOME, EXPENSE }
        String id = UUID.randomUUID().toString();
        String category;
        double amount;
        Type type;
        public BudgetItem(String category, double amount, Type type) {
            this.category = category; this.amount = amount; this.type = type;
        }
    }

    public static class LoanInput {
        double principal;          // e.g., 15000
        double annualRatePct;      // e.g., 5.5 for 5.5%
        int termMonths;            // e.g., 120
        int graceMonths;           // optional (0 for none)
        boolean capitalizeDuringGrace; // capitalize interest during grace?
        public LoanInput(double principal, double annualRatePct, int termMonths, int graceMonths, boolean cap) {
            this.principal = principal;
            this.annualRatePct = annualRatePct;
            this.termMonths = termMonths;
            this.graceMonths = graceMonths;
            this.capitalizeDuringGrace = cap;
        }
    }

    public static class CardInput {
        double balance;            // current balance
        double annualRatePct;      // APR, e.g., 24.99
        double monthlyPayment;     // fixed monthly payment
        public CardInput(double balance, double annualRatePct, double monthlyPayment) {
            this.balance = balance;
            this.annualRatePct = annualRatePct;
            this.monthlyPayment = monthlyPayment;
        }
    }

    public static class BudgetPayload {
        List<BudgetItem> items = new ArrayList<>();
        List<LoanInput> loans  = new ArrayList<>();
        List<CardInput> cards  = new ArrayList<>();
        Double emergencyFundTargetMonths = 3.0; // optional, used for future upgrades
        Double savingsRateTargetPct      = 20.0;

        public BudgetPayload addItem(BudgetItem bi){ items.add(bi); return this; }
        public BudgetPayload addLoan(LoanInput li){ loans.add(li); return this; }
        public BudgetPayload addCard(CardInput ci){ cards.add(ci); return this; }
    }

    // ---------- Workbook builder ----------
    public static ByteArrayOutputStream buildWorkbook(BudgetPayload p) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            DataFormat df = wb.createDataFormat();

            // Styles
            CellStyle header = wb.createCellStyle();
            Font bold = wb.createFont(); bold.setBold(true);
            header.setFont(bold);
            header.setBorderBottom(BorderStyle.THIN);

            CellStyle money = wb.createCellStyle();
            money.setDataFormat(df.getFormat("$#,##0.00"));

            CellStyle pct = wb.createCellStyle();
            pct.setDataFormat(df.getFormat("0.00%"));

            // 1) Budget sheet
            Sheet sBudget = wb.createSheet("Budget");
            int r = 0;
            Row hdr = sBudget.createRow(r++);
            setHeaderRow(hdr, header, "Category","Amount","Type");

            for (BudgetItem bi : p.items) {
                Row row = sBudget.createRow(r++);
                row.createCell(0).setCellValue(bi.category);
                Cell amt = row.createCell(1); amt.setCellValue(bi.amount); amt.setCellStyle(money);
                row.createCell(2).setCellValue(bi.type.name());
            }
            int lastDataRow = Math.max(1, r - 1); // at least row 1 (header is row 0)
            r++; // spacer
            Row totalsLabel = sBudget.createRow(r++);
            totalsLabel.createCell(0).setCellValue("Totals");

            Row ti = sBudget.createRow(r++);
            ti.createCell(0).setCellValue("Total Income");
            Cell tiCell = ti.createCell(1); tiCell.setCellStyle(money);
            tiCell.setCellFormula("SUMIF(C2:C"+lastDataRow+",\"INCOME\",B2:B"+lastDataRow+")");

            Row te = sBudget.createRow(r++);
            te.createCell(0).setCellValue("Total Expenses");
            Cell teCell = te.createCell(1); teCell.setCellStyle(money);
            teCell.setCellFormula("SUMIF(C2:C"+lastDataRow+",\"EXPENSE\",B2:B"+lastDataRow+")");

            Row rb = sBudget.createRow(r++);
            rb.createCell(0).setCellValue("Remaining Balance");
            Cell rbCell = rb.createCell(1); rbCell.setCellStyle(money);
            rbCell.setCellFormula(tiCell.getAddress().formatAsString()+"-"+teCell.getAddress().formatAsString());

            autoSize(sBudget, 3);

            // 2) Loans
            Sheet sLoans = wb.createSheet("StudentLoans");
            makeLoansSheet(sLoans, header, money, pct, p.loans);

            // 3) Credit cards
            Sheet sCards = wb.createSheet("CreditCards");
            makeCardsSheet(sCards, header, money, pct, p.cards);

            // 4) Summary
            Sheet sSummary = wb.createSheet("Summary");
            makeSummarySheet(sSummary, header, money, pct, tiCell, teCell);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return baos;
        }
    }

    private static void makeLoansSheet(Sheet sh, CellStyle header, CellStyle money, CellStyle pct, List<LoanInput> loans) {
        int r = 0;
        setHeaderRow(sh.createRow(r++), header,
                "Loan #","Principal","APR","Term (months)","Grace (months)","Capitalize During Grace?","Monthly Payment");

        int i = 1;
        for (LoanInput li : loans) {
            Row row = sh.createRow(r++);
            row.createCell(0).setCellValue(i++);

            Cell cP = row.createCell(1); cP.setCellValue(li.principal); cP.setCellStyle(money);
            Cell cR = row.createCell(2); cR.setCellValue(li.annualRatePct/100.0); cR.setCellStyle(pct);
            row.createCell(3).setCellValue(li.termMonths);
            row.createCell(4).setCellValue(li.graceMonths);
            row.createCell(5).setCellValue(li.capitalizeDuringGrace ? "Yes" : "No");

            String rate = cR.getAddress().formatAsString();
            String pv   = cP.getAddress().formatAsString();
            String nper = row.getCell(3).getAddress().formatAsString();
            String grace= row.getCell(4).getAddress().formatAsString();
            String cap  = row.getCell(5).getAddress().formatAsString();

            Cell payment = row.createCell(6); payment.setCellStyle(money);
            payment.setCellFormula("""
                -PMT(%s/12,%s,IF(%s="Yes",%s*(1+%s/12)^%s,%s))
            """.formatted(rate, nper, cap, pv, rate, grace, pv));
        }
        autoSize(sh, 7);
    }

    private static void makeCardsSheet(Sheet sh, CellStyle header, CellStyle money, CellStyle pct, List<CardInput> cards) {
        int r = 0;
        setHeaderRow(sh.createRow(r++), header,
                "Card #","Balance","APR","Fixed Monthly Payment","Months to Payoff (approx)","Total Interest (approx)");
        int i = 1;
        for (CardInput ci : cards) {
            Row row = sh.createRow(r++);
            row.createCell(0).setCellValue(i++);

            Cell b = row.createCell(1); b.setCellValue(ci.balance); b.setCellStyle(money);
            Cell apr = row.createCell(2); apr.setCellValue(ci.annualRatePct/100.0); apr.setCellStyle(pct);
            Cell pmt = row.createCell(3); pmt.setCellValue(ci.monthlyPayment); pmt.setCellStyle(money);

            String APR = apr.getAddress().formatAsString();
            String BAL = b.getAddress().formatAsString();
            String PMT = pmt.getAddress().formatAsString();

            // n = -LN(1 - (APR/12)*BAL/PMT) / LN(1 + APR/12)
            Cell n = row.createCell(4);
            n.setCellFormula("""
                IF(%s/12>0,IF(%s>%s*(%s/12),-LN(1-(%s/12)*%s/%s)/LN(1+%s/12),NA()),NA())
            """.formatted(APR, PMT, BAL, APR, APR, BAL, PMT, APR));

            // Total interest ≈ n*PMT - BAL
            Cell totI = row.createCell(5); totI.setCellStyle(money);
            totI.setCellFormula("""
                IF(ISNUMBER(%s),%s*%s-%s,NA())
            """.formatted(n.getAddress().formatAsString(), n.getAddress().formatAsString(), PMT, BAL));
        }
        autoSize(sh, 6);
    }

    private static void makeSummarySheet(Sheet sh, CellStyle header, CellStyle money, CellStyle pct,
                                         Cell totalIncomeCell, Cell totalExpenseCell) {
        int r = 0;
        Row title = sh.createRow(r++); title.createCell(0).setCellValue("GradGoals Budget Summary");

        Row ri = sh.createRow(r++); ri.createCell(0).setCellValue("Total Income");
        Cell ti = ri.createCell(1); ti.setCellStyle(money);
        ti.setCellFormula(totalIncomeCell.getSheet().getSheetName() + "!" + totalIncomeCell.getAddress().formatAsString());

        Row re = sh.createRow(r++); re.createCell(0).setCellValue("Total Expenses");
        Cell te = re.createCell(1); te.setCellStyle(money);
        te.setCellFormula(totalExpenseCell.getSheet().getSheetName() + "!" + totalExpenseCell.getAddress().formatAsString());

        Row rr = sh.createRow(r++); rr.createCell(0).setCellValue("Remaining Balance");
        Cell rb = rr.createCell(1); rb.setCellStyle(money);
        rb.setCellFormula(ti.getAddress().formatAsString() + "-" + te.getAddress().formatAsString());

        // 50/30/20 guidance
        Row e = sh.createRow(r++); e.createCell(0).setCellValue("Essentials (50%)");
        e.createCell(1).setCellFormula(ti.getAddress().formatAsString()+"*0.50");
        Row w = sh.createRow(r++); w.createCell(0).setCellValue("Wants (30%)");
        w.createCell(1).setCellFormula(ti.getAddress().formatAsString()+"*0.30");
        Row s = sh.createRow(r++); s.createCell(0).setCellValue("Savings/Debt (20%)");
        s.createCell(1).setCellFormula(ti.getAddress().formatAsString()+"*0.20");

        autoSize(sh, 2);
    }

    // ---------- helpers ----------
    private static void setHeaderRow(Row row, CellStyle header, String... labels) {
        for (int i=0; i<labels.length; i++) {
            Cell c = row.createCell(i);
            c.setCellValue(labels[i]);
            c.setCellStyle(header);
        }
    }
    private static void autoSize(Sheet sh, int cols) {
        for (int c=0;c<cols;c++) sh.autoSizeColumn(c);
    }

    // ---------- demo main (safe to delete later) ----------
    public static void main(String[] args) throws Exception {
        // Sample data; replace with your own collection wiring later
        BudgetPayload payload = new BudgetPayload()
                .addItem(new BudgetItem("Wages", 1800, BudgetItem.Type.INCOME))
                .addItem(new BudgetItem("Scholarship", 300, BudgetItem.Type.INCOME))
                .addItem(new BudgetItem("Rent", 900, BudgetItem.Type.EXPENSE))
                .addItem(new BudgetItem("Groceries", 250, BudgetItem.Type.EXPENSE))
                .addItem(new BudgetItem("Transportation", 75, BudgetItem.Type.EXPENSE))
                .addLoan(new LoanInput(15000, 5.5, 120, 6, true))
                .addLoan(new LoanInput(3500, 4.2, 60, 0, false))
                .addCard(new CardInput(1200, 24.99, 75))
                .addCard(new CardInput(450, 19.99, 35));

        ByteArrayOutputStream xlsx = buildWorkbook(payload);
        String out = "GradGoals_Budget.xlsx";
        try (FileOutputStream fos = new FileOutputStream(out)) {
            fos.write(xlsx.toByteArray());
        }
        System.out.println("Wrote " + out + " in current folder.");
    }
}
