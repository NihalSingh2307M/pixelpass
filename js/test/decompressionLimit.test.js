const zlib = require("zlib");
const { decompressData } = require("../src/utils/cborUtils");
const { MAX_DECOMPRESSED_SIZE, DECOMPRESSED_SIZE_EXCEEDED_MESSAGE } = require("../src/shared/Constants");

const MB = 1024 * 1024;
const zlibPack = (size) => zlib.deflateSync(Buffer.alloc(size, 0x41));
const brotliPack = (size) =>
  zlib.brotliCompressSync(Buffer.alloc(size, 0x41), {
    params: { [zlib.constants.BROTLI_PARAM_QUALITY]: 1 },
  });

const codecs = [
  ["zlib", zlibPack],
  ["Brotli", brotliPack],
];

describe.each(codecs)("decompression size cap (%s)", (_name, pack) => {
  test("decodes a tiny payload", () => {
    expect(decompressData(pack(5)).length).toBe(5);
  });

  test("decodes a payload just under the cap", () => {
    expect(decompressData(pack(MAX_DECOMPRESSED_SIZE - 1)).length).toBe(MAX_DECOMPRESSED_SIZE - 1);
  });

  test("decodes a payload exactly at the cap", () => {
    const out = decompressData(pack(MAX_DECOMPRESSED_SIZE));
    expect(out.length).toBe(MAX_DECOMPRESSED_SIZE);
    expect(out[0]).toBe(0x41);
  });

  test("rejects a payload one byte over the cap", () => {
    expect(() => decompressData(pack(MAX_DECOMPRESSED_SIZE + 1))).toThrow(DECOMPRESSED_SIZE_EXCEEDED_MESSAGE);
  });

  test.each([2 * MB, 10 * MB, 100 * MB, 500 * MB])(
      "rejects a decompression bomb expanding to %i bytes",
      (size) => {
        const bomb = pack(size);
        expect(bomb.length).toBeLessThan(MB);
        expect(() => decompressData(bomb)).toThrow(DECOMPRESSED_SIZE_EXCEEDED_MESSAGE);
      }
  );
});
