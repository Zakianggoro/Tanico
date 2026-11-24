# Architecture Decision Log

| ID | Decision | Status | Notes |
|---|---|---|---|
| ADR-001 | Android uses Kotlin | Agreed | Project baseline |
| ADR-002 | UI uses Jetpack Compose + Material 3 | Agreed | Project baseline |
| ADR-003 | Backend uses Supabase | Agreed | Auth, database, storage |
| ADR-004 | Database uses Supabase PostgreSQL | Agreed | Relational data |
| ADR-005 | App uses buyer and seller roles | Agreed | `buyer`, `seller` |
| ADR-006 | One order belongs to one store | Agreed | Checkout one store at a time |
| ADR-007 | Deduct stock at successful order creation | Agreed | Must be atomic |
| ADR-008 | Shipping based on distance and weight | Agreed | Provider and tariff formula open |
| ADR-009 | Payment method | Open | Do not assume |
| ADR-010 | Routing/distance provider | Open | Must be selected |
| ADR-011 | Shipping tariff formula | Open | Must be selected |
| ADR-012 | Cancellation policy after seller acceptance | Open | Define stock restoration conditions |
