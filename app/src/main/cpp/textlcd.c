#include <jni.h>
#include <errno.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <sys/mman.h>
#include <termios.h>

#define TEXTLCD_BASE 0xbc
#define TEXTLCD_COMMAND_SET _IOW(TEXTLCD_BASE, 0, int)
#define TEXTLCD_FUNCTION_SET _IOW(TEXTLCD_BASE, 1, int)
#define TEXTLCD_DISPLAY_CONTROL _IOW(TEXTLCD_BASE, 2, int)
#define TEXTLCD_CURSOR_SHIFT _IOW(TEXTLCD_BASE, 3, int)
#define TEXTLCD_ENTRY_MODE_SET _IOW(TEXTLCD_BASE, 4, int)
#define TEXTLCD_RETURN_HOME _IOW(TEXTLCD_BASE, 5, int)
#define TEXTLCD_CLEAR _IOW(TEXTLCD_BASE, 6, int)
#define TEXTLCD_DD_ADDRESS _IOW(TEXTLCD_BASE, 7, int)
#define TEXTLCD_WRITE_BYTE _IOW(TEXTLCD_BASE, 8, int)

#define TEXTLCD "/dev/fpga_textlcd"

int fd = -1;

struct strcommand_variable{
    char rows;
    char nfonts;
    char display_enable;
    char cursor_enable;

    char nblink;
    char set_screen;
    char set_rightshit;
    char increase;
    char nshift;
    char pos;
    char command;
    char strlength;
    char buf[16];
};

static struct strcommand_variable strcommand;
static int initialized = 0;

void init(){
    if (!initialized){
        strcommand.rows = 0;
        strcommand.nfonts = 0;
        strcommand.display_enable = 1;
        strcommand.cursor_enable = 0;
        strcommand.nblink = 0;
        strcommand.set_screen = 0;
        strcommand.set_rightshit = 1;
        strcommand.increase = 1;
        strcommand.nshift = 0;
        strcommand.pos = 10;
        strcommand.command = 1;
        strcommand.strlength = 16;
        initialized = 1;
    }
}

int TextLCDIoctol(int cmd, char* buf){
    int ret, i;

    if (fd < 0) fd = open(TEXTLCD, O_WRONLY | O_NDELAY);
    if (fd < 0) return -errno;

    if (cmd == TEXTLCD_WRITE_BYTE) {
        ioctl(fd, TEXTLCD_DD_ADDRESS, &strcommand, 32);
        for (i = 0; i < strlen(buf); i++) {
            strcommand.buf[0] = buf[i];
            ret = ioctl(fd, cmd, &strcommand, 32);
        }
    }else{
        ret = ioctl(fd, cmd, &strcommand, 32);
    }
    close(fd);
    fd = -1;
    return ret;
}



JNIEXPORT jboolean
JNICALL Java_com_example_intmob_TextLCD_open
        (JNIEnv *env, jobject obj) {
    fd = open(TEXTLCD, O_WRONLY | O_NDELAY);
    if (fd < 0) return -errno;
    else return 1;
}


JNIEXPORT jint
JNICALL Java_com_example_intmob_TextLCD_control
        (JNIEnv *env, jobject obj, jstring str, jstring str2) {
    jboolean iscopy;
    char *buf0, *buf1;
    int ret;

    if (fd < 0) fd = open(TEXTLCD, O_WRONLY | O_NDELAY);
    if (fd < 0) return -errno;

    init();

    buf0 = (char *) (*env)->GetStringUTFChars(env, str, &iscopy);
    buf1 = (char *) (*env)->GetStringUTFChars(env, str2, &iscopy);

    strcommand.pos = 0;
    ioctl(fd, TEXTLCD_DD_ADDRESS, &strcommand, 32);
    ret = write(fd, buf0, strlen(buf0));

    strcommand.pos = 40;
    ioctl(fd, TEXTLCD_DD_ADDRESS, &strcommand, 32);
    ret = write(fd, buf1, strlen(buf1));

    close(fd);
    fd = -1;

    return ret;
}

JNIEXPORT jint
JNICALL Java_com_example_intmob_TextLCD_clear
        (JNIEnv *env, jobject obj) {
    init();
    return TextLCDIoctol(TEXTLCD_CLEAR, NULL);
}


JNIEXPORT jint
JNICALL Java_com_example_intmob_TextLCD_IOCtlDisplay
        (JNIEnv *env, jobject obj, jboolean data) {
    init();
    if (data) {
        strcommand.display_enable = 0x01;
    } else {
        strcommand.display_enable = 0x00;
    }
    close(fd);
    return TextLCDIoctol(TEXTLCD_DISPLAY_CONTROL, NULL);

}

JNIEXPORT jint
JNICALL Java_com_example_intmob_TextLCD_IOCtlReturnHome
        (JNIEnv *env, jobject obj) {
    init();
    return TextLCDIoctol(TEXTLCD_RETURN_HOME, NULL);
}

JNIEXPORT jint
JNICALL Java_com_example_intmob_TextLCD_IOCtlCursor
        (JNIEnv *env, jobject obj, jboolean data) {
    init();
    if (data) {
        strcommand.cursor_enable = 0x01;
    } else
        strcommand.cursor_enable = 0x00;

    return TextLCDIoctol(TEXTLCD_DISPLAY_CONTROL, NULL);
}


JNIEXPORT jint JNICALL Java_com_example_intmob_TextLCD_IOCtlBlink
        (JNIEnv *env, jobject obj, jboolean data) {
    init();
    if (data)
        strcommand.nblink = 0x01;
    else strcommand.nblink = 0x00;
    close(fd);
    return TextLCDIoctol(TEXTLCD_DISPLAY_CONTROL, NULL);

}


