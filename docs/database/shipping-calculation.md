# Shipping Calculation

## Agreed requirement

Shipping cost is calculated using distance and total shipment weight.

## Required source data

- Store origin coordinates (`stores.latitude`, `stores.longitude`).
- Delivery destination coordinates (`user_addresses.latitude`, `user_addresses.longitude`).
- Product sale unit (`products.unit`).
- Weight per sale unit (`products.weight_per_unit_kg`).
- Ordered quantity.

## Total weight

`total_weight_kg = SUM(quantity * weight_per_unit_kg)`

Use current product data during checkout, then store the final weight as an order snapshot.

## Distance

The distance must be calculated using a selected routing method/provider. Do not label straight-line coordinate distance as road distance. Routing provider is an open decision.

## Tariff

The tariff formula and rate configuration are open decisions. Do not hardcode a made-up per-kilometer or per-kilogram price.

The shipping component should have a replaceable contract, conceptually:

```kotlin
interface ShippingCalculator {
    suspend fun calculate(
        origin: Coordinates,
        destination: Coordinates,
        totalWeightKg: Double
    ): ShippingQuote
}
```

The actual implementation may call a backend function or approved routing service. Keep API secrets off the Android client.

## Persisted order snapshot

Persist:
- calculated distance,
- total weight,
- shipping fee,
- origin/destination snapshot,
- calculation metadata needed to explain the quote.

## Open decisions

- Routing provider.
- Whether distance means road distance or another explicitly defined metric.
- Tariff formula, minimum fee, rounding, and maximum distance/weight rules.
- Who maintains tariff configuration.
