package io.mosip.pixelpass.shared

enum class CompressionType {
    ZLIB, BROTLI;

    companion object {
        fun fromString(value: String): CompressionType =
            values().firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unsupported compression type: $value")
    }
}