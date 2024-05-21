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
//    fd = open("/dev/fpga_dipsw", O_RDONLY);
    fd = open("/dev/input/event4", O_RDONLY);
    data = 0x0;
    read(fd, &data, 1);
    close(fd);
    char strr[11];
    sprintf(strr, "%d", data);

//    char data=0;
//    fd = open("/dev/input/", O_RDONLY);
//    read(fd, &data, 1)


    //std::string hello = "Hello from C++";
    //return env->NewStringUTF(hello.c_str());

    return env->NewStringUTF(strr);
}
