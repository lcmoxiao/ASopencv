#include <jni.h>
#include <string>
#include <bitset>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_wocaowocao_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jint JNICALL Java_com_example_wocaowocao_base_CMD_loadFile
        (JNIEnv *env, jclass obj, jstring filePath)
{
    const char* testfilePath = env->GetStringUTFChars(filePath, nullptr);
    if(testfilePath == nullptr ) {
        return -1;
    }
        /*********************************************/
        //read and operate the file, or doing something
        /*********************************************/
    env->ReleaseStringUTFChars(filePath, testfilePath);

    return  0;

}