#include <jni.h>
#include <string>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>

extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_intmob_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    int fd;
    int data;
    fd = open("/dev/fpga_dipsw", O_RDONLY);
    data = 0x0;
    read(fd, &data, 4);
    close(fd);
    char strr[11];
    sprintf(strr, "%d", data);
    //std::string hello = "Hello from C++";
    //return env->NewStringUTF(hello.c_str());
    return env->NewStringUTF(strr);
}
