//
// Created on 2024-06-18.
//

#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <sys/mman.h>
#include <android/log.h>

jint
Java_com_example_intmob_Segment_SegmentControl(JNIEnv* env,
                                                    jobject thiz, jstring data){
    int dev, ret;
    dev = open("/dev/fpga_segment", O_RDWR|O_SYNC);

    if(dev != -1){

        const char *nativeString = (*env)->GetStringUTFChars(env, data, 0);

        // use your string
        ret = write(dev, nativeString, 6);
        close(dev);

        (*env)->ReleaseStringUTFChars(env, data, nativeString);

    } else{
        __android_log_print(ANDROID_LOG_ERROR, "SegmentActivity", "Device Open ERROR!\n");
        exit(1);
    }
    return 0;
}
