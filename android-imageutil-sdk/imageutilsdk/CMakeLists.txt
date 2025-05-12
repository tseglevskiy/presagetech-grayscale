cmake_minimum_required(VERSION 3.14)
project(imageutil_jni)

add_subdirectory(../../cpp_shared image_util_build)

# Create the JNI wrapper library
add_library(imageutil_jni SHARED
        src/main/cpp/imageutil_jni.cpp
)

target_include_directories(imageutil_jni PRIVATE
        ../../cpp_shared/include
)

target_link_libraries(imageutil_jni
        image_util
        android
        log
)