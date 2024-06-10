#include <jni.h>
#include <android/native_window_jni.h>
#include <GLES2/gl2.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <string>
#include <string.h>
#include "Pacman.h"
#include "Pacman.cpp"
#include "Ghost.h"
#include "Ghost.cpp"

Pacman *pacman;
Ghost *ghost;

float projectionMatrix[16];
float viewMatrix[16];
float modelMatrix[16];
float mvpMatrix[16];


void multiplyMatrices(float *result, const float *a, const float *b) {
    for (int i = 0; i < 4; ++i) {
        for (int j = 0; j < 4; ++j) {
            result[i * 4 + j] = 0.0f;
            for (int k = 0; k < 4; ++k) {
                result[i * 4 + j] += a[i * 4 + k] * b[k * 4 + j];
            }
        }
    }
}

void setIdentityMatrix(float *matrix) {
    memset(matrix, 0, 16 * sizeof(float));
    matrix[0] = 1.0f;
    matrix[5] = 1.0f;
    matrix[10] = 1.0f;
    matrix[15] = 1.0f;
}

void setOrthographicMatrix(float *matrix, float left, float right, float bottom, float top, float near, float far) {
    setIdentityMatrix(matrix);
    matrix[0] = 2.0f / (right - left);
    matrix[5] = 2.0f / (top - bottom);
    matrix[10] = -2.0f / (far - near);
    matrix[12] = -(right + left) / (right - left);
    matrix[13] = -(top + bottom) / (top - bottom);
    matrix[14] = -(far + near) / (far - near);
}


extern "C" JNIEXPORT void JNICALL
Java_com_example_intmob_MainActivity_init(JNIEnv* env, jobject /* this */) {
    // OpenGL ES 초기화 코드
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    pacman = new Pacman();
    ghost = new Ghost();
    ghost->setPosition(0.0f, 0.5f);

    setIdentityMatrix(projectionMatrix);
    setIdentityMatrix(viewMatrix);
    setIdentityMatrix(modelMatrix);
    setIdentityMatrix(mvpMatrix);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_intmob_MainActivity_step(JNIEnv* env, jobject /* this */) {
    float tempMatrix[16];
    multiplyMatrices(tempMatrix, viewMatrix, modelMatrix);
    multiplyMatrices(mvpMatrix, projectionMatrix, tempMatrix);

    // Pacman 렌더링 및 게임 로직 업데이트 코드
    glClear(GL_COLOR_BUFFER_BIT);
    pacman->move();
    pacman->draw(mvpMatrix);

    ghost->move();
    ghost->draw(mvpMatrix);
}



extern "C"
JNIEXPORT void JNICALL
Java_com_example_intmob_MainActivity_setOrthographicMatrix(JNIEnv *env, jobject thiz, jint width, jint height) {
    // TODO: implement setOrthographicMatrix()
    //setOrthographicMatrix(projectionMatrix, -1.0f, 1.0f, -1.0f * height / width, 1.0f * height / width, -1.0f, 1.0f);
    /*float ratio = (float)height / (float)width;
    setOrthographicMatrix(projectionMatrix, -1.0f, 1.0f, -1.0f * ratio, 1.0f * ratio, -1.0f, 1.0f);
*/    //pacman->setBounds(ratio);
    float aspectRatio = (float)width / (float)height;
    float left, right, bottom, top;
    if (aspectRatio > 1.0f) {
        left = -aspectRatio;
        right = aspectRatio;
        bottom = -1.0f;
        top = 1.0f;
    } else {
        left = -1.0f;
        right = 1.0f;
        bottom = -1.0f / aspectRatio;
        top = 1.0f / aspectRatio;
    }
    setOrthographicMatrix(projectionMatrix, left, right, bottom, top, -1.0f, 1.0f);
    pacman->setBounds(left, right, bottom, top);
    ghost->setBounds(left, right, bottom, top);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_intmob_MainActivity_setDirection(JNIEnv *env, jobject thiz, jint direction) {
    // TODO: implement setDirection()
    pacman->setDirection(direction);
}