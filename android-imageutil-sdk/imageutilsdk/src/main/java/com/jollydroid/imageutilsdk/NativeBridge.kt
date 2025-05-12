package com.jollydroid.imageutilsdk

import java.nio.ByteBuffer

object NativeBridge {
    init {
        System.loadLibrary("imageutil_jni")
    }

    external fun convertToGrayscale(
        input: ByteBuffer, width: Int, height: Int, output: ByteBuffer
    ): Int
}