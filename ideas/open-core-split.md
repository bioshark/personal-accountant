# Open-Core Monetization — Ideas & Notes

Parking lot for a possible future "publish for earning" direction. Nothing here is
committed; it's a reference to revisit later.

## TL;DR

If the goal is ever to earn from this project, the model to reach for is **open-core**,
not donations-vs-one-time-fee:

- Keep a **free, self-hosted core** to maximize adoption and build trust (trust is the
  whole game in personal finance).
- Charge a **recurring fee for a hosted/convenience tier** (sync, backups, bank import,
  mobile) for people who don't want to run a server.
- The code isn't the product; **hosting + convenience is**. Recurring revenue matches the
  recurring costs (servers, maintenance, bank-API upkeep).

### Why not the alternatives (recap)

- **Donations only:** conversion is <1–2% and whale-dependent (e.g. Actual Budget: a single
  org funded the majority of all money raised). It's a tip jar, not a business model.
- **One-time low fee ($5–10):** converts better than donations and is honest, but it's
  front-loaded revenue against back-loaded cost — you get paid once but maintain forever,
  and income only grows by acquiring *new* users (nobody re-buys a budgeting app).
- **Subscription only:** matches costs well but high friction/churn; needs a free funnel
  in front of it. Hence open-core.

## Proposed Feature Split

### Free / Self-Hosted Core (the funnel + trust builder)

Everything the app does today, kept free and open source:

- Monthly expense tracking with the configurable start→end date range
- Daily allowance model (weekday/weekend allocation, day-done settle-up)
- Payment types (DAILY / FIXED / LEISURE / SAVING) and categories
- Fixed / Leisure / Saving budgets with progress bars
- Income tracking + core/want/save (50/30/20-style) percentage split
- Percentage-based savings envelopes
- Recurring payment templates → pending → paid/skip flow
- Single-user, local H2 file database
- Manual CSV export (basic)

Planned features from `features-todo.md` that should stay **free** (they build adoption
and don't carry per-user cost):

- Multi-month views & charts (spending trends, month-over-month)
- Tags & notes
- Search / filter across months
- Basic reports (monthly/yearly summary)

### Paid Hosted / Convenience Tier (the revenue)

Things that carry ongoing cost or real convenience value:

- **Hosted instance** — we run it, user just logs in (no self-hosting needed)
- **Multi-device sync** — access from phone + laptop, always current
- **Automatic encrypted backups** + restore
- **Bank import / sync** (CSV/OFX is free; *automatic* bank-API sync is paid — it has real
  recurring API costs and maintenance, so it fits a paid tier structurally)
- **Mobile app** (or installable PWA with push/notifications)
- **PDF / advanced export** for tax or review
- Possibly: **multi-user / household** shared budgets

### Optional "Supporter License" (middle ground)

A cheap one-time or low recurring "supporter" badge for self-hosters who want to pay but
don't need hosting. Pure goodwill revenue; not a primary channel.

## Technical Implications for THIS Codebase

The current design is single-user and local-first. A hosted tier needs work that is worth
scoping before committing:

1. **Authentication & authorization** — none today. Hosted multi-tenant needs login,
   sessions, and per-user data isolation. (Flag: any network-exposed multi-user build must
   not ship without auth.)
2. **Datastore** — currently H2 file DB (`jdbc:h2:file:./data/...`). Hosted multi-tenant
   wants a real server DB (e.g. Postgres) with a tenant/user key on every entity.
    - Note: schema migrations currently rely on `ddl-auto=update` + `@ColumnDefault`. A
      hosted/product path should move to **Flyway/Liquibase** for safe, versioned migrations
      (already noted as a risk in earlier work).
3. **Data isolation** — `MonthlyExpenseEntity` and friends would need an owner/tenant id;
   repositories would filter by it.
4. **Sync model** — local-first + sync is non-trivial. Actual Budget's whole architecture
   is built around this (CRDT-ish sync). Decide early: hosted-only (simpler) vs true
   local-first sync (harder, but a real differentiator).
5. **Bank import** — this is the feature most likely to justify a subscription, but also
   the highest ongoing maintenance (aggregator fees, breaking APIs). Treat as a distinct
   paid module.

## Differentiator to Lean Into

The **daily spending-allowance model** (per-day allowance with a weekend bump and a
"day-done" settle-up) is unusual — most tools budget monthly, not daily. Actual, YNAB,
Firefly don't do this out of the box. If we ever productize, this is the hook worth
marketing rather than trying to match YNAB feature-for-feature.

## Open Questions

- Hosted-only vs local-first-with-sync? (Big architectural fork.)
- Pricing for the hosted tier? (Anchor: YNAB ~$109/yr; Actual hosted-by-others ~$3–5/mo.)
- Is bank import worth the maintenance burden, or lean on manual CSV + good UX?
- Solo project vs open to contributors? (Affects licensing choice — e.g. permissive vs
  open-core-friendly license, and whether to use an Open Collective from the start.)

## Next Steps (when/if revisited)

1. Decide the architectural fork (hosted-only vs sync).
2. Prototype auth + per-user data isolation behind a feature flag.
3. Introduce Flyway before any real product data exists.
4. Pick one paid hook (likely hosting + sync) and validate demand before building bank sync.
