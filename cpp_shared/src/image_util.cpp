#include <opencv2/imgproc.hpp>
#include <cstring>
#include <exception>

#include "image_util.h"

GrayscaleResultCode convert_to_grayscale(
    const uint8_t* inputData,
    int width,
    int height,
    uint8_t* outputData,
    size_t outputBufferSize
) {
    try {
        if (!inputData || !outputData) {
            return GRAYSCALE_ERR_NULL_BUFFER;
        }

        if (width <= 0 || height <= 0) {
            return GRAYSCALE_ERR_INVALID_DIMENSIONS;
        }

        const size_t requiredOutputSize = static_cast<size_t>(width) * height * 4; // BGRA: 4 bytes per pixel
        if (outputBufferSize < requiredOutputSize) {
            return GRAYSCALE_ERR_OUTPUT_TOO_SMALL;
        }

        // Wrap raw pointers with OpenCV Mats
        cv::Mat inputMat(height, width, CV_8UC4, const_cast<uint8_t*>(inputData));
        cv::Mat outputMat(height, width, CV_8UC4, outputData);
        cv::Mat grayMat;

        // Step 1: Convert BGRA → Grayscale
        cv::cvtColor(inputMat, grayMat, cv::COLOR_RGBA2GRAY); // CV_8UC1

        // Step 2: Extract alpha from input
        std::vector<cv::Mat> inChannels;
        cv::split(inputMat, inChannels); // inChannels[3] is alpha channel

        // Step 3: Merge grayscale + original alpha into BGRA output
        std::vector<cv::Mat> outChannels = {grayMat, grayMat, grayMat, inChannels[3]};
        cv::merge(outChannels, outputMat);

        return GRAYSCALE_SUCCESS;

    } catch (const std::exception&) {
        return GRAYSCALE_ERR_EXCEPTION;
    } catch (...) {
        return GRAYSCALE_ERR_UNKNOWN;
    }
}


// GrayscaleResultCode convert_to_grayscale(
//     const uint8_t* inputData,
//     int width,
//     int height,
//     int channels,
//     uint8_t* outputData,
//     size_t outputBufferSize
// ) {
//     try {
//         if (!inputData || !outputData) {
//             return GRAYSCALE_ERR_NULL_BUFFER;
//         }
//
//         if (width <= 0 || height <= 0) {
//             return GRAYSCALE_ERR_INVALID_DIMENSIONS;
//         }
//
//         if (channels != 3 && channels != 4) {
//             return GRAYSCALE_ERR_UNSUPPORTED_CHANNELS;
//         }
//
//         const size_t requiredOutputSize = static_cast<size_t>(width) * height;
//
//         if (outputBufferSize < requiredOutputSize) {
//             return GRAYSCALE_ERR_OUTPUT_TOO_SMALL;
//         }
//
//         cv::Mat inputMat(height, width, channels == 3 ? CV_8UC3 : CV_8UC4, const_cast<uint8_t*>(inputData));
//         cv::Mat grayMat;
//
//         int conversionCode = (channels == 3) ? cv::COLOR_BGR2GRAY : cv::COLOR_BGRA2GRAY;
//         cv::cvtColor(inputMat, grayMat, conversionCode);
//
//         std::memcpy(outputData, grayMat.data, requiredOutputSize);
//         return GRAYSCALE_SUCCESS;
//
//     } catch (const std::exception&) {
//         return GRAYSCALE_ERR_EXCEPTION;
//     } catch (...) {
//         return GRAYSCALE_ERR_UNKNOWN;
//     }
// }
