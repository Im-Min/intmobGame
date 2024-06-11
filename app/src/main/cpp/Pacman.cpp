#include "Pacman.h"
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <GLES2/gl2platform.h>
#include <malloc.h>
#include <math.h>

const char * vertexShaderSrc =
        "attribute vec4 a_Position; \n"
        "uniform mat4 u_MVPMatrix; \n"
        "void main() \n"
        "{ \n"
        "   gl_Position = u_MVPMatrix * a_Position; \n"
        "} \n";

const char* fragmentShaderSrc =
        "precision mediump float; \n"
        "uniform vec4 u_Color; \n"
        "void main() \n"
        "{ \n"
        "   gl_FragColor = u_Color; \n"
        "} \n";

Pacman::Pacman() {
    position.x = 0.0f;
    position.y = 0.0f;
    numSegments = 100;
    speed = 0.007f;
    direction = -1;
    initShaders();

    radius = 0.07f;

    GLfloat vertices[(numSegments+2)*3];

    vertices[0] = position.x;
    vertices[1] = position.y;
    vertices[2] = 0.0f;

    for (int i = 1; i <= numSegments+1; ++i) {
        float angle = 2.0f * M_PI * float(i-1) / float(numSegments);
        float dx = radius * cosf(angle);
        float dy = radius * sinf(angle);

        vertices[3*i] = position.x + dx;
        vertices[3*i+1] = position.y + dy;
        vertices[3*i+2] = 0.0f;
    }

    glGenBuffers(1, &vertexBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
}

void Pacman::setDirection(int newDirection) {
    direction = newDirection;
}

void Pacman::move() {
    switch (direction) {
        case 0:
            if (position.y > topBound) position.y = topBound;
            else position.y += speed;
            break;
        case 1:
            if (position.y < bottomBound) position.y = bottomBound;
            else position.y -= speed;
            break;
        case 2:
            if (position.x < leftBound) position.x = leftBound;
            else position.x -= speed;
            break;
        case 3:
            if (position.x > rightBound) position.x = rightBound;
            else position.x += speed;
            break;
    }
    updateVertices();
}

void Pacman::draw(float *mvpMatrix) {
    glUseProgram(shaderProgram);

    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glVertexAttribPointer(positionHandle, 3, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(positionHandle);

    glUniformMatrix4fv(mvpMatrixHandle, 1, GL_FALSE, mvpMatrix);
    glUniform4f(colorHandle, 1.0f, 1.0f, 0.0f, 1.0f);

    glDrawArrays(GL_TRIANGLE_FAN, 0, numSegments+2);
    glDisableVertexAttribArray(positionHandle);
}

Position Pacman::getPosition() {
    return position;
}

void Pacman::initShaders() {
    GLuint vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderSrc);
    GLuint fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderSrc);

    shaderProgram = glCreateProgram();
    glAttachShader(shaderProgram, vertexShader);
    glAttachShader(shaderProgram, fragmentShader);
    glLinkProgram(shaderProgram);

    GLint linked;
    glGetProgramiv(shaderProgram, GL_LINK_STATUS, &linked);

    positionHandle = glGetAttribLocation(shaderProgram, "a_Position");
    colorHandle = glGetUniformLocation(shaderProgram, "u_Color");
    mvpMatrixHandle = glGetUniformLocation(shaderProgram, "u_MVPMatrix");
}

GLuint Pacman::loadShader(GLenum type, const char *shaderSrc) {
    GLuint shader = glCreateShader(type);
    glShaderSource(shader, 1, &shaderSrc, nullptr);
    glCompileShader(shader);

    GLint compiled;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
    if(!compiled) {
        GLint infoLen = 0;
        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
        if (infoLen > 0) {
            char *infoLog = (char*)malloc(infoLen);
            glGetShaderInfoLog(shader, infoLen, nullptr, infoLog);
            glDeleteShader(shader);
            free(infoLog);
        }
        return 0;
    }
    return shader;
}


void Pacman::updateVertices() {
    GLfloat vertices[(numSegments + 2) * 3];

    vertices[0] = position.x; // 중심 x 좌표
    vertices[1] = position.y; // 중심 y 좌표
    vertices[2] = 0.0f; // 중심 z 좌표

    for (int i = 1; i <= numSegments + 1; ++i) {
        float angle = 2.0f * M_PI * float(i - 1) / float(numSegments);
        float dx = radius * cosf(angle);
        float dy = radius * sinf(angle);

        vertices[3 * i] = position.x + dx;
        vertices[3 * i + 1] = position.y + dy;
        vertices[3 * i + 2] = 0.0f;
    }

    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
}

void Pacman::setBounds(float left, float right, float bottom, float top) {
    leftBound = left;
    rightBound = right;
    bottomBound = bottom;
    topBound = top;
}
