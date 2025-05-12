package com.jollydroid.imageutilsdk.test

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jollydroid.imageutilsdk.toGrayscaleSync
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun testAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.jollydroid.imageutilsdk.test", appContext.packageName)
    }

    @Test
    fun toGrayscaleSync_preservesColorFormatAndSize() {
        // Create a 2x2 test bitmap with known colors
        val input = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888).apply {
            setPixel(0, 0, Color.RED)
            setPixel(1, 0, Color.GREEN)
            setPixel(0, 1, Color.BLUE)
            setPixel(1, 1, Color.WHITE)
        }

        val output = input.toGrayscaleSync()

        assertEquals(Bitmap.Config.ARGB_8888, output.config)
        assertEquals(2, output.width)
        assertEquals(2, output.height)

        // Extract ARGB values
        val expectedGrayValues = mapOf(
            Pair(0, 0) to 76,    // RED → ~76
            Pair(1, 0) to 150,   // GREEN → ~150
            Pair(0, 1) to 29,    // BLUE → ~29
            Pair(1, 1) to 255    // WHITE → 255
        )

        for ((position, expectedGray) in expectedGrayValues) {
            val (x, y) = position
            val pixel = output.getPixel(x, y)
            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)
            val a = Color.alpha(pixel)

            // Assert RGB are equal (grayscale)
            assertTrue(r in (expectedGray - 5)..(expectedGray + 5), "Red at ($x,$y) = $r")
            assertTrue(g in (expectedGray - 5)..(expectedGray + 5), "Green at ($x,$y) = $g")
            assertTrue(b in (expectedGray - 5)..(expectedGray + 5), "Blue at ($x,$y) = $b")

            // Alpha should be 255 (opaque)
            assertEquals(255, a)
        }
    }

    @Test
    fun testToGrayscaleSync_preservesAlpha() {
        // Create a 2x2 ARGB_8888 bitmap with varying alpha
        val input = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888).apply {
            setPixel(0, 0, Color.argb(255, 255, 0, 0))   // Red, fully opaque
            setPixel(1, 0, Color.argb(128, 0, 255, 0))   // Green, semi-transparent
            setPixel(0, 1, Color.argb(64, 0, 0, 255))    // Blue, very transparent
            setPixel(1, 1, Color.argb(0, 255, 255, 255)) // White, fully transparent
        }

        val output = input.toGrayscaleSync()

        assertEquals(Bitmap.Config.ARGB_8888, output.config)

        // Check that alpha is preserved at each pixel
        val expectedAlphas = mapOf(
            Pair(0, 0) to 255,
            Pair(1, 0) to 128,
            Pair(0, 1) to 64,
            Pair(1, 1) to 0
        )

        for ((position, expectedAlpha) in expectedAlphas) {
            val (x, y) = position
            val pixel = output.getPixel(x, y)
            val a = Color.alpha(pixel)
            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)

            // RGB should still be equal (grayscale)
            assertTrue(r == g && g == b, "Pixel at ($x,$y) is not grayscale: R=$r, G=$g, B=$b")

            // Alpha should match
            assertEquals(expectedAlpha, a, "Alpha mismatch at ($x,$y)")
        }
    }
}