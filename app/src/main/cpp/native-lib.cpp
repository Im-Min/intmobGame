#include <jni.h>
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
#include <errno.h>

typedef struct input_event {
    struct timeval time;
    unsigned short type;
    unsigned short code;
    unsigned int value;
} input_event;

#ifdef __cplusplus
extern "C"
{
#endif

JNIEXPORT jstring JNICALL Java_com_example_intmob_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject mainActivity) {
    int fd;
    const char *EVENT;

    EVENT = "/dev/input/event4";

    fd = open(EVENT, O_RDONLY);

    if (fd < 0) {
        char* errmsg = strerror(errno);
        return env->NewStringUTF(errmsg);
    }

    const int input_size = sizeof(input_event);

    input_event input_data;

    ssize_t r = read(fd, &input_data, input_size);

    if (r < 0) {
        char* errmsg = strerror(errno);
        return env->NewStringUTF(errmsg);
    }

    //printf("time=%ld.%06lu type=%hu code=%hu value=%u\n", input_data->time.tv_sec,input_data->time.tv_usec, input_data->type, input_data->code, input_data->value);

    char result[1];
    result[0] = '0' + (input_data.code - 1);
    return env->NewStringUTF(result);
}

JNIEXPORT void JNICALL Java_com_example_intmob_MainActivity_div0(JNIEnv *env, jobject mainActivity){
    int i = 0;
    i /= i;
}

#ifdef __cplusplus
}
#endif