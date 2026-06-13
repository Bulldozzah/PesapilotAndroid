# Customer / Sales / Inventory Reports (Android)

Added to the Reports screen on 2026-06-13, mirroring the web app
(`pilot-grow-wise-main`). Implements the reports from "Customer Tracking Reports.docx"
that the existing double-entry + contacts data can support.

## Implemented (new "Customer & Sales" report group)
- **Sales by Customer** — revenue attributed to each customer over the period, ranked + chart
- **Customer Statement** — per-customer (picker in the toolbar): opening balance, invoices,
  payments, running balance, amount due
- **Customer Ledger** — per-customer full receivable history with a running balance
- **Customer Credit / Outstanding** — every customer's current outstanding receivable balance
- **Sales Register** — every revenue-recognising entry in the period, by date, with customer
- **Monthly Sales Summary** — operating revenue per month with total / average / best month + chart
- **Inventory Valuation & Turnover** — accounting value of inventory asset accounts + turnover (COGS ÷ avg inventory)

Attribution uses `journal_lines.customer_id` + the Accounts Receivable control account
(same approach as the A/R aging report). Sales with no tagged customer show as "Unattributed".

## NOT implemented — need a new data model (same as web)
Sales by Product, Sales by Salesperson, Sales Pipeline, and per-SKU inventory
(stock movement, inventory aging, low-stock/reorder, physical count, stock by location,
FIFO/LIFO/WAC valuation) require `products` / `stock_movements` / CRM tables that don't
exist yet. The app models inventory only as ledger value, so valuation/turnover works but
per-item reporting does not. These are a separate build (schema + data-entry screens).

_Verified: `:app:compileDebugKotlin` passes clean._
