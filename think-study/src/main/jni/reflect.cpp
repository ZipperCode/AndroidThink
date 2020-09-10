//
// Created by 90604 on 2020/9/8.
//

#include "reflect.h"


extern "C"
JNIEXPORT jint
JNI_OnLoad(JavaVM *vm,void* reserved)
{
    JNIEnv *env = nullptr;

    LOGD("JNI_OnLoad GetEnv Call before")
    int init = vm->GetEnv(reinterpret_cast<void **>(&env),JNI_VERSION_1_6);
    LOGD("JNI_OnLoad GetEnv Call after %d",init)

    if(init != JNI_OK)
    {
        LOGD("JNI_OnLoad GetEnv Call failure")
        return -1;
    }

    const char className[] = "com/think/study/JNIUtil";
    jclass clazz = env->FindClass(className);

    JNINativeMethod jniNativeMethods[] = {
            {"dynamicFunc","()V",(void *)dynamicFunc}
    };

    if(env->RegisterNatives(clazz,jniNativeMethods,1) < 0)
    {
        LOGD("JNI_OnLoad RegisterNatives Call failure")
        return -1;
    }

    art::JavaVMExt *javaVMExt = (art::JavaVMExt *) vm;

    LOGE("get JavaVmExt = %p",javaVMExt);

    void *runtime = javaVMExt->runtime;

    LOGE("runtime pointer = %p",runtime);

    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_think_study_JNIUtil_getString(
        JNIEnv *env,
        jclass jclazz
)
{
    std::string hello = "hello world";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
void dynamicFunc()
{
    LOGD("dynamicFunc function is called")
}