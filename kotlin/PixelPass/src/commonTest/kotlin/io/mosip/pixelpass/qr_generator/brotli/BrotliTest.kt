package io.mosip.pixelpass.qr_generator.brotli

import io.mockk.clearAllMocks
import io.mosip.pixelpass.IgnoreOnAndroid
import io.mosip.pixelpass.factory.CompressorFactory
import io.mosip.pixelpass.shared.CompressionType
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

@IgnoreOnAndroid
class BrotliTest {

    @AfterTest
    fun after() {
        clearAllMocks()
    }

    @Test
    fun `should encode and decode given string using brotli`() {
        val data = "test".toByteArray()

        val compressor = CompressorFactory.create(CompressionType.BROTLI)

        val compressed = compressor.encode(data)
        val decompressed = compressor.decode(compressed)

        assertContentEquals(data, decompressed)
    }

    @Test
    fun `should encode and decode given string using brotli with given quality level`() {
        val data = "test".toByteArray()

        val compressor = CompressorFactory.create(CompressionType.BROTLI)

        val compressed = compressor.encode(data, 5)
        val decompressed = compressor.decode(compressed)

        assertContentEquals(data, decompressed)
    }

    @Test
    fun `should encode and decode unicode string`() {
        val data = "hello world".toByteArray(Charsets.UTF_8)

        val compressor = CompressorFactory.create(CompressionType.BROTLI)

        val compressed = compressor.encode(data)
        val decompressed = compressor.decode(compressed)

        assertContentEquals(data, decompressed)
    }

    @Test
    fun `should encode and decode empty byte array`() {
        val data = ByteArray(0)

        val compressor = CompressorFactory.create(CompressionType.BROTLI)

        val compressed = compressor.encode(data)
        val decompressed = compressor.decode(compressed)

        assertContentEquals(data, decompressed)
    }

    @Test
    fun `should throw exception for invalid brotli data`() {
        val invalid = "not brotli".toByteArray()

        val compressor = CompressorFactory.create(CompressionType.BROTLI)

        assertFailsWith<Exception> {
            compressor.decode(invalid)
        }
    }
}