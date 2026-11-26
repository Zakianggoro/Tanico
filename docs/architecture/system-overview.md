# System Overview

## 1. Purpose

TANICO is an Android marketplace for organic agricultural harvest waste. The application connects sellers (such as farmers) with buyers or small businesses that need organic waste as raw material. TANICO also provides educational content about processing agricultural waste and a calculator for estimating its economic value.

## 2. Goals

- Provide a marketplace for buying and selling agricultural waste.
- Help sellers offer harvest waste as an additional source of income.
- Help buyers discover organic raw materials.
- Provide educational information about waste processing.
- Provide an estimated value calculator for selected waste types.

## 3. User roles

### Buyer (`buyer`)
- Register and sign in.
- Manage profile and delivery addresses.
- Browse, search, and filter products; browse nearby stores and view storefronts.
- View product details.
- Manage cart items.
- Checkout one store at a time.
- View order history, details, and status.
- Read and like educational articles.
- Use the waste-value calculator.
- View and mark own notifications as read.

### Seller (`seller`)
- Register and sign in.
- Manage profile and store information.
- Create, update, and remove/deactivate products.
- Manage product stock.
- View incoming orders for own store.
- Accept or reject orders and update allowed order statuses.
- View sales summary.
- Read and like educational articles.
- Use the waste-value calculator.
- View and mark own notifications as read.

## 4. Technology

- Android application: Kotlin + Jetpack Compose + Material 3.
- Backend platform: Supabase.
- Database: PostgreSQL managed by Supabase.
- Authentication: Supabase Auth.
- Product/article images: Supabase Storage.
- Authorization: PostgreSQL RLS and trusted database functions.

## 5. System boundaries

The Android application is the client. Supabase provides authentication, persistent data, storage, and database-side authorization. External routing/distance calculation and payment services are not selected yet.

## 6. Open decisions

- Payment method and payment lifecycle.
- Routing/distance provider.
- Shipping tariff formula and tariff configuration ownership.
- Cancellation rules after seller acceptance or order processing.
- Seller response time limit.
- Whether push notifications are required; in-app notifications are the baseline.
