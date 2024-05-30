#include <jni.h>
#include <GLES2/gl2.h>
#include <android/log.h>
#include <string>
#include <malloc.h>

#define LOG_TAG "PacmanNative"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

GLuint program;
GLuint vPosition;

GLuint loadShader(GLenum shaderType, const char* shaderSource) {
    GLuint shader = glCreateShader(shaderType);
    glShaderSource(shader, 1, &shaderSource, nullptr);
    glCompileShader(shader);
    GLint compiled;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
    if (!compiled) {
        GLint infoLen = 0;
        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
        if (infoLen) {
            char* buf = (char*) malloc(infoLen);
            if (buf) {
                glGetShaderInfoLog(shader, infoLen, nullptr, buf);
                LOGD("Could not compile shader %d:\n%s\n", shaderType, buf);
                free(buf);
            }
            glDeleteShader(shader);
            shader = 0;
        }
    }
    return shader;
}

GLuint createProgram(const char* vertexSource, const char* fragmentSource) {
    GLuint vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource);
    if (!vertexShader) {
        return 0;
    }

    GLuint fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource);
    if (!fragmentShader) {
        return 0;
    }

    GLuint program = glCreateProgram();
    if (program) {
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        GLint linkStatus = GL_FALSE;
        glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);
        if (linkStatus != GL_TRUE) {
            GLint infoLen = 0;
            glGetProgramiv(program, GL_INFO_LOG_LENGTH, &infoLen);
            if (infoLen) {
                char* buf = (char*) malloc(infoLen);
                if (buf) {
                    glGetProgramInfoLog(program, infoLen, nullptr, buf);
                    LOGD("Could not link program:\n%s\n", buf);
                    free(buf);
                }
            }
            glDeleteProgram(program);
            program = 0;
        }
    }
    return program;
}

const char* vertexShaderSource = R"(
attribute vec4 vPosition;
void main() {
    gl_Position = vPosition;
}
)";

const char* fragmentShaderSource = R"(
precision mediump float;
void main() {
    gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
}
)";

extern "C"
JNIEXPORT void JNICALL
Java_com_example_intmob_GLRenderer_nativeInit(JNIEnv *env, jobject thiz) {
    // TODO: implement nativeInit()
    program = createProgram(vertexShaderSource, fragmentShaderSource);
    if (!program) {
        LOGD("Could not create program.");
        return;
    }
    vPosition = glGetAttribLocation(program, "vPosition");
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_intmob_GLRenderer_nativeResize(JNIEnv *env, jobject thiz, jint width,
                                                jint height) {
    // TODO: implement nativeResize()
    glViewport(0, 0, width, height);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_intmob_GLRenderer_nativeRender(JNIEnv *env, jobject thiz) {
    // TODO: implement nativeRender()
    glClear(GL_COLOR_BUFFER_BIT);
    glUseProgram(program);

    GLfloat vertices[] = {
            0.0f,  0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f
    };

    glVertexAttribPointer(vPosition, 2, GL_FLOAT, GL_FALSE, 0, vertices);
    glEnableVertexAttribArray(vPosition);
    glDrawArrays(GL_TRIANGLES, 0, 3);
    glDisableVertexAttribArray(vPosition);
}