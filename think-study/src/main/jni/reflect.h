
#ifndef REFLECT_H_
#define REFLECT_H_

#include <jni.h>
#include <android/log.h>
#include <string>

#define TAG "JNI_Call"

#define DEBUG 1

#ifdef DEBUG
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__);
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__);
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG,__VA_ARGS__);
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__);
#else
#define LOGI(...)
#define LOGD(...)
#define LOGW(...)
#define LOGE(...)

#endif

//
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_think_study_JNIUtil_getString(
//        JNIEnv *env,
//        jclass jclazz
//        );
/**
 *
 */
 namespace art{
     struct JavaVMExt {
         void *functions;
         void *runtime;
     };
 }


extern "C"
void dynamicFunc();
#endif /*END REFLECT_H_ */