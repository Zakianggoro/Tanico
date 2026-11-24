# Table Definitions — TANICO

This is a proposed logical schema. Review types, nullability, constraints, and ownership with the team before creating migrations.

## 1. `profiles`

| Column | Suggested type | Rules |
|---|---|---|
| `id` | uuid | PK; references `auth.users(id)` |
| `full_name` | text | required |
| `email` | text | optional/unique only if synchronized policy is agreed |
| `phone` | text | optional |
| `role` | text | required; `buyer` or `seller` |
| `avatar_path` | text | optional |
| `created_at` | timestamptz | default now() |
| `updated_at` | timestamptz | default now() |

## 2. `stores`

| Column | Suggested type | Rules |
|---|---|---|
| `id` | uuid | PK |
| `owner_id` | uuid | FK profiles.id; unique if one store per seller |
| `store_name` | text | required |
| `description` | text | optional |
| `address` | text | required |
| `latitude` / `longitude` | numeric | required for distance-based shipping |
| `created_at` / `updated_at` | timestamptz | timestamps |

## 3. `user_addresses`

| Column | Suggested type | Rules |
|---|---|---|
| `id` | uuid | PK |
| `user_id` | uuid | FK profiles.id |
| `label` | text | optional |
| `recipient_name` | text | required |
| `phone` | text | required |
| `address` | text | required |
| `city` | text | required |
| `postal_code` | text | optional |
| `latitude` / `longitude` | numeric | required for distance-based shipping |
| `is_default` | boolean | default false |
| `created_at` | timestamptz | default now() |

## 4. `categories`

| Column | Suggested type | Rules |
|---|---|---|
| `id` | uuid | PK |
| `name` | text | required; unique |
| `description` | text | optional |
| `created_at` | timestamptz | default now() |

## 5. `products`

| Column | Suggested type | Rules |
|---|---|---|
| `id` | uuid | PK |
| `store_id` | uuid | FK stores.id |
| `category_id` | uuid | FK categories.id |
| `name` | text | required |
| `description` | text | optional |
| `price` | numeric(14,2) | required; >= 0 |
| `stock` | integer | required; >= 0 |
| `unit` | text | required, e.g. kg, karung, ikat |
| `weight_per_unit_kg` | numeric(12,3) | required for weight-based shipping |
| `status` | text | draft, active, inactive, out_of_stock |
| `created_at` / `updated_at` | timestamptz | timestamps |

## 6. `product_images`

| Column | Suggested type | Rules |
|---|---|---|
| `id` | uuid | PK |
| `product_id` | uuid | FK products.id |
| `storage_path` | text | required |
| `is_primary` | boolean | default false |
| `created_at` | timestamptz | default now() |

## 7. `carts` and `cart_items`

`carts`: `id uuid PK`, `buyer_id uuid FK profiles.id`, `created_at`, `updated_at`. Consider unique `buyer_id` for one active cart per buyer.

`cart_items`: `id uuid PK`, `cart_id uuid FK carts.id`, `product_id uuid FK products.id`, `quantity integer CHECK (quantity > 0)`, timestamps. Consider unique `(cart_id, product_id)`.

## 8. `orders`

| Column | Suggested type | Rules |
|---|---|---|
| `id` | uuid | PK |
| `buyer_id` | uuid | FK profiles.id |
| `store_id` | uuid | FK stores.id |
| `address_id` | uuid | FK user_addresses.id; nullable if snapshot-only policy chosen |
| `status` | text | controlled order status |
| `subtotal` | numeric(14,2) | >= 0 |
| `shipping_fee` | numeric(14,2) | >= 0 |
| `total` | numeric(14,2) | >= 0 |
| `shipping_distance_km` | numeric(12,3) | >= 0 |
| `total_weight_kg` | numeric(12,3) | >= 0 |
| `shipping_address_snapshot` | jsonb | required |
| `store_address_snapshot` | jsonb | required |
| `shipping_calculation_details` | jsonb | optional |
| `payment_method` | text | nullable; undecided |
| `payment_status` | text | nullable; undecided |
| `stock_restored_at` | timestamptz | nullable; supports idempotent restoration |
| `created_at` / `updated_at` | timestamptz | timestamps |

## 9. `order_items`

| Column | Suggested type | Rules |
|---|---|---|
| `id` | uuid | PK |
| `order_id` | uuid | FK orders.id |
| `product_id` | uuid | FK products.id; consider nullable if preserving line after product deletion |
| `product_name` | text | required snapshot |
| `unit_price` | numeric(14,2) | required snapshot |
| `quantity` | integer | > 0 |
| `unit` | text | required snapshot |
| `weight_per_unit_kg` | numeric(12,3) | required snapshot |
| `subtotal` | numeric(14,2) | required snapshot |

## 10. `order_status_history`

`id uuid PK`, `order_id uuid FK orders.id`, `changed_by uuid FK profiles.id`, `old_status text`, `new_status text`, `note text`, `created_at timestamptz`.

## 11. `notifications`

`id uuid PK`, `user_id uuid FK profiles.id`, `order_id uuid FK orders.id nullable`, `title text`, `message text`, `type text`, `is_read boolean default false`, `created_at timestamptz`.

## 12. Education tables

- `article_categories`: `id`, `name`, `description`.
- `articles`: `id`, `category_id`, `title`, `content`, `image_path`, `status`, `published_at`, timestamps.
- `article_likes`: `id`, `article_id`, `user_id`, `created_at`; unique `(article_id, user_id)`.

## 13. Calculator tables

- `waste_types`: `id`, `name`, `unit`, `description`, `is_active`.
- `waste_price_estimates`: `id`, `waste_type_id`, `estimated_price`, `effective_at`, `source_note`, `created_at`.

The calculator result is an estimate, not a guaranteed selling price or actual income.
