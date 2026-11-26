# Supabase migrations

The ordered migrations establish the documented identity, store, address, product, cart, order, notification, education, and calculator tables. They enable RLS, create profiles from validated signup metadata, create a store with validated signup metadata for new seller accounts, prevent client role changes and direct stock updates, and expose orders only as read-only owner/buyer data until trusted transaction functions are implemented.

`20261126154838_nearby_products_rpc.sql` adds the buyer-only `get_nearby_active_products` RPC. It returns active products within the requested radius and their straight-line distance without making store addresses or coordinates publicly readable. Apply this migration to the Supabase project before using the buyer nearby catalog.

Seller signup requires a store name, full street address, latitude, and longitude. Existing seller profiles are not backfilled automatically because their exact store address and coordinates are not known; those accounts need a one-time store setup with the seller's real location.

Apply migrations to a development project first and review the SQL before applying it to any shared environment. `supabase/dev/analytics_seed.sql` is manual local-only demo data and is not an executable migration.

This is not a complete implementation of the proposed system. Payment workflow, routing/distance provider, shipping tariff formula, cancellation rules, and exact product unit requirements remain open. Do not implement checkout or order status mutation until those decisions are approved and the atomic transaction functions are designed.
