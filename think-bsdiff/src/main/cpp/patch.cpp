//
// Created by 90604 on 2020/12/13.
//
#include <jni.h>
#include<android/log.h>
extern "C"{
extern int p_main(int argc,const char * argv[]);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_think_bsdiff_BsPatcher_margeParch(JNIEnv *env, jclass clazz, jstring old_apk,
jstring patch, jstring output_apk) {
    const char* oldApk = env->GetStringUTFChars(old_apk,nullptr);
    const char* patchFile = env->GetStringUTFChars(patch,nullptr);
    const char* outputApk = env->GetStringUTFChars(output_apk, nullptr);
    const char* argv[] = {"", oldApk,outputApk,patchFile};
    try {
        p_main(4,argv);
    } catch (const char* msg) {
        __android_log_print(ANDROID_LOG_ERROR,"PATCH","mergePatch file error, please check!");
        return -1;
    }
    env->ReleaseStringUTFChars(old_apk,oldApk);
    env->ReleaseStringUTFChars(patch,patchFile);
    env->ReleaseStringUTFChars(output_apk,outputApk);
    return 0;
}
