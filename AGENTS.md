# AGENTS.md — TANICO Development Rules

## 1. Project context

TANICO is an Android application for a marketplace of organic agricultural harvest waste. It connects buyers with sellers and provides educational content about processing agricultural waste.

## 2. Approved technology baseline

- Kotlin
- Jetpack Compose
- Material Design 3
- Supabase
- Supabase PostgreSQL
- Supabase Auth
- Supabase Storage
- PostgreSQL Row Level Security (RLS)

Do not introduce another backend, database, UI framework, or architecture without team approval.

## 3. Mandatory workflow before coding

1. Read this file.
2. Read the relevant documents under `docs/`.
3. Inspect the existing source code before modifying it.
4. Identify the feature owner and shared files/tables involved.
5. Explain the implementation plan briefly before making broad changes.
6. Follow existing conventions and reuse existing components.
7. Run relevant tests or checks after changes.
8. Report files changed, behavior implemented, tests run, and unresolved issues.

## 4. Architecture rules

- Use the agreed MVVM + Repository approach.
- Compose UI must not query Supabase directly.
- UI events are handled by ViewModels.
- ViewModels expose observable UI state.
- Data access is handled through repository/data-source boundaries.
- Keep business rules out of Composable functions.
- Prefer small, feature-scoped components and clear interfaces.
- Do not add Gradle multi-module complexity unless the team agrees.

## 5. Database and Supabase rules

- PostgreSQL is the source of truth for shared application data.
- Supabase Auth owns authentication identity.
- `profiles.id` must reference `auth.users.id`.
- Use UUID identifiers unless the approved schema states otherwise.
- Never put a Supabase `service_role` key in Android source code.
- Never trust prices, stock, weights, totals, role, or ownership values sent by the client without server-side validation.
- Do not bypass RLS.
- Do not create or modify tables independently of the shared database documentation.
- Every schema change must be represented by a migration and documented.
- Never edit an already-applied migration in a shared environment; create a new migration.
- Transaction creation and stock changes must be performed atomically in PostgreSQL.
- Do not invent table columns, RPC functions, or endpoints. If missing, propose and document them first.

## 6. Role and authorization rules

- Supported roles: `buyer` and `seller`.
- Navigation visibility is not security. Enforce authorization in database policies and trusted database functions.
- A buyer can access only their own profile, addresses, cart, orders, and notifications.
- A seller can manage only their own store and products, and process orders belonging to that store.
- Publicly visible product data must follow the product publication/status rules.

## 7. Transaction rules already agreed

- One order belongs to one store.
- A cart may contain products from multiple stores, but checkout processes one store at a time.
- Revalidate product availability, price, and stock during checkout.
- Stock is deducted when an order is successfully created.
- Stock deduction and order creation must be atomic.
- If a qualifying order is rejected/cancelled, stock restoration must be idempotent and happen only once.
- Shipping cost is based on distance and total weight.
- Payment method is not decided yet. Do not assume COD, bank transfer, payment gateway, or simulated payment.
- Shipping provider, distance-routing provider, and tariff formula are not decided yet.

## 8. UI rules

- Use Jetpack Compose and Material Design 3.
- Follow `docs/design/design-system.md`.
- Use the TANICO palette defined there.
- Support loading, success, empty, and error states.
- Keep UI accessible and responsive.
- Do not introduce decorative gradients or unrelated visual styles without design approval.

## 9. Coding conventions

- Use descriptive English identifiers for code symbols.
- Keep user-facing UI copy in Bahasa Indonesia unless the team decides otherwise.
- Avoid duplicated business logic.
- Handle loading and errors explicitly.
- Do not silently swallow exceptions.
- Keep secrets and environment configuration out of version control.
- Add tests for business-critical behavior, especially authorization, checkout, stock, and status transitions.

## 10. Git collaboration

- Work on a feature branch; do not commit directly to the shared main branch unless agreed.
- Keep commits focused and descriptive.
- Do not overwrite another member's work.
- Avoid unrelated formatting or refactoring in a feature task.
- Document schema changes before implementing dependent client code.

## 11. Feature ownership

Ownership is about implementation responsibility, not exclusive ownership of shared tables.

- Muhammad Zaki Anggoro: authentication integration, session management, role-based navigation/access integration, buyer/seller profiles.
- Marketplace & Purchasing member: buyer marketplace, search/filter, cart, checkout, purchase history.
- Store & Sales member: seller dashboard, product management, stock, incoming orders, sales summary.
- Education & Notifications member: learning articles, categories, article likes, waste-value calculator, notifications.

Use the current team task allocation if it is updated by the project lead.

## 12. AI agent restrictions

- Do not claim code has been tested unless tests were actually run.
- Do not invent product requirements or resolve open decisions silently.
- Ask for clarification when a missing decision affects implementation.
- Do not change shared database contracts without explaining impact.
- Do not generate a second implementation of an existing feature without checking current code.
- When completing a task, summarize changed files, behavior, tests, and remaining work.
