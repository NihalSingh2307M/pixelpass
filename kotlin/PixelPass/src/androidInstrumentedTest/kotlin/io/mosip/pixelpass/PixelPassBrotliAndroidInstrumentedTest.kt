package io.mosip.pixelpass

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import nl.minvws.encoding.Base45
import org.json.JSONObject
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PixelPassBrotliAndroidInstrumentedTest {
    private val pixelPass = PixelPass()
    private val json = """{"hello":"world"}"""

    @Test
    fun brotliGenerateQRDataThenDecode() {
        val qrData = pixelPass.generateQRData(json, compressorType = "brotli")
        assertEquals("world", JSONObject(pixelPass.decode(qrData)).getString("hello"))
    }

    @Test
    fun brotliOutputIsNotMistakenForZlib() {
        val bytes = Base45.getDecoder().decode(pixelPass.generateQRData(json, compressorType = "brotli"))
        assertFalse(pixelPass.isDataZlibEncoded(bytes))
    }

    @Test
    fun compressorTypeIsCaseInsensitive() {
        val qrData = pixelPass.generateQRData(json, compressorType = "BROTLI")
        assertEquals("world", JSONObject(pixelPass.decode(qrData)).getString("hello"))
    }

    @Test
    fun plainTextRoundTripsThroughBrotli() {
        val text = "plain text message"
        assertEquals(text, pixelPass.decode(pixelPass.generateQRData(text, compressorType = "brotli")))
    }

    @Test
    fun largeJsonRoundTripsThroughBrotli() {
        val big = JSONObject().put("payload", "PixelPass ".repeat(5_000)).toString()
        val decoded = pixelPass.decode(pixelPass.generateQRData(big, compressorType = "brotli"))
        assertEquals(50_000, JSONObject(decoded).getString("payload").length)
    }

    @Test
    fun zlibStillRoundTrips() {
        val qrData = pixelPass.generateQRData(json)
        assertEquals("world", JSONObject(pixelPass.decode(qrData)).getString("hello"))
    }

    // Brotli-compressed CBOR of {"hello":"world"} made by the reference Python encoder, then Base45-encoded.
    @Test
    fun decodesQRDataFromReferenceEncoder() {
        val qrData = "*I3000L\$K%H8NGIIND/0N30"
        assertEquals("world", JSONObject(pixelPass.decode(qrData)).getString("hello"))
    }

    @Test
    fun generateQRCodeWithBrotliReturnsImage() {
        assertTrue(pixelPass.generateQRCode(json, compressorType = "brotli").isNotEmpty())
    }

    @Test
    fun unsupportedCompressorTypeFails() {
        assertFailsWith<IllegalArgumentException> { pixelPass.generateQRData(json, compressorType = "lz4") }
    }
}
