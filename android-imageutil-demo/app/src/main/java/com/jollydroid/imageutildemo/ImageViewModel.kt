package com.jollydroid.imageutildemo

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jollydroid.imageutilsdk.toGrayscale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.system.measureTimeMillis

class ImageViewModel : ViewModel() {
    // The latest grayscale bitmap to be shown in the UI
    private val _grayscaleBitmap = mutableStateOf<Bitmap?>(null)
    val grayscaleBitmap: State<Bitmap?> = _grayscaleBitmap

    // Internal buffer to hold the most recent captured bitmap
    private var latestBitmap: Bitmap? = null

    // Prevents overlapping image processing jobs
    private var isProcessing = false

    // Ensures thread-safe access to shared state
    private val mutex = Mutex()

    /**
     * Called when a new image has been captured from the camera.
     * Stores the bitmap and triggers the grayscale conversion process.
     */
    fun onCapturedBitmap(newBitmap: Bitmap) {
        viewModelScope.launch {
            mutex.withLock {
                latestBitmap = newBitmap
            }
            processNext()
        }
    }

    /**
     * Processes the next available bitmap in the queue by converting it to grayscale.
     * Ensures only one conversion runs at a time and automatically processes the next
     * if multiple frames are queued.
     */
    private fun processNext() {
        viewModelScope.launch {
            val bitmapToProcess: Bitmap = mutex.withLock {
                if (isProcessing || latestBitmap == null) return@launch

                isProcessing = true
                latestBitmap!!.also { latestBitmap = null }
            }

            try {
                Log.d("ImageUtilDemo", "Grayscale conversion started")

                val result: Bitmap
                val duration = measureTimeMillis {
                    // Perform grayscale conversion using ImageUtil SDK
                    result = bitmapToProcess.toGrayscale(Dispatchers.Default)
                }

                Log.d("ImageUtilDemo", "Grayscale conversion took ${duration}ms")

                // Push the result to the UI
                _grayscaleBitmap.value = result
            } catch (e: Exception) {
                Log.e("ImageUtilDemo", "Grayscale conversion failed: ${e.message}")
            }

            // Clear the flag and process next frame if available
            mutex.withLock {
                isProcessing = false
                processNext()
            }
        }
    }
}
