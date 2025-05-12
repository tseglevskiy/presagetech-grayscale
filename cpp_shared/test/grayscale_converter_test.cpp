#include <gtest/gtest.h>
#include "image_util.h"

#include <gtest/gtest.h>
#include "image_util.h"

TEST(GrayscaleTest, RedPixelShouldConvertToGrayRGBA) {
    const int width = 2, height = 2;
    const size_t channels = 4; // RGBA
    const size_t pixelCount = width * height;
    const size_t inputSize = pixelCount * channels;

    // Prepare input: 2x2 red pixels (255, 0, 0, 255)
    std::vector<uint8_t> input(inputSize);
    for (size_t i = 0; i < inputSize; i += 4) {
        input[i + 0] = 255; // R
        input[i + 1] = 0;   // G
        input[i + 2] = 0;   // B
        input[i + 3] = 255; // A
    }

    std::vector<uint8_t> output(inputSize); // same size as input (RGBA)

    auto result = convert_to_grayscale(input.data(), width, height, output.data(), output.size());
    ASSERT_EQ(result, GRAYSCALE_SUCCESS);

    for (size_t i = 0; i < output.size(); i += 4) {
        uint8_t r = output[i + 0];
        uint8_t g = output[i + 1];
        uint8_t b = output[i + 2];
        uint8_t a = output[i + 3];

        // Grayscale value for red ~76 in OpenCV (0.299 * 255)
        EXPECT_NEAR(r, 76, 2);
        EXPECT_EQ(r, g);
        EXPECT_EQ(r, b);
        EXPECT_EQ(a, 255);
    }
}

