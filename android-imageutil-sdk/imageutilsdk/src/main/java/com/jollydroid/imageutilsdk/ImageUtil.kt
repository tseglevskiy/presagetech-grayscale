package com.jollydroid.imageutilsdk

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Converts this [Bitmap] to grayscale synchronously using a native OpenCV implementation.
 *
 * This function works only with [Bitmap.Config.ARGB_8888] bitmaps. It converts each pixel
 * to grayscale by computing luminance from the RGB channels (based on OpenCV's `cv::COLOR_RGBA2GRAY`)
 * and sets R = G = B = gray. The alpha channel is preserved.
 *
 * Under the hood, it copies the bitmap's pixel data into a direct [ByteBuffer], invokes a
 * native C++ routine via JNI, and reconstructs a new ARGB_8888 bitmap with the grayscale result.
 *
 * @receiver A [Bitmap] in [Bitmap.Config.ARGB_8888] format.
 * @return A new [Bitmap] in grayscale, preserving the original alpha channel.
 *
 * @throws IllegalArgumentException If the input bitmap is not ARGB_8888.
 * @throws IllegalStateException If the native conversion fails.
 *
 * @see toGrayscale for a suspendable version.
 */

fun Bitmap.toGrayscaleSync(): Bitmap {
    require(config == Bitmap.Config.ARGB_8888) {
        "Only ARGB_8888 bitmaps are supported"
    }

    val width = this.width
    val height = this.height
    val pixelSize = 4 // ARGB_8888 = 4 bytes per pixel
    val bufferSize = width * height * pixelSize

    // Allocate direct buffers
    val inputBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())
    val outputBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())

    // Fill input buffer with raw pixel data
    copyPixelsToBuffer(inputBuffer)
    inputBuffer.rewind()

    // Call native grayscale conversion
    val resultCode = NativeBridge.convertToGrayscale(
        input = inputBuffer, width = width, height = height, output = outputBuffer
    )
    require(resultCode == 0) { "Grayscale conversion failed with code $resultCode" }

    // Create new ARGB_8888 bitmap and copy output buffer into it
    outputBuffer.rewind()
    return createBitmap(width, height).apply {
        copyPixelsFromBuffer(outputBuffer)
    }
}

/**
 * Converts this [Bitmap] to grayscale on a background thread using native OpenCV via JNI.
 *
 * This is the recommended way to perform a grayscale conversion in Kotlin-based Android apps.
 * It preserves the alpha channel and returns a new bitmap where the red, green, and blue channels
 * are equal to the computed luminance of the original pixel. Internally, it performs the conversion
 * in native code using OpenCV's `cv::COLOR_RGBA2GRAY`, followed by restoring the alpha channel.
 *
 * The input must use [Bitmap.Config.ARGB_8888], which is standard for Android bitmaps.
 * The output will also be in [Bitmap.Config.ARGB_8888] and compatible with all Android drawing APIs.
 *
 * This function is suspendable and offloads the computation to the provided [dispatcher] (defaulting to [Dispatchers.Default]),
 * making it safe to call from the main thread.
 *
 * @receiver A [Bitmap] in [Bitmap.Config.ARGB_8888] format.
 * @param dispatcher The [CoroutineDispatcher] on which the conversion should run.
 * @return A new [Bitmap] in grayscale, preserving the original alpha channel.
 *
 * @throws IllegalArgumentException If the input bitmap is not ARGB_8888.
 * @throws IllegalStateException If the native grayscale conversion fails.
 *
 * @see toGrayscaleSync for a synchronous version of this API.
 */

suspend fun Bitmap.toGrayscale(
    dispatcher: CoroutineDispatcher = Dispatchers.Default
): Bitmap = withContext(dispatcher) {
    toGrayscaleSync()
}