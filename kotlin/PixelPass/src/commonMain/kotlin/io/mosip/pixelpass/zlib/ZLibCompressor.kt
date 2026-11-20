package io.mosip.pixelpass.zlib

import io.mosip.pixelpass.compressor.Compressor
import java.util.zip.Deflater
import java.util.zip.DeflaterInputStream
import java.util.zip.InflaterInputStream

internal class ZLibCompressor : Compressor {
    override fun encode(input: ByteArray, qualityLevel: Int): ByteArray =
        DeflaterInputStream(input.inputStream(), Deflater(qualityLevel)).readBytes()

    override fun decode(input: ByteArray): ByteArray =
        InflaterInputStream(input.inputStream()).readBytes()
}