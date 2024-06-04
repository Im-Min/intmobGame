//
// Created by imtjr on 2024-05-31.
//
#include <GLES2/gl2.h>
#ifndef PACMAN_PACMAN_H
#define PACMAN_PACMAN_H

struct Position {
    float x,y;
};

class Pacman {
public:
    Pacman();
    void setDirection(int direction);
    void move();
    void draw(float *mvpMatrix);
    void setBounds(float left, float right, float bottom, float top);
    Position getPosition();

private:
    Position position;
    int numSegments;
    int direction;
    float radius;
    float speed;
    float leftBound, rightBound, topBound, bottomBound;
    GLuint shaderProgram;
    GLuint vertexBuffer;
    GLuint positionHandle;
    GLuint colorHandle;
    GLuint mvpMatrixHandle;

    void updateVertices();
    void initShaders();
    GLuint loadShader(GLenum type, const char* shaderSrc);

};


#endif //PACMAN_PACMAN_H
