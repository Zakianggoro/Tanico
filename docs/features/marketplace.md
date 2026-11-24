# Feature Specification — Buyer Marketplace

## Scope

- Product catalog.
- Search and category/price/availability filters.
- Product detail.
- Product images.
- Add/update/remove cart items.
- Checkout initiation for one store at a time.

## Data sources

- `categories`
- `products`
- `product_images`
- `stores`
- `carts`
- `cart_items`

## Rules

- Only products eligible for public listing are visible to buyers.
- Product detail shows current price, unit, stock availability, description, seller/store, and images.
- Quantity must be positive and cannot exceed available stock at checkout.
- Cart can contain items from different stores.
- Checkout selection must contain items from exactly one store.
- Product price and stock must be revalidated at checkout.
- The client must not determine the authoritative order total.

## UI states

Provide loading, populated, empty, no search result, unavailable product, and error states.
