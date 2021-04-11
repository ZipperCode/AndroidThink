//
// Created by 90604 on 2021/4/9.
//

#ifndef ANDROIDTHINK_LOG_H
#define ANDROIDTHINK_LOG_H

#include<android/log.h>

#define TAG "AHook"

#define LV(...) ((void)__android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__))
#define LE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))
#define LD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))
#define LW(...) ((void)__android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__))
#define LI(...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))

#endif //ANDROIDTHINK_LOG_H
