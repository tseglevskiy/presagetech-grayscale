#include <jni.h>
#include "image_util.h"

extern "C"
JNIEXPORT jint JNICALL
Java_com_jollydroid_imageutilsdk_NativeBridge_convertToGrayscale(
        JNIEnv *env,
        jobject thiz,
        jobject inputBuffer,
        jint width,
        jint height,
        jobject outputBuffer) {

    auto *inData = static_cast<uint8_t *>(env->GetDirectBufferAddress(inputBuffer));
    auto *outData = static_cast<uint8_t *>(env->GetDirectBufferAddress(outputBuffer));

    GrayscaleResultCode result = convert_to_grayscale(
            inData,
            width, height,
            outData,
            env->GetDirectBufferCapacity(outputBuffer)
    );

    return static_cast<jint>(result);
}