# JavaFX Desktop UI — Approach 2 (REST Client)

Idea + tracking doc for adding an optional JavaFX desktop front-end **alongside** the
existing Thymeleaf web UI, without duplicating business logic.

## Chosen approach: JavaFX as a REST client

The desktop app is a **thin client** that talks to the Spring Boot server over HTTP,
reusing the existing `view/rs` REST layer. It does **not** embed Spring or touch the
database directly.

```
┌─────────────────┐        HTTP/JSON        ┌──────────────────────────────┐
│  JavaFX Desktop │  ───────────────────▶   │  Spring Boot server          │
│  (AtlantaFX)    │  ◀───────────────────   │  view/rs REST  →  ExpenseMgr  │
└─────────────────┘                         │  domain + JPA + H2           │
                                            └──────────────────────────────┘
                                            (also serves the Thymeleaf web UI)
```

### Why this approach (vs. in-process)

- **Clean separation** — the desktop app has zero knowledge of persistence or Spring.
- **Future-proof for hosted/open-core** (see `open-core-split.md`): the same desktop
  client can point at `localhost` or a **remote** instance just by changing the base URL.
- Both UIs can run at once: web on `:8080`, desktop as a separate client process.
- Trade-off accepted: needs the REST API fleshed out, and a running server.

### Why not in-process (approach 1)

Injecting `ExpenseManager` straight into JavaFX controllers is less code for a purely
local app, but entangles JavaFX's lifecycle/threading with Spring and can't talk to a
remote server. Rejected in favor of the client model for long-term flexibility.

## Prerequisite: flesh out the REST API

The current `ExpenseController` is mostly write endpoints (add/remove/edit) plus a thin
`GET /expenses` (name + cash-left only). The desktop client needs **read** endpoints that
return enough to render a screen.

- [x] `GET /v1/accountant/expenses/expense/{yearMonth}` → full month detail JSON
      (`MonthDetailResult`): budgets, statistics, percentages, per-day allocation list.
- [x] `GET /v1/accountant/expenses/months` → `MonthSummaryResult` list (yearMonth +
      expenseName + cashLeft) to drive the month selector.
- [ ] Later: add month start/end dates to the summary for a richer selector.
- [ ] Later: read endpoints for payments/incomes/savings/pending to build full screens.

## Prototype scope (this iteration)

A single desktop window that:

1. Calls `GET /v1/accountant/expenses/expenses` to list months.
2. On selection, calls `GET /v1/accountant/expenses/expense/{yearMonth}`.
3. Renders, AtlantaFX-styled:
   - Header: expense name, cash total / cash left.
   - The three budgets (Fixed / Leisure / Saving) as progress bars mirroring the web UI's
     over-budget behavior (full + red when spent ≥ budget, incl. budget 0).
   - A daily table: date, weekday/weekend, max allocation, spent, done flag.

Launched via a Maven **`desktop` profile** so the default web build is untouched:

```bash
# 1. start the server (in one terminal)
./mvnw spring-boot:run

# 2. run the desktop client (in another terminal)
./mvnw -Pdesktop javafx:run
```

Optional base URL override (for pointing at a remote instance later):

```bash
./mvnw -Pdesktop javafx:run -Dpa.api.baseUrl=http://localhost:8080
```

## Build / packaging notes

- JavaFX is **not** bundled with the JDK. Deps (`javafx-controls`, `javafx-fxml`) + the
  `javafx-maven-plugin` live only in the `desktop` profile so the web jar stays lean.
- Versions pinned: JavaFX **25.0.4** (matches Java 25), AtlantaFX **2.1.0**,
  javafx-maven-plugin **0.0.8**.
- For real distribution later: `jpackage`/`jlink` to bundle a runtime + JavaFX into native
  installers (.dmg/.exe/.deb). Out of scope for the prototype.

## Threading

- JavaFX runs on its own Application Thread. All HTTP calls must run **off** it (background
  thread / `Task`) and results marshalled back via `Platform.runLater(...)` to avoid
  freezing the UI. The prototype uses a simple background executor for fetches.

## Look & feel

- **AtlantaFX** gives a modern flat light/dark theme out of the box (default JavaFX
  "Modena" looks dated). Prototype applies `PrimerLight`.
- JavaFX built-in charts are a good fit for the planned "multi-month charts" feature.
- Native file dialogs will help the planned CSV import / backup features.

## Ongoing cost / caveat

Two UIs = every feature potentially built twice. The shared core (`ExpenseManager` +
domain) keeps the *logic* single-sourced, but views diverge. Only commit to maintaining
both if the desktop experience proves worth it.

## Next steps (beyond prototype)

1. Month selector + navigation between months.
2. Write actions from desktop (add payment/income) via existing POST endpoints.
3. Charts tab (multi-month trends) using JavaFX charts.
4. Package with jpackage for a real installer.
5. Decide unified launcher (`--ui=web|desktop|both`) vs. keeping desktop a separate process.
