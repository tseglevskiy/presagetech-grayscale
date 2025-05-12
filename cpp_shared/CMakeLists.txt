cmake_minimum_required(VERSION 3.14)
project(image_util LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Optional OpenCV fetch (can be overridden by parent)
option(USE_FETCH_OPENCV "Use FetchContent to get OpenCV" ON)

if (USE_FETCH_OPENCV)
    # Core OpenCV modules only
    set(BUILD_LIST "core,imgproc" CACHE STRING "" FORCE)

    # Disable all extras
    set(BUILD_TESTS OFF CACHE BOOL "" FORCE)
    set(BUILD_PERF_TESTS OFF CACHE BOOL "" FORCE)
    set(BUILD_DOCS OFF CACHE BOOL "" FORCE)
    set(BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
    set(BUILD_opencv_apps OFF CACHE BOOL "" FORCE)
    set(BUILD_opencv_js OFF CACHE BOOL "" FORCE)
    set(BUILD_opencv_java OFF CACHE BOOL "" FORCE)
    set(BUILD_opencv_python OFF CACHE BOOL "" FORCE)
#    set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)  # Optional: force static linking

    # Avoid platform-specific CPU extensions and binaries
    set(WITH_IPP OFF CACHE BOOL "" FORCE)
    set(WITH_IPP_A OFF CACHE BOOL "" FORCE)
    set(WITH_TBB OFF CACHE BOOL "" FORCE)
    set(WITH_OPENCL OFF CACHE BOOL "" FORCE)
    set(WITH_OPENMP OFF CACHE BOOL "" FORCE)
    set(WITH_PTHREADS_PF OFF CACHE BOOL "" FORCE)
    set(WITH_QUIRC OFF CACHE BOOL "" FORCE)
    set(WITH_PROTOBUF OFF CACHE BOOL "" FORCE)
    set(WITH_EIGEN OFF CACHE BOOL "" FORCE)
    set(WITH_WEBP OFF CACHE BOOL "" FORCE)
    set(WITH_IMGCODEC_HDR OFF CACHE BOOL "" FORCE)
    set(WITH_IMGCODEC_PXM OFF CACHE BOOL "" FORCE)
    set(WITH_IMGCODEC_SUNRASTER OFF CACHE BOOL "" FORCE)

    # Optional compatibility toggles
    set(OPENCV_ENABLE_NONFREE OFF CACHE BOOL "" FORCE)
    set(OPENCV_GENERATE_PKGCONFIG OFF CACHE BOOL "" FORCE)
    set(OPENCV_GENERATE_SETUPVARS OFF CACHE BOOL "" FORCE)
    set(OPENCV_ENABLE_ALLOCATOR_STATS OFF CACHE BOOL "" FORCE)

    # Android-specific
    set(BUILD_ANDROID_PROJECTS OFF CACHE BOOL "" FORCE)
    set(WITH_ANDROID_MEDIANDK OFF CACHE BOOL "" FORCE)

    include(FetchContent)
    FetchContent_Declare(
            opencv
            GIT_REPOSITORY https://github.com/opencv/opencv.git
            GIT_TAG 4.11.0
    )
    FetchContent_MakeAvailable(opencv)
    set(OpenCV_INCLUDE_DIRS ${opencv_SOURCE_DIR}/modules/core/include
            ${opencv_SOURCE_DIR}/modules/imgproc/include
            ${opencv_BINARY_DIR})
endif ()

# Image processing library
add_library(image_util SHARED
        include/image_util.h
        src/image_util.cpp
)

target_include_directories(image_util PUBLIC
        $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
        $<INSTALL_INTERFACE:include>
        ${OpenCV_INCLUDE_DIRS}
        ${CMAKE_BINARY_DIR})


target_link_libraries(image_util PUBLIC
        opencv_core
        opencv_imgproc
)

# Enable testing infrastructure
include(CTest)

if (BUILD_TESTING)
    enable_testing()  # Required for ctest

    # Add GoogleTest via FetchContent
    include(FetchContent)
    FetchContent_Declare(
            googletest
            GIT_REPOSITORY https://github.com/google/googletest.git
            GIT_TAG release-1.12.1
    )
    FetchContent_MakeAvailable(googletest)

    # Define test executable
    add_executable(image_util_test
            test/grayscale_converter_test.cpp
    )

    # Link against your library and GoogleTest
    target_link_libraries(image_util_test PRIVATE
            image_util
            gtest
            gtest_main
    )

    # Register test with CTest
    add_test(NAME GrayscaleTest COMMAND image_util_test)
endif()
