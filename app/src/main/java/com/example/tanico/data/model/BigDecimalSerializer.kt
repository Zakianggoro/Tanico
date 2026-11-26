package com.example.tanico.data.model

import java.math.BigDecimal
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.json.jsonPrimitive

/**
 * PostgreSQL `numeric` (price, weight, totals) arrives as a JSON number.
 * BigDecimal keeps money exact; Double would introduce rounding errors.
 */
@OptIn(ExperimentalSerializationApi::class)
object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): BigDecimal {
        val json = decoder as? JsonDecoder ?: error("BigDecimalSerializer supports JSON only")
        return BigDecimal(json.decodeJsonElement().jsonPrimitive.content)
    }

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        val json = encoder as? JsonEncoder ?: error("BigDecimalSerializer supports JSON only")
        json.encodeJsonElement(JsonUnquotedLiteral(value.toPlainString()))
    }
}
