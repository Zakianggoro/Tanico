# Feature Specification — Seller Store and Sales

## Scope

- Seller dashboard.
- Store profile.
- Product CRUD and stock management.
- Incoming orders.
- Accept/reject order.
- Update order processing status.
- Sales summary.

## Data sources

- `stores`
- `products`
- `product_images`
- `orders`
- `order_items`
- `order_status_history`

## Rules

- Seller may manage only their own store and products.
- Seller cannot modify historical order item snapshots.
- Seller cannot directly change stock in a way that bypasses transaction rules.
- Order status transitions must be validated server-side.
- Reject/cancel stock restoration must be idempotent.
- Sales summary must define which order statuses count. Pending/rejected/cancelled orders must not be silently counted as completed sales.

## Metrics

Keep these concepts separate:
- Gross order value / omzet.
- Completed sales.
- Net profit (only possible if costs are recorded).
- Available balance (only meaningful if a payment/settlement model exists).

Do not label gross sales as profit or available balance.
