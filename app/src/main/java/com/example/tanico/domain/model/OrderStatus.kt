package com.example.tanico.domain.model

/**
 * Order status wrapper.
 *
 * The concrete status values are NOT yet defined in the shared docs
 * ("controlled order status"). Per AGENTS.md we must not invent them, so this
 * is a thin value class over the raw database string.
 *
 * TODO: once the team documents the valid statuses and transitions,
 * replace this with an enum and move transition rules into a use case.
 */
@JvmInline
value class OrderStatus(val value: String)
