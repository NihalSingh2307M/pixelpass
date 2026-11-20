
package io.mosip.pixelpass.brotli

import com.aayushatharva.brotli4j.Brotli4jLoader
import com.aayushatharva.brotli4j.decoder.BrotliInputStream
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream
import com.aayushatharva.brotli4j.encoder.Encoder
import io.mosip.pixelpass.compressor.Compressor
import java.io.ByteArrayOutputStream

internal class JvmBrotliCompressor : Compressor {

    init {
        // Brotli4j needs a native per OS/arch; surface a clear error when the host's is missing.
        try {
            Brotli4jLoader.ensureAvailability()
        } catch (e: LinkageError) {
            throw UnsupportedOperationException("Brotli native runtime is unavailable on this platform", e)
        }
    }

    override fun encode(input: ByteArray, qualityLevel: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val params = Encoder.Parameters().setQuality(qualityLevel)

        BrotliOutputStream(outputStream, params).use {
            it.write(input)
        }

        return outputStream.toByteArray()
    }

    override fun decode(input: ByteArray): ByteArray =
        BrotliInputStream(input.inputStream()).use {
            it.readBytes()
        }
}

internal actual fun createBrotliCompressor(): Compressor =
    JvmBrotliCompressor()
