# Supabase migrations

The ordered migrations establish the documented identity, store, address, product, cart, order, notification, education, and calculator tables. They enable RLS, create profiles from validated signup metadata, prevent client role changes and direct stock updates, and expose orders only as read-only owner/buyer data until trusted transaction functions are implemented.

Apply migrations to a development project first and review the SQL before applying it to any shared environment. `supabase/dev/analytics_seed.sql` is manual local-only demo data and is not an executable migration.

This is not a complete implementation of the proposed system. Payment workflow, routing/distance provider, shipping tariff formula, cancellation rules, and exact product unit requirements remain open. Do not implement checkout or order status mutation until those decisions are approved and the atomic transaction functions are designed.
