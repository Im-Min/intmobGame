#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
#include <poll.h>
#include <stdbool.h>
#include <limits.h>
#include <string.h>
#include <sys/time.h>
#include <errno.h>
#include <stdlib.h>

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


char* concat(const char *s1, const char *s2)
{
    char * result = (char*)malloc(strlen(s1) + strlen(s2) + 1); // +1 for the null-terminator
    // in real code you would check for errors in malloc here
    strcpy(result, s1);
    strcat(result, s2);
    return result;
}

char* devinput(const char* x){
    const char* INPUT = "/dev/input/";
    return concat(INPUT, x);
}

JNIEXPORT jstring JNICALL Java_com_example_intmob_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject mainActivity, jstring event) {
    int fd;

    const char* eventname = env->GetStringUTFChars(event, 0);
    const char* path = devinput(eventname);

    fd = open(path, O_RDONLY);

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