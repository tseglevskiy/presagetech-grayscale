This project provides a cross-platform solution for converting images to grayscale using OpenCV. It
combines a native C++ library for efficient image processing with an Android SDK that integrates
seamlessly into Kotlin-based applications. The implementation prioritizes performance, memory
efficiency, and compatibility with Android's Bitmap API, while maintaining a modular design for
potential expansion to other platforms. Below, you'll find details about the C++ library, Android
SDK, and a sample app demonstrating the API in action.

# C++

I assumed that the solution being developed should be cross-platform, so the shared C++ code is
organized as an independent project.

The directory `cpp_shared/` contains the native C++ logic:

* `src/image_util.cpp`: Main implementation of grayscale conversion using OpenCV
* `include/image_util.h`: Public C++ API header
* `test/grayscale_converter_test.cpp`: Unit tests using GoogleTest
* `CMakeLists.txt`: CMake configuration with optional BUILD_TESTING support
* `build.sh`: Script to configure, build, and run C++ unit tests with CMake and ctest

Since only the Android part of the project was implemented, I did not verify whether the proposed
data format would work for iOS -- though I expect there shouldn't be significant differences.

## Building and Testing

The `build.sh` script for building and testing is provided.

Or you can run it manually, it will build everything to `build/` directory:

```bash
cmake -S . -B build -DCMAKE_BUILD_TYPE=Release -DBUILD_TESTING=ON
cmake --build build --parallel
ctest --output-on-failure --test-dir build
```

It requires Linux or WSL with CMake and a C++ compiler installed.

## Provided API

The C++ API provides functionality for converting RGBA images to grayscale using OpenCV, ensuring
the alpha channel is preserved. It requires the caller to manage memory for input and output buffers
and supports efficient processing without internal memory allocation. For detailed usage and
parameter descriptions, refer to the `include/image_util.h` file.

## Known Limitations

OpenCV is integrated using `FetchContent`, which provides easy version control and environment
independence, but drastically increases build time. In a real-world project, you should consider
other integration options or set up a local cache.

# Android SDK

Since the end goal is to provide a usable SDK, I implemented it as a separate Android library
project. This allows it to be exported as a .aar using any standard method, including publishing
to a Maven repository.

Key components:

* `app/src/main/java/.../ImageUtil.kt`: The toGrayscaleSync() and toGrayscale() API extensions
* `app/src/main/java/.../NativeBridge`.kt: Kotlin JNI interface to C++ code
* `app/src/main/cpp/imageutil_jni.cpp`: JNI bridge that links Kotlin to the shared C++ module

To build the SDK .aar:

```
./gradlew assembleRelease
```

This outputs the artifact to:

```
imageutilsdk/build/outputs/aar/imageutilsdk-release.aar
```

## Testing

The project is linked with native C++ code, which makes it impossible to run unit tests on the
host machine. Therefore, a separate test module was created containing connected tests, which
require a device or emulator to run.

To execute connected tests:

```
./gradlew :test:connectedDebugAndroidTest
```

## Provided API

The Kotlin API provides functionality for converting Bitmap images to grayscale using a native
OpenCV implementation via JNI. It supports both synchronous and suspendable methods, ensuring the
alpha channel is preserved. For detailed usage and parameter descriptions, refer to the
`imageutilsdk/src/main/java/com/jollydroid/imageutilsdk/ImageUtil.kt` file.

## Known Limitations

* The API is implemented as a `Bitmap` extension function, which makes dependency injection
  difficult. This design pattern may require refinement.
* Build time is long; the first run may take up to 20 minutes.

# Android Sample

The `android-imageutil-demo/` directory contains a minimal Compose-based Android app that
demonstrates the usage of the SDK.

Key files:

* `app/src/main/.../MainActivity.kt`: Hosts the camera preview and UI layout
* `app/src/main/.../ImageViewModel.kt`: Handles captured image processing via the grayscale SDK

Features:

* Displays live camera preview using Jetpack CameraX
* Captures a frame on button press
* Converts it to grayscale using the C++-backed SDK
* Displays both the original and grayscale images side by side

## Usage of the API

You can find usage of the API in the `ImageViewModel.kt` file, where the conversion is performed

```
                    // Perform grayscale conversion using ImageUtil SDK
                    result = bitmapToProcess.toGrayscale(Dispatchers.Default)
```

# Warning

I used Android Studio Preview (Canary build), so some templates might include non-standard
plugins or library versions. I tried to keep everything clean, but let me know if you run
into surprises.

Please accept my apologies if the project takes a long time to load or build on your machine.  
The initial setup involves native C++ compilation and fetching OpenCV sources, which may cause
significant delays — especially on the first run.
I appreciate your patience, and I hope the structure and clarity of the code make up for the wait.
