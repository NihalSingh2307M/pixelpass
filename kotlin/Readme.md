# PixelPass

## Features

- Create QR Code for given data
- Uses zlib (default) or Brotli compression and base45 encoding
- Decode QR data encoded by PixelPass
- Convert CBOR encoded base64Url string to JSON

## Usage

The library is implemented in Kotlin and is compatible with both **Kotlin** and **Java** projects. It is published to Maven as two artifacts:

* **`pixelpass-aar`** – Android library (AAR)
* **`pixelpass-jar`** – JVM library (JAR)

Choose the artifact that best fits your project.

### Gradle

**Android (AAR)**

```gradle
implementation("io.inji:pixelpass-aar:1.0.0-rc.2")
```

**JVM (JAR)**

```gradle
implementation("io.inji:pixelpass-jar:1.0.0-rc.2")
```

### Maven

**Android (AAR)**

```xml
<dependency>
    <groupId>io.inji</groupId>
    <artifactId>pixelpass-aar</artifactId>
    <version>1.0.0-rc.2</version>
</dependency>
```

**JVM (JAR)**

```xml
<dependency>
    <groupId>io.inji</groupId>
    <artifactId>pixelpass-jar</artifactId>
    <version>1.0.0-rc.2</version>
</dependency>
```


## Compression

`generateQRCode` and `generateQRData` accept an optional `compressorType`: `"zlib"` (default) or `"brotli"`, case-insensitive. Any other value throws `IllegalArgumentException`.

`decode` detects the format by itself: data with a zlib header is inflated as zlib, anything else is decoded as Brotli. QR data from earlier versions, and calls that omit `compressorType`, behave exactly as before.

### Requirements

- JDK 17. The project is built with Kotlin 2.0.0 and Android Gradle Plugin 8.3.0.
- Android `minSdk` 23. Android builds compile against API 34 (`compileSdk = 34`).

### Brotli native libraries

