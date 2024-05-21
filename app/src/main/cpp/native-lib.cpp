#include <jni.h>
#include <string>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <unistd.h>
#include <poll.h>
#include <stdbool.h>
#include <limits.h>
#include <string.h>
#include <stdio.h>
#include <sys/time.h>

struct input_event {
    struct timeval time;
    unsigned short type;
    unsigned short code;
    unsigned int value;
};

extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_intmob_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    int fd;
    int ret;
    char path[] = "/dev/input/event4";
    struct pollfd fds[1];
    fds[0].fd = open(path, O_RDONLY | O_NONBLOCK);
    if (fds[0].fd < 0) {
        return env->NewStringUTF("error unable open for reading");
    }

    const int input_size = sizeof(struct input_event);
    const int timeout_ms = -1;
    struct input_event *input_data;
    memset(input_data, 0, input_size);
    fds[0].events = POLLIN;


    ret = poll(fds, 1, timeout_ms);
    if (ret < 0) {
        return env->NewStringUTF("timeout\n");

    }

    if (!fds[0].revents) {
        return env->NewStringUTF("error\n");

    }

    ssize_t r = read(fds[0].fd, input_data, input_size);

    if (r < 0) {
        return env->NewStringUTF("error");
    }

    printf("time=%ld.%06lu type=%hu code=%hu value=%u\n", input_data->time.tv_sec,
           input_data->time.tv_usec, input_data->type, input_data->code, input_data->value);
    char result[1];
    result[0] = '0' + (input_data->code-1);
    return env->NewStringUTF(result);
}
