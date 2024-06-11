#include "Ghost.h"
#include <cmath>
#include <cstdlib>
#include <ctime>

Ghost::Ghost() {
    // Initialize vertex buffer for ghost shape
    speed = 0.007f;
    direction = -1;

    initShaders();

    GLfloat vertices[] = {        // Body
            -0.02f + position.x, 0.07f + position.y,
            -0.02f + position.x, 0.06f + position.y,
            -0.04f + position.x, 0.06f + position.y,
            -0.04f + position.x, 0.05f + position.y,
            -0.05f + position.x, 0.05f + position.y,
            -0.05f + position.x, 0.04f + position.y,
            -0.06f + position.x, 0.04f + position.y,
            -0.06f + position.x,  0.01f + position.y,
            -0.07f + position.x,  0.01f + position.y,
            -0.07f + position.x,  -0.07f + position.y,
            -0.06f + position.x,  -0.07f + position.y,
            -0.06f + position.x,  -0.06f + position.y,
            -0.06f + position.x,  -0.07f + position.y,
            -0.06f + position.x,  -0.06f + position.y,
            -0.05f + position.x,  -0.06f + position.y,
            -0.05f + position.x,  -0.05f + position.y,
            -0.04f + position.x,  -0.05f + position.y,
            -0.04f + position.x,  -0.06f + position.y,
            -0.03f + position.x,  -0.06f + position.y,
            -0.03f + position.x,  -0.07f + position.y,
            -0.01f + position.x,  -0.07f + position.y,
            -0.01f + position.x,  -0.05f + position.y,

            0.01f + position.x,  -0.05f + position.y,
            0.01f + position.x,  -0.07f + position.y,
            0.03f + position.x,  -0.07f + position.y,
            0.03f + position.x,  -0.06f + position.y,
            0.04f + position.x,  -0.06f + position.y,
            0.04f + position.x,  -0.05f + position.y,
            0.05f + position.x,  -0.05f + position.y,
            0.05f + position.x,  -0.06f + position.y,
            0.06f + position.x,  -0.06f + position.y,
            0.06f + position.x,  -0.07f + position.y,
            0.06f + position.x,  -0.06f + position.y,
            0.06f + position.x,  -0.07f + position.y,
            0.07f + position.x,  -0.07f + position.y,
            0.07f + position.x,  0.01f + position.y,
            0.06f + position.x,  0.01f + position.y,
            0.06f + position.x, 0.04f + position.y,
            0.05f + position.x, 0.04f + position.y,
            0.05f + position.x, 0.05f + position.y,
            0.04f + position.x, 0.05f + position.y,
            0.04f + position.x, 0.06f + position.y,
            0.02f + position.x, 0.06f + position.y,
            0.02f + position.x, 0.07f + position.y,

            // Left Eye
            -0.05f + position.x,  0.04f + position.y,
            -0.05f + position.x,  0.03f + position.y,
            -0.06f + position.x,  0.03f + position.y,
            -0.06f + position.x,  0.00f + position.y,
            -0.05f + position.x,  0.00f + position.y,
            -0.05f + position.x,  -0.01f + position.y,
            -0.03f + position.x,  -0.01f + position.y,
            -0.03f + position.x,  0.00f + position.y,
            -0.02f + position.x,  0.00f + position.y,
            -0.02f + position.x,  0.03f + position.y,
            -0.03f + position.x,  0.03f + position.y,
            -0.03f + position.x,  0.04f + position.y,

            // Right Eye
            -0.05f + position.x,  0.04f + position.y,
            -0.05f + position.x,  0.03f + position.y,
            -0.06f + position.x,  0.03f + position.y,
            -0.06f + position.x,  0.00f + position.y,
            -0.05f + position.x,  0.00f + position.y,
            -0.05f + position.x,  -0.01f + position.y,
            -0.03f + position.x,  -0.01f + position.y,
            -0.03f + position.x,  0.00f + position.y,
            -0.02f + position.x,  0.00f + position.y,
            -0.02f + position.x,  0.03f + position.y,
            -0.03f + position.x,  0.03f + position.y,
            -0.03f + position.x,  0.04f + position.y,

            -0.06f + position.x,  0.02f + position.y,
            -0.06f + position.x,  0.00f + position.y,
            -0.04f + position.x,  0.00f + position.y,
            -0.04f + position.x,  0.02f + position.y,

            -0.0f + position.x,  0.02f + position.y,
            -0.0f + position.x,  0.00f + position.y,
            0.02f + position.x,  0.00f + position.y,
            0.02f + position.x,  0.02f + position.y,
    };

    for (int i = 112; i < 136 ; i+=2) {
        vertices[i]+=0.06f;
    }

    glGenBuffers(1, &vertexBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
}

void Ghost::initShaders() {
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

GLuint Ghost::loadShader(GLenum type, const char* shaderSrc) {
    GLuint shader = glCreateShader(type);
    glShaderSource(shader, 1, &shaderSrc, nullptr);
    glCompileShader(shader);

    GLint compiled;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
    if (!compiled) {
        GLint infoLen = 0;
        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
        if (infoLen > 0) {
            char *infoLog = (char *)malloc(infoLen);
            glGetShaderInfoLog(shader, infoLen, nullptr, infoLog);
            glDeleteShader(shader);
            free(infoLog);
        }
        return 0;
    }
    return shader;
}

void Ghost::draw(float *mvpMatrix) {
    glUseProgram(shaderProgram);

    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glVertexAttribPointer(positionHandle, 2, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(positionHandle);

    glUniformMatrix4fv(mvpMatrixHandle, 1, GL_FALSE, mvpMatrix);

    glUniform4f(colorHandle, 1.0f, 0.0f, 0.0f, 1.0f); // Red color for the body
    glDrawArrays(GL_TRIANGLE_FAN, 0, 44);

    glUniform4f(colorHandle, 1.0f, 1.0f, 1.0f, 1.0f); // White color for the eyes
    glDrawArrays(GL_TRIANGLE_FAN, 44, 12);

    glUniform4f(colorHandle, 1.0f, 1.0f, 1.0f, 1.0f); // White color for the eyes
    glDrawArrays(GL_TRIANGLE_FAN, 56, 12);

    glUniform4f(colorHandle, 0.0f, 0.439f, 0.710f, 1.0f); // White color for the eyes
    glDrawArrays(GL_TRIANGLE_FAN, 68, 4);

    glUniform4f(colorHandle, 0.0f, 0.439f, 0.710f, 1.0f); // White color for the eyes
    glDrawArrays(GL_TRIANGLE_FAN, 72, 4);

    glDisableVertexAttribArray(positionHandle);
}

void Ghost::move() {
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

void Ghost::setBounds(float left, float right, float bottom, float top) {
    leftBound = left;
    rightBound = right;
    bottomBound = bottom;
    topBound = top;
}

void Ghost::setPosition(float x, float y) {
    position.x = x;
    position.y = y;
}

void Ghost::updateVertices() {

    GLfloat vertices[] = {
            -0.02f + position.x, 0.07f + position.y,
            -0.02f + position.x, 0.06f + position.y,
            -0.04f + position.x, 0.06f + position.y,
            -0.04f + position.x, 0.05f + position.y,
            -0.05f + position.x, 0.05f + position.y,
            -0.05f + position.x, 0.04f + position.y,
            -0.06f + position.x, 0.04f + position.y,
            -0.06f + position.x,  0.01f + position.y,
            -0.07f + position.x,  0.01f + position.y,
            -0.07f + position.x,  -0.07f + position.y,
            -0.06f + position.x,  -0.07f + position.y,
            -0.06f + position.x,  -0.06f + position.y,
            -0.06f + position.x,  -0.07f + position.y,
            -0.06f + position.x,  -0.06f + position.y,
            -0.05f + position.x,  -0.06f + position.y,
            -0.05f + position.x,  -0.05f + position.y,
            -0.04f + position.x,  -0.05f + position.y,
            -0.04f + position.x,  -0.06f + position.y,
            -0.03f + position.x,  -0.06f + position.y,
            -0.03f + position.x,  -0.07f + position.y,
            -0.01f + position.x,  -0.07f + position.y,
            -0.01f + position.x,  -0.05f + position.y,

            0.01f + position.x,  -0.05f + position.y,
            0.01f + position.x,  -0.07f + position.y,
            0.03f + position.x,  -0.07f + position.y,
            0.03f + position.x,  -0.06f + position.y,
            0.04f + position.x,  -0.06f + position.y,
            0.04f + position.x,  -0.05f + position.y,
            0.05f + position.x,  -0.05f + position.y,
            0.05f + position.x,  -0.06f + position.y,
            0.06f + position.x,  -0.06f + position.y,
            0.06f + position.x,  -0.07f + position.y,
            0.06f + position.x,  -0.06f + position.y,
            0.06f + position.x,  -0.07f + position.y,
            0.07f + position.x,  -0.07f + position.y,
            0.07f + position.x,  0.01f + position.y,
            0.06f + position.x,  0.01f + position.y,
            0.06f + position.x, 0.04f + position.y,
            0.05f + position.x, 0.04f + position.y,
            0.05f + position.x, 0.05f + position.y,
            0.04f + position.x, 0.05f + position.y,
            0.04f + position.x, 0.06f + position.y,
            0.02f + position.x, 0.06f + position.y,
            0.02f + position.x, 0.07f + position.y,


            // Left Eye
            -0.05f + position.x,  0.04f + position.y,
            -0.05f + position.x,  0.03f + position.y,
            -0.06f + position.x,  0.03f + position.y,
            -0.06f + position.x,  0.00f + position.y,
            -0.05f + position.x,  0.00f + position.y,
            -0.05f + position.x,  -0.01f + position.y,
            -0.03f + position.x,  -0.01f + position.y,
            -0.03f + position.x,  0.00f + position.y,
            -0.02f + position.x,  0.00f + position.y,
            -0.02f + position.x,  0.03f + position.y,
            -0.03f + position.x,  0.03f + position.y,
            -0.03f + position.x,  0.04f + position.y,

            // Right Eye
            -0.05f + position.x,  0.04f + position.y,
            -0.05f + position.x,  0.03f + position.y,
            -0.06f + position.x,  0.03f + position.y,
            -0.06f + position.x,  0.00f + position.y,
            -0.05f + position.x,  0.00f + position.y,
            -0.05f + position.x,  -0.01f + position.y,
            -0.03f + position.x,  -0.01f + position.y,
            -0.03f + position.x,  0.00f + position.y,
            -0.02f + position.x,  0.00f + position.y,
            -0.02f + position.x,  0.03f + position.y,
            -0.03f + position.x,  0.03f + position.y,
            -0.03f + position.x,  0.04f + position.y,

            -0.06f + position.x,  0.02f + position.y,
            -0.06f + position.x,  0.00f + position.y,
            -0.04f + position.x,  0.00f + position.y,
            -0.04f + position.x,  0.02f + position.y,

            -0.0f + position.x,  0.02f + position.y,
            -0.0f + position.x,  0.00f + position.y,
            0.02f + position.x,  0.00f + position.y,
            0.02f + position.x,  0.02f + position.y,
    };

    for (int i = 112; i < 136 ; i+=2) {
        vertices[i]+=0.06f;
    }

    glGenBuffers(1, &vertexBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
}

void Ghost::setDirection(int newDirection) {
    direction = newDirection;
}

Position Ghost::getPosition() {
    return position;
}
