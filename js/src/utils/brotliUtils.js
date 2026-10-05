const brotliDecode = require("brotli/decompress");

// Decoding uses a pure-JS Brotli so browser bundles never need Node's zlib.
function brotliDecompress(bytes) {
  return new Uint8Array(brotliDecode(bytes));
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
