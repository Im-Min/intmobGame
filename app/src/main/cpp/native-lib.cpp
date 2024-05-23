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
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <sys/stat.h>

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


const char* concat(const char *s1, const char *s2)
{
    char * result = (char*)malloc(strlen(s1) + strlen(s2) + 1); // +1 for the null-terminator
    // in real code you would check for errors in malloc here
    strcpy(result, s1);
    strcat(result, s2);
    return result;
}

const char* concatd(const char* eventname){
    const char* INPUT = "/dev/input/";
    return concat(INPUT, eventname);
}

int chmod777(const char* path){
        char mode[] = "0777";
        int i;
        i = strtol(mode, 0, 8);
        if (chmod (path,i) < 0)
        {
            return errno;
        }
        return(0);
}

JNIEXPORT jstring JNICALL Java_com_example_intmob_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject mainActivity, jstring event) {
    int fd;

    const char* eventname = env->GetStringUTFChars(event, 0);
    const char* path = concatd(eventname);

    fd = open(path, O_RDONLY);

    if (fd < 0) {
        const char* errmsg = concat("open:", strerror(errno));
        return env->NewStringUTF(errmsg);
    }

    const int input_size = sizeof(input_event);

    input_event input_data;

    ssize_t r = read(fd, &input_data, input_size);

    if (r < 0) {
        const char* errmsg = concat("read:", strerror(errno));
        return env->NewStringUTF(errmsg);
    }

    //printf("time=%ld.%06lu type=%hu code=%hu value=%u\n", input_data->time.tv_sec,input_data->time.tv_usec, input_data->type, input_data->code, input_data->value);

    char result[2];
    result[0] = '0' + (input_data.code - 1);
    result[1] = '\0';
    return env->NewStringUTF(result);
}

JNIEXPORT void JNICALL Java_com_example_intmob_MainActivity_div0(JNIEnv *env, jobject mainActivity){
    int i = 0;
    i /= i;
}

#ifdef __cplusplus
}
#endif