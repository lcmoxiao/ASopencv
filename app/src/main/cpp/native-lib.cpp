#include <jni.h>
#include <string>
#include <bitset>
#include <iostream>
#include<android/log.h>
#include <iconv.h>
#include <sys/types.h>
#include <unistd.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_wocaowocao_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}




