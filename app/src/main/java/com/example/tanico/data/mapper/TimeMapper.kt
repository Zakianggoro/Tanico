package com.example.tanico.data.mapper

import java.time.Instant
import java.time.OffsetDateTime

/**
 * Parses PostgreSQL timestamptz text (e.g. 2026-11-26T10:00:00.123456+00:00).
 * Note: java.time needs minSdk 26 or core library desugaring.
 */
internal fun String.toInstant(): Instant = OffsetDateTime.parse(this).toInstant()
