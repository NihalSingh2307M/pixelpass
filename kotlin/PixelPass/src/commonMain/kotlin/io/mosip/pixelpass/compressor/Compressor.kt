package io.mosip.pixelpass.compressor

import io.mosip.pixelpass.shared.DEFAULT_COMPRESSION_LEVEL

internal interface Compressor {
    fun encode(
        input: ByteArray,
        qualityLevel: Int = DEFAULT_COMPRESSION_LEVEL
    ): ByteArray

    fun decode(input: ByteArray): ByteArray
}