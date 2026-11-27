package io.mosip.pixelpass.qr_generator.zlib

import io.mockk.clearAllMocks
import io.mosip.pixelpass.factory.CompressorFactory
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ZLibTest {

    @AfterTest
    fun after() {
        clearAllMocks()
    }

    @Test
    fun `should encode given string to zlib format`() {
        val data = "test".toByteArray()
        val expected = byteArrayOf(120, -38, 43, 73, 45, 46, 1, 0, 4, 93, 1, -63)
        val compressor = CompressorFactory.getCompressor()
        val actual = compressor.encode(data)
        assertContentEquals(expected, actual)
    }

    @Test
    fun `should encode given string to zlib format with given compression level`() {
        val data = "test".toByteArray()
        val expected = byteArrayOf(120, 94, 43, 73, 45, 46, 1, 0, 4, 93, 1, -63)
        val compressor = CompressorFactory.getCompressor()
        val actual = compressor.encode(data,5)
        assertContentEquals(expected, actual)
    }

    @Test
    fun `should decode given zlib compressed byte array to string format`() {
        val data = byteArrayOf(120, -38, 43, 73, 45, 46, 1, 0, 4, 93, 1, -63)
        val expected = "test".toByteArray()
        val compressor = CompressorFactory.getCompressor()
        val actual = compressor.decode(data)
        assertContentEquals(expected, actual)
    }
}
