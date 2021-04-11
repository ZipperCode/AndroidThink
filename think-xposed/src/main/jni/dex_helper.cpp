//
// Created by 90604 on 2021/4/9.
//

#include<jni.h>
#include <fcntl.h>
#include <stdio.h>
#include"dex_file_art.h"
#include"log.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_think_xposed_JniHelper_dexFileByCookie(JNIEnv *env, jclass clazz,jstring pks, jobjectArray cookies)
{
    LI("Java_com_think_xposed_dex_Dex9_dexFileByCookie clazz = %p, cookies = %p", clazz, cookies);
    if(cookies == nullptr){
        return;
    }
    jboolean is_copy;
    const char* package = env->GetStringUTFChars(pks,&is_copy);
    int cookies_size = env->GetArrayLength(cookies);
    LI("cookies_size = %d", cookies_size);
    for(int i = 0; i< cookies_size; i++){
        jobject obj = env->GetObjectArrayElement(cookies,i);
        if(obj == nullptr){
            LE("cookie object == null -> continue");
            continue;
        }

        auto array = reinterpret_cast<jarray>(obj);
        jint cookie_size = env->GetArrayLength(array);
        if (env->ExceptionCheck() == JNI_TRUE) {
            LE("ExceptionCheck == JNI_TRUE -> continue");
            continue;
        }

        jlong *long_data = env->GetLongArrayElements(reinterpret_cast<jlongArray>(array), &is_copy);
        if (env->ExceptionCheck() == JNI_TRUE) {
            LE("ExceptionCheck == JNI_TRUE -> continue");
            continue;
        }
        LI("GetLongArrayElements => p = %p cookie_size = %d",long_data,cookie_size);

        // 强制转换为DexFile
        for (jsize j = 1; j < cookie_size;j++) {
            auto dexFile = reinterpret_cast<const art::DexFile *>(static_cast<uintptr_t>(long_data[j]));
            if(dexFile == nullptr){
                LE("dexFile == null -> continue");
                continue;
            }

            char* path = new char[255];
            sprintf(path,"/data/data/%s/files/%s_%d.dex",package, "classes",dexFile->Size());
            LI("dex文件保存目录 ： path = %s", path);

            FILE *file;
            file = fopen(path,"w+");
            if(file == nullptr){
                LE("create file %s failed, %s", path, strerror(errno));
                return;
            }
            fwrite(dexFile->Begin(),dexFile->Size(),1,file);
            fclose(file);
        }

    }

}

