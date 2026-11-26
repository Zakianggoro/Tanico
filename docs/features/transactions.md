# Feature Specification — Cart, Checkout, and Orders

## Scope

- Cart management.
- Checkout one store at a time.
- Shipping quote based on distance and total weight.
- Order creation.
- Buyer order history and details.
- Order status tracking.

## Data sources

- `carts`
- `cart_items`
- `orders`
- `order_items`
- `order_status_history`
- `user_addresses`
- `products`
- `stores`

## Rules

- One order belongs to one store.
- Checkout revalidates price, product status, and stock.
- Shipping fee is based on distance and weight; tariff/provider remain open decisions.
- Stock is deducted when order creation succeeds.
- Order creation and stock deduction must be atomic.
- Order item price/name/unit/weight are snapshots.
- Initial payment choice: cash on delivery (COD) only. Bank transfer and digital wallets are deferred.
- Do not show a fabricated payment success state. COD does not make client-side order creation safe.
- Order status changes must follow the approved transition rules.

## Current buyer UI scope

- The initial cart is session-only and is not synchronized to Supabase.
- Checkout review may display COD, but order submission remains disabled until a documented trusted database function can revalidate price and stock and create the order atomically.
- Shipping cost remains unavailable until the provider and tariff formula are approved. Do not present an estimated or payable total before then.

## Buyer screens

- Cart.
- Checkout.
- Order history.
- Order detail.
- Order status timeline.
