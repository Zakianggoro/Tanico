# Supabase Android setup

## Local client configuration

The Android app reads these values from the ignored root `local.properties` file:

```properties
supabase.url=https://YOUR_PROJECT_ID.supabase.co
supabase.publishableKey=YOUR_SUPABASE_PUBLISHABLE_KEY
```

Use the project's publishable/anon key only. Never use a `service_role` or secret key in the Android app. These values are compiled into the application; the publishable key is public and database access must be protected by RLS.

## Database

Apply the ordered SQL migrations in `supabase/migrations/` to a development Supabase project before testing registration, profile loading, seller analytics, or the buyer nearby catalog. The `get_nearby_active_products` RPC is restricted to authenticated buyer profiles and returns active products within a requested radius without exposing store addresses or coordinates. The Android app obtains a one-shot foreground location when a buyer opens the app; the Beranda location card can request a new one-shot fix with its calibration button. Location is not continuously tracked and coordinates stay only in the Activity-scoped ViewModel; they are not saved to the profile or database. The app uses a 25 km straight-line radius. Debug builds also provide a location-card action to display all active products when device location is unavailable; this uses the existing public-active-product RLS policy and is not included in release builds. Seller signup creates a `stores` row in the same transaction as the profile and requires a store name, full address, and valid latitude/longitude coordinates. Buyer signup does not require store details. Role values are constrained to `buyer` and `seller`; owners cannot update their own role. Order tables have read-only client policies until checkout and status changes are implemented through trusted PostgreSQL transactions.

The signup migration does not fabricate store data for sellers who registered before it was applied. Those sellers can complete the one-time setup in the seller Profile tab with their actual address and coordinates.

The schema is a foundation, not a complete checkout implementation. Payment configuration, routing provider, shipping tariff, cancellation behavior, and server-side order creation remain undecided. Checkout writes, order status transitions, stock deduction/restoration, and product stock changes have no client policies until atomic trusted database functions are added.

`supabase/dev/analytics_seed.sql` is an optional local-only script. It requires existing buyer and seller profiles, creates a clearly named demo store if needed, and adds completed/pending example orders. It is not a migration and must not be run against shared or production data.

## Authentication settings

The TANICO project currently uses email/password authentication. Email Auth is enabled and phone Auth is disabled, so registration requires a valid email address; the phone number is stored as profile contact information. Email confirmation is controlled by the Supabase Auth email settings. If phone sign-in is enabled later, update the UI and Auth provider configuration together; phone confirmation also requires an SMS delivery provider.

## Manual smoke test

1. Apply migrations to a disposable development project and configure `local.properties`.
2. Register a buyer and verify that the trigger creates a profile but no store.
3. Register a seller with an actual store name, full address, and coordinates. Verify that the trigger creates both the profile and exactly one owned store.
4. Sign out and sign in as each account. Confirm the role comes from `profiles.role`, not the role selector on the login screen.
5. Check profile changes persist after a fresh login and role changes are rejected.
6. Run the local analytics seed only against a disposable local database. The seller's analytics should count completed product subtotals as omzet and show pending counts separately.
