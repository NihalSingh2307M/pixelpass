package io.mosip.pixelpass.factory

import io.mosip.pixelpass.brotli.createBrotliCompressor
import io.mosip.pixelpass.compressor.Compressor
import io.mosip.pixelpass.shared.CompressionType
import io.mosip.pixelpass.zlib.ZLibCompressor

internal object CompressorFactory {
    fun create(type: CompressionType = CompressionType.ZLIB): Compressor =
        when (type) {
            CompressionType.ZLIB -> ZLibCompressor()
            CompressionType.BROTLI -> createBrotliCompressor()
        }
}