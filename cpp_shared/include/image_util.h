#ifndef IMAGE_UTIL_H
#define IMAGE_UTIL_H

/**
 * @enum GrayscaleResultCode
 * @brief Enum representing the result of the grayscale conversion.
 */
enum GrayscaleResultCode {
    GRAYSCALE_SUCCESS = 0,
    GRAYSCALE_ERR_NULL_BUFFER,
    GRAYSCALE_ERR_INVALID_DIMENSIONS,
    GRAYSCALE_ERR_OUTPUT_TOO_SMALL,
    GRAYSCALE_ERR_EXCEPTION,
    GRAYSCALE_ERR_UNKNOWN
};

/**
 * @brief Converts a raw RGBA image to a grayscale RGBA image using OpenCV.
 *
 * This function converts a 4-channel image (assumed to be in RGBA format) to a grayscale
 * representation where the R, G, and B channels are set to the luminance value, and the
 * alpha channel is preserved from the input image.
 *
 * Both input and output buffers must be provided by the caller. No memory is allocated internally.
 * The output buffer must be at least (width × height × 4) bytes in size.
 *
 * @param inputData         Pointer to the input image buffer in RGBA format (4 bytes per pixel).
 * @param width             Width of the image in pixels.
 * @param height            Height of the image in pixels.
 * @param outputData        Pointer to the output buffer that will receive the RGBA grayscale image.
 * @param outputBufferSize  Size of the output buffer in bytes. Must be ≥ width × height × 4.
 *
 * @return GrayscaleResultCode indicating the result of the conversion.
 *
 * @retval GRAYSCALE_SUCCESS                 Conversion completed successfully.
 * @retval GRAYSCALE_ERR_NULL_BUFFER        One or more required buffers are null.
 * @retval GRAYSCALE_ERR_INVALID_DIMENSIONS Provided image width or height is invalid (≤ 0).
 * @retval GRAYSCALE_ERR_OUTPUT_TOO_SMALL   Provided output buffer is too small for the result.
 * @retval GRAYSCALE_ERR_EXCEPTION          A C++ exception occurred during processing.
 * @retval GRAYSCALE_ERR_UNKNOWN            An unknown error occurred.
 *
 * @note This function assumes input is in RGBA order (not BGRA). If used with Android Bitmap data,
 *       ensure the memory layout matches RGBA (as it typically does when using copyPixelsToBuffer).
 * @note No memory allocation is performed. The caller must manage both input and output buffers.
 */
extern "C"
GrayscaleResultCode convert_to_grayscale(
    const uint8_t* inputData,
    int width,
    int height,
    uint8_t* outputData,
    size_t outputBufferSize
);

#endif //IMAGE_UTIL_H
