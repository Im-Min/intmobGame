#ifndef PACMAN_GHOST_H
#define PACMAN_GHOST_H

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>


class Ghost {
public:
    Ghost();
    void move();
    void draw(float *mvpMatrix);
    void setBounds(float left, float right, float bottom, float top);
    void setPosition(float x, float y);
    void setDirection(int direction);
    Position getPosition();



private:
    Position position;
    int direction;
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


#endif //PACMAN_GHOST_H
