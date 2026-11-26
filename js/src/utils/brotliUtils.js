const { BrotliDecompress } = require("brotli/dec/decode");
const { BrotliInput } = require("brotli/dec/streams");
const {
  MAX_DECOMPRESSED_SIZE,
  DECOMPRESSED_SIZE_EXCEEDED_MESSAGE,
} = require("../shared/Constants");

// The decoder grows its own buffer, so output_size is no limit. This sink
// grows on demand and throws once the cap is crossed, before allocating more.
class CappedOutput {
  constructor(maxSize) {
    this.maxSize = maxSize;
    this.buffer = new Uint8Array(Math.min(4096, maxSize));
    this.pos = 0;
  }

  write(buf, count) {
    const needed = this.pos + count;
    if (needed > this.maxSize) throw new Error(DECOMPRESSED_SIZE_EXCEEDED_MESSAGE);
    if (needed > this.buffer.length) {
      const grown = new Uint8Array(Math.min(Math.max(needed, this.buffer.length * 2), this.maxSize));
      grown.set(this.buffer.subarray(0, this.pos));
      this.buffer = grown;
    }
    this.buffer.set(buf.subarray(0, count), this.pos);
    this.pos += count;
    return count;
  }
}

// Decoding uses a pure-JS Brotli so browser bundles never need Node's zlib.
function brotliDecompress(bytes, maxSize = MAX_DECOMPRESSED_SIZE) {
  const output = new CappedOutput(maxSize);
  BrotliDecompress(new BrotliInput(bytes), output);
  return output.buffer.slice(0, output.pos);
}

// Encoding relies on Node's zlib; package.json maps it to false for bundlers.
function brotliCompress(bytes, quality) {
  const zlib = require("zlib");
  if (!zlib || typeof zlib.brotliCompressSync !== "function") {
    throw new Error("Brotli compression is only supported in Node.js");
  }
  return zlib.brotliCompressSync(bytes, {
    params: { [zlib.constants.BROTLI_PARAM_QUALITY]: quality },
  });
}

module.exports = { brotliDecompress, brotliCompress };