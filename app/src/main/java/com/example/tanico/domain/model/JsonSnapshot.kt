package com.example.tanico.domain.model

/**
 * Raw JSON snapshot (orders.shipping_address_snapshot, store_address_snapshot,
 * shipping_calculation_details). The JSON keys are not documented yet, so the
 * domain layer keeps the raw string instead of guessing a structure.
 *
 * TODO: replace with typed models after the checkout owner documents the keys.
 */
@JvmInline
value class JsonSnapshot(val raw: String)