Brotli uses [Brotli4j](https://github.com/hyperxpro/Brotli4j) (`1.23.0`) on the JVM and [Brotli4Droid](https://central.sonatype.com/artifact/io.github.lukeaschenbrenner/Brotli4Droid) (`1.2.0`) on Android. Both load a native library. PixelPass declares only the Java part on the JVM, so zlib works everywhere without extra setup.

If the native library cannot load, Brotli calls throw `UnsupportedOperationException` with the loader error as the cause. Zlib is unaffected.

#### Building PixelPass (Windows, Linux, macOS)

No manual changes are needed. `kotlin/PixelPass/build.gradle.kts` picks the Brotli4j native for the host OS and CPU for the JVM tests:

```
cd kotlin
./gradlew :PixelPass:jvmTest :PixelPass:testDebugUnitTest
```

The same command works in Windows PowerShell and in Linux and macOS shells. Android unit tests run on the host JVM and skip Brotli. Building the Android target needs the Android SDK, which is not available on Linux ARM hosts. There, run only the JVM tests with `./gradlew :PixelPass:jvmTest -x :PixelPass:jacocoMergedReport`.

#### Using Brotli in a JVM app

Add the native for your OS and CPU, with the same version as `brotli4j`:

```kotlin
runtimeOnly("com.aayushatharva.brotli4j:native-linux-x86_64:1.23.0") // or windows-x86_64, osx-aarch64, ...
```

#### Android

Brotli4Droid bundles its native libraries, so no extra dependency is needed. Brotli encode and decode through `PixelPass` are verified on Android 15 (API 35) emulators with both 4 KB and 16 KB page sizes.

Keep Brotli4Droid at `1.2.0` or newer. Versions `1.1.0` and `1.1.1` did not pass the instrumented tests on a 16 KB page-size emulator. Verification on physical devices and on ABIs other than the emulator's was done with an earlier version only, so re-run the instrumented tests on your target hardware.

Android unit tests run on the host JVM, so Brotli on Android is covered by instrumented tests. Run them with a device attached: `./gradlew :PixelPass:connectedDebugAndroidTest`.

### Platform support

| Platform | Brotli | Verified by |
|---|---|---|
| Windows x86_64 | Yes | CI and local run |
| Linux x86_64 | Yes | CI |
| Linux aarch64 | Yes | CI, JVM tests only |
| macOS Apple Silicon | Yes | CI |
| macOS Intel | Yes | CI |
| Android emulator (x86_64 image), 4 KB pages | Yes | Instrumented tests, Pixel 8 API 35 |
| Android emulator (x86_64 image), 16 KB pages | Yes | Instrumented tests, Pixel 8 API 35 |
| Android physical devices (arm64-v8a, armeabi-v7a) | Not re-verified with `1.2.0` | Verified with `1.1.0` on one Android 13 device |

CI runs on GitHub-hosted runners through `.github/workflows/kotlin-platform-tests.yml`.

## APIs

`generateQRCode( data, ecc, header, compressorType )`

`data` - Data needs to be compressed and encoded

`ecc` - Error Correction Level for the QR generated. defaults to `"L"`

`header` - Data header need to be prepend to identify the encoded data. defaults to `""`

`compressorType` - `"zlib"` or `"brotli"`. defaults to `"zlib"`

returns a Base64-encoded PNG string of the QR code image. If a header is provided, it's included in the data that gets encoded into the QR.


`generateQRData( data, header, compressorType )`

`data` - Data needs to be compressed and encoded

`header` - Data header need to be prepend to identify the encoded data. defaults to `""`

`compressorType` - `"zlib"` or `"brotli"`. defaults to `"zlib"`

returns compressed and encoded data for qrcode with header prepended if provided.

`decode(data)`

`data` - Data needs to be decoded and decompressed without header

returns a base45 decoded and decompressed string. The compression format (zlib or Brotli) is detected automatically.

`decodeBinary(data)`

`data` - Data needs to be decompressed without header. Should be sent as a ByteArray. Currently only zip binary data is only supported.

returns a unzipped string


`getMappedData(jsonData: JSONObject, keyMapper: Map<String,Int>, valueMapper: Map<String, Map<Any, Int>>, cborEnable: Boolean = false)`

- `jsonData` - A JSON data. Which is a JSONObject.
- `keyMapper` - A Map which is used to map keys for the given JSON data. Which is a Map<String,Int>. It will default to the Claim-169 key mapper if nothing provided.
- `valueMapper` - A Map which is used to map values for the given JSON data. Which is a Map<String, Map<Any, Int>>. It will default to the Claim-169 value mapper if nothing provided.
- `cborEnable` - A Boolean which is used to enable or disable CBOR encoding on mapped data. Defaults to `false` if not provided.

return a hex string which is a CBOR encoded JSON with given mapper if `cborEnable` is set to true. Or returns a JSON remapped string.

`getMappedData(jsonData: JSONArray, keyMapper: Map<String,Int>, valueMapper: Map<String, Map<Any, Int>>, cborEnable: Boolean = false)`

- `jsonData` - An array of JSON data. Which is a JSONArray.
- `keyMapper` - A Map which is used to map keys for the given JSON data. Which is a Map<String,Int>. It will default to the Claim-169 key mapper if nothing provided.
- `valueMapper` - A Map which is used to map values for the given JSON data. Which is a Map<String, Map<Any, Int>>. It will default to the Claim-169 value mapper if nothing provided.
- `cborEnable` - A Boolean which is used to enable or disable CBOR encoding on mapped data. Defaults to `false` if not provided.

return an array hex string which is a CBOR encoded JSON with given mapper if `cborEnable` is set to true. Or returns an array of JSON remapped string.

`decodeMappedData(data: String, keyMapper: Array<Map<String, String>>, valueMapperFunction: (JSONObject) -> JSONObject)`

- `data` - A CBOR Encoded string or JSON string which needs to be re mapped.
- `keyMapper` - A array of map which is used to reverse map keys for the given data. Which is a Array<Map<String, String>>. The order of array is directly equals to the depth of JSON tree. As some of the keys are repeated at different depths of JSON this approach is needed. It will default to the Claim-169 reverse key mapper if nothing provided.
- `valueMapperFunction` - A function which is used to map values for the given data. Which is a (JSONObject) -> JSONObject. It will default to the Claim-169 value mapper function if nothing provided.

return a JSON remapped string.

`decodeMappedData(data: Array<String>, keyMapper: Array<Map<String, String>>, valueMapperFunction: (JSONObject) -> JSONObject)`

- `data` - An array of CBOR Encoded string or JSON string which needs to be re mapped.
- `keyMapper` - A array of map which is used to reverse map keys for the given data. Which is a Array<Map<String, String>>. The order of array is directly equals to the depth of JSON tree. As some of the keys are repeated at different depths of JSON this approach is needed. It will default to the Claim-169 reverse key mapper if nothing provided.
- `valueMapperFunction` - A function which is used to map values for the given data. Which is a (JSONObject) -> JSONObject. It will default to the Claim-169 value mapper function if nothing provided.

return an array JSON remapped string.

`toJson(base64UrlEncodedCborEncodedString)`

- `base64UrlEncodedCborEncodedString` - base64url-encoded representation of the CBOR-encoded data

returns decoded data in JSON format

## License
Apache 2.0
