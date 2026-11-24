# Database Design — TANICO

Status: Proposed baseline, subject to team review before SQL migrations are applied.

## 1. Database principles

- PostgreSQL through Supabase is the source of truth.
- Use UUID primary keys for application entities.
- Use `timestamptz` for timestamps.
- Use foreign keys and check constraints where appropriate.
- Enable RLS on tables exposed to the client.
- Store media files in Supabase Storage; store object paths/URLs in PostgreSQL.
- Keep order snapshots so historical transactions do not change when product/store/address data changes.

## 2. Entity groups

### Identity and store
- `profiles`: application profile and role; `id` references `auth.users.id`.
- `stores`: seller store data and origin coordinates.
- `user_addresses`: buyer delivery addresses and destination coordinates.

### Marketplace
- `categories`: product categories.
- `products`: products listed by stores.
- `product_images`: product image metadata/storage paths.

### Cart
- `carts`: buyer cart.
- `cart_items`: product and quantity in cart.

### Orders
- `orders`: one order for one buyer and one store.
- `order_items`: immutable purchase-line snapshots.
- `order_status_history`: audit trail for status transitions.

### Notifications
- `notifications`: in-app notifications owned by a user.

### Education and calculator
- `article_categories`: article categories.
- `articles`: educational content.
- `article_likes`: unique user/article likes.
- `waste_types`: calculator waste types and units.
- `waste_price_estimates`: estimated unit prices.

## 3. Agreed transaction rules

1. A cart can contain products from multiple stores.
2. A checkout operation selects products from exactly one store.
3. Each created order belongs to exactly one store.
4. Revalidate product status, price, and stock during checkout.
5. Deduct stock when order creation succeeds.
6. Order creation, order item insertion, stock deduction, and cart item removal must be atomic.
7. A rejection/cancellation that qualifies for stock restoration must restore stock exactly once.
8. Shipping cost is based on distance and total order weight.
9. Payment method is undecided; do not implement a specific payment provider or method yet.

## 4. Shipping calculation data

The following values are required:
- Store origin latitude/longitude.
- Delivery address latitude/longitude.
- Product weight per sale unit.
- Ordered quantity.
- Total order weight.
- Calculated distance.
- Tariff configuration and resulting shipping fee.

Suggested product field: `weight_per_unit_kg`.

Suggested order snapshots:
- `shipping_distance_km`
- `total_weight_kg`
- `shipping_fee`
- `shipping_address_snapshot`
- `store_address_snapshot`
- optional `shipping_calculation_details` JSONB

The routing provider and tariff formula are open decisions. Do not assume straight-line distance is road distance. Persist the distance and calculation details used at checkout.

## 5. Order totals

- `subtotal`: sum of order item subtotals.
- `shipping_fee`: shipping amount calculated for the order.
- `total`: `subtotal + shipping_fee` (until additional fees/discounts are formally introduced).
- All amounts use a consistent currency convention (IDR) and appropriate numeric precision.

## 6. Payment

Payment is not decided. Payment-related fields must remain optional until the team agrees on the payment workflow. Do not mark an order as paid merely because order creation succeeded.

## 7. Atomic checkout

Implement checkout as a trusted PostgreSQL function/RPC or equivalent server-side transaction. The function must:
- Derive the authenticated buyer from the session, not trust a client-supplied buyer ID.
- Verify the selected cart belongs to the buyer.
- Verify all selected items belong to one store.
- Lock relevant product rows or use an equivalent concurrency-safe mechanism.
- Revalidate active status, current price, and available stock.
- Recalculate item totals, total weight, shipping fee according to the approved tariff implementation.
- Insert order and order item snapshots.
- Deduct stock.
- Remove only the cart items included in that checkout.
- Create the seller notification.
- Roll back all changes if any step fails.

## 8. Stock restoration

Stock restoration must be idempotent. Record whether stock has already been restored or use a transaction-safe ledger/transition mechanism. A repeated reject/cancel request must not restore stock twice.

## 9. Index recommendations

Review and add indexes for:
- `products(store_id, status)`
- `products(category_id, status)`
- `orders(buyer_id, created_at)`
- `orders(store_id, created_at)`
- `order_items(order_id)`
- `notifications(user_id, is_read, created_at)`
- `article_likes(article_id, user_id)` unique
- `cart_items(cart_id, product_id)` unique where appropriate

## 10. Open decisions before production migrations

- Payment workflow.
- Routing/distance provider.
- Shipping tariff formula and configuration.
- Cancellation/refund rules after acceptance/processing.
- Whether order completion is seller-confirmed or buyer-confirmed.
- Exact product units and whether product weight is mandatory.
