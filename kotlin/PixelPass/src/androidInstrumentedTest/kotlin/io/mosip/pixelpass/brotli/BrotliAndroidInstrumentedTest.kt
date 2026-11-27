package io.mosip.pixelpass.brotli

import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mosip.pixelpass.factory.CompressorFactory
import io.mosip.pixelpass.shared.CompressionType
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrotliAndroidInstrumentedTest {
    private val compressor = CompressorFactory.getCompressor(CompressionType.BROTLI)

    @Test
    fun roundTrip() {
        val data = "PixelPass brotli round trip".toByteArray()
        assertContentEquals(data, compressor.decode(compressor.encode(data)))
    }

    @Test
    fun emptyInput() {
        val data = ByteArray(0)
        assertContentEquals(data, compressor.decode(compressor.encode(data)))
    }

    @Test
    fun largeInput() {
        val data = ByteArray(1_000_000) { (it % 251).toByte() }
        val encoded = compressor.encode(data)
        assertTrue(encoded.size < data.size)
        assertContentEquals(data, compressor.decode(encoded))
    }

    // Produced by the reference Python brotli encoder, so it proves cross-implementation decoding.
    @Test
    fun decodesPayloadFromAnotherEncoder() {
        val payload = Base64.decode("GzYAAMRtbF3ndNyChhAU1ajoaaiq9dRH0nbhU5fCEY7iRg0H", Base64.DEFAULT)
        val expected = "PixelPass brotli round trip PixelPass brotli round trip".toByteArray()
        assertContentEquals(expected, compressor.decode(payload))
    }

    @Test
    fun invalidDataFails() {
        assertFailsWith<Exception> { compressor.decode("not brotli".toByteArray()) }
    }
}