# PesaPilotAndroid — Security & Report Accuracy Review

_Reviewed: 2026-06-12. Native Android (Kotlin/Compose) port of PesaPilot. Talks
directly to the same Supabase project as the web app, so server-side Row Level
Security is the real data-isolation layer — and that was **verified live** to be
active and enforcing during the web review._

## Security

### Verified GOOD ✅
- **Secrets:** `SUPABASE_URL` / `SUPABASE_ANON_KEY` come from `local.properties`
  → `BuildConfig` (with gradle-property / env fallbacks for CI). `local.properties`
  is git-ignored and was **never committed**. Only the **anon** key reaches the
  APK — public by design; RLS enforces access. No `service_role` key or password
  anywhere in source.
- **Auth:** official Supabase Kotlin SDK with **PKCE** flow (correct for mobile).
- **Manifest:** INTERNET permission only; no exported services/receivers/providers;
  only the launcher activity is exported. Cleartext traffic is disabled by default
  on `targetSdk 36` (HTTPS-enforced).
- **Release build:** `isMinifyEnabled` + `isShrinkResources` + ProGuard.
- **No sensitive logging** (no `Log.*` of passwords/tokens/sessions).
- **Data isolation:** every query goes through Supabase under the signed-in user;
  the same RLS that blocks cross-user reads/writes on web applies here.

### Fixed in this pass
- **[Medium] Backup could exfiltrate the cached session token.** `allowBackup` was
  `true` with empty backup rules, so Android Auto Backup / device-transfer could
  capture the cached Supabase refresh token from app storage. Set
  `allowBackup="false"` and added explicit `exclude` rules for `sharedpref` and the
  DataStore directory in both `backup_rules.xml` (API <31) and
  `data_extraction_rules.xml` (cloud-backup + device-transfer). The backend remains
  the source of truth, so nothing of value is lost.
- **[Housekeeping] Removed a 750 MB `java_pid31224.hprof` heap dump** from the
  working tree (already git-ignored; a memory dump can contain in-flight secrets).

### Recommendation (your action)
- The shared anon key appeared in the previous web git history (public by design).
  Optional: rotate it in Supabase → Settings → API for a clean slate.

## Report accuracy

The native reports had the **same calculation bugs as the web app** (the port even
included Other Income/Expense in net income already — slightly ahead of the original
web — but still mis-handled contra accounts). All fixed in `ReportsViewModel.kt`:

| # | Severity | Bug | Fix |
|---|---|---|---|
| 1 | High | P&L/Tax/Revenue/Expense used `abs(balance)` per account, so contra accounts (Sales Returns, Purchase Returns, Discounts) were **added** instead of netted | Signed contributions: `−balance` for credit-normal income, `+balance` for debit-normal expense/COGS |
| 2 | High | Balance Sheet added only current-period net income to equity; prior-period retained earnings were missing → out of balance for any period not starting at inception | Added a "Retained Earnings (prior periods)" equity line = net income from inception to day-before-start |
| 3 | Medium | A/P & A/R aging pooled all payments globally, so one contact's payment could clear another's invoice | FIFO **per vendor/customer** using `vendorId` / `customerId` on each line |
| 4 | Low | General Ledger running balance started at 0, ignoring prior balance | Carry an opening balance forward as the first row |
| 5 | Low | Tax Summary excluded other income/expense and used `abs` | Taxable income now equals the corrected net income; added an estimate disclaimer |

**Verification:** `:app:compileDebugKotlin` succeeds, and a standalone numeric test
with contra + other-income entries confirmed the corrected net income matches
hand-computed truth (4,300), where the old logic was off by 1,000.

### Gaps (not bugs)
- "Sales Tax / Income Tax / Withholding Tax" are not separate reports — only a
  single flat-30% Tax Summary estimate exists (now labelled as such). Proper
  VAT/WHT reporting is a feature addition; tell me the jurisdiction + rates to add it.
- Cash Flow uses a name/code heuristic to classify entries (same as web) — fine for
  typical entries, can misclassify unusual ones.
