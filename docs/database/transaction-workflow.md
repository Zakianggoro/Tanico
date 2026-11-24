# Transaction Workflow

## Agreed rules

- One order belongs to one store.
- Cart may contain multiple stores.
- Checkout handles one store at a time.
- Stock is deducted when order creation succeeds.
- Checkout must validate current product data on the database side.
- Shipping is based on distance and total weight.
- Payment method is undecided.

## Checkout sequence

1. Buyer opens cart.
2. App groups cart items by store.
3. Buyer selects one store group to checkout.
4. Buyer selects a delivery address.
5. Server obtains store and destination coordinates.
6. Server calculates total order weight.
7. Approved routing component calculates distance.
8. Approved tariff component calculates shipping fee.
9. App displays checkout summary.
10. Buyer confirms.
11. Database transaction revalidates products, prices, stock, cart ownership, and store consistency.
12. Database inserts order and order item snapshots.
13. Database deducts stock.
14. Database removes only checked-out cart items.
15. Database creates seller notification.
16. Transaction commits.

## Failure behavior

Any failure before commit must roll back:
- order insertions,
- order item insertions,
- stock changes,
- cart item deletion,
- notification insertion.

The client must show a useful error and allow the buyer to refresh the cart.

## Concurrency

Use row-level locking or an equivalent concurrency-safe method when validating and decrementing stock. Never rely on a client-side stock check alone.

## Stock restoration

When a rejection/cancellation policy requires restoring stock:
- run restoration in a trusted database function;
- lock the order and affected product rows;
- check `stock_restored_at` or an equivalent restoration ledger;
- restore stock once;
- update order status and history in the same transaction.

## Order status baseline

- `pending`: waiting for seller decision.
- `accepted`: accepted by seller.
- `rejected`: rejected by seller.
- `processing`: seller is preparing the order.
- `shipped`: order has been shipped.
- `completed`: order is complete.
- `cancelled`: order is cancelled.

Allowed transitions must be enforced in trusted server-side logic. The exact cancellation policy after acceptance/processing remains an open decision.

## Payment

No payment provider or payment method is approved yet. Keep payment integration out of the first implementation until the team decides the workflow.
