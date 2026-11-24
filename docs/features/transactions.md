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
- Payment method is undecided. Do not show a fabricated payment success state.
- Order status changes must follow the approved transition rules.

## Buyer screens

- Cart.
- Checkout.
- Order history.
- Order detail.
- Order status timeline.
