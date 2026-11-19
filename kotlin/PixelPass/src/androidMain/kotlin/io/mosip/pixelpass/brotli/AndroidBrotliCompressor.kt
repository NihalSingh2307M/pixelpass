package io.mosip.pixelpass.brotli

import com.txtnet.brotli4droid.Brotli4jLoader
import com.txtnet.brotli4droid.decoder.BrotliInputStream
import com.txtnet.brotli4droid.encoder.BrotliOutputStream
import com.txtnet.brotli4droid.encoder.Encoder
import io.mosip.pixelpass.compressor.Compressor
import java.io.ByteArrayOutputStream

internal class AndroidBrotliCompressor : Compressor {
    init {
        // Native load fails on unsupported ABIs or page sizes; report it instead of a raw link error.
        try {
            Brotli4jLoader.ensureAvailability()
        } catch (e: LinkageError) {
            throw UnsupportedOperationException("Brotli native runtime is unavailable on this device", e)
        }
    }

    override fun encode(input: ByteArray, qualityLevel: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val params = Encoder.Parameters().setQuality(qualityLevel)
        BrotliOutputStream(outputStream, params).use { it.write(input) }
        return outputStream.toByteArray()
    }

    override fun decode(input: ByteArray): ByteArray =
        BrotliInputStream(input.inputStream()).use { it.readBytes() }
}

internal actual fun createBrotliCompressor(): Compressor = AndroidBrotliCompressor()
