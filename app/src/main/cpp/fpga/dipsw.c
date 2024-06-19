//
// Created on 2024-06-20.
//
#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <sys/mman.h>
#include <errno.h>
#include <android/log.h>

#define DIPSW "/dev/fpga_dipsw"

JNIEXPORT jint JNICALL Java_com_example_intmob_fpga_DipSW_GetValue
        (JNIEnv *env, jobject obj) {
    int ret;
    int data = 0;
    int fd;

    fd = open(DIPSW, O_RDONLY);
    if (fd < 0)
        return -errno;


    ret = read(fd, &data, 2);
    if(ret == 2) {
        close(fd);
        return data;
    }

    return -127;
}
