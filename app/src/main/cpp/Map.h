#ifndef PACMAN_MAP_H
#define PACMAN_MAP_H
#include <GLES2/gl2.h>

#define MAP_WIDTH 28
#define MAP_HEIGHT 31

enum CellType {
    EMPTY,
    WALL,
    DOT,
    POWER_PELLET,
    GHOST_HOUSE
};

class Map {
public:
    Map();
    void drawCell(float x, float y, float r, float g, float b);
    void drawMap(float *mvpMatrix);

private:
    CellType map[MAP_HEIGHT][MAP_WIDTH];
    GLuint shaderProgram;
    GLuint positionHandle;
    GLuint colorHandle;
    GLuint mvpMatrixHandle;

    void initShaders();
    void updateVertices();
    GLuint loadShader(GLenum type, const char* shaderSrc);
};

#endif // PACMAN_MAP_H
