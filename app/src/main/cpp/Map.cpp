#include "Map.h"
#include <GLES2/gl2.h>
#include <cstring>

Map::Map() {
    initShaders();

    CellType initialMap[MAP_HEIGHT][MAP_WIDTH] = {
            {WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL},
            {WALL, DOT, DOT, DOT, DOT, DOT, DOT, DOT, DOT, DOT, DOT, DOT, WALL, WALL, DOT, DOT, DOT, DOT, DOT, DOT, DOT, DOT, DOT, DOT, DOT, DOT, DOT, WALL},
            {WALL, DOT, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, DOT, WALL, WALL, DOT, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, DOT, WALL, WALL, WALL},
            {WALL, POWER_PELLET, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, DOT, WALL, WALL, DOT, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, POWER_PELLET, WALL, WALL, WALL},
            {WALL, DOT, WALL, EMPTY, WALL, WALL, WALL, EMPTY, WALL, EMPTY, WALL, DOT, WALL, WALL, DOT, WALL, EMPTY, WALL, EMPTY, WALL, WALL, WALL, EMPTY, WALL, EMPTY, WALL, DOT, WALL},
            {WALL, DOT, WALL, EMPTY, WALL, EMPTY, WALL, EMPTY, WALL, EMPTY, WALL, DOT, WALL, WALL, DOT, WALL, EMPTY, WALL, EMPTY, WALL, EMPTY, WALL, EMPTY, WALL, EMPTY, WALL, DOT, WALL},
            {WALL, DOT, WALL, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, DOT, WALL, WALL, DOT, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, WALL, EMPTY, WALL, DOT, WALL},
            {WALL, DOT, WALL, EMPTY, WALL, WALL, WALL, WALL, WALL, WALL, WALL, DOT, WALL, WALL, DOT, WALL, WALL, WALL, WALL, WALL, WALL, WALL, EMPTY, WALL, EMPTY, WALL, DOT, WALL},
            {WALL, DOT, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, DOT, WALL, WALL, DOT, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, WALL, DOT, WALL},
            {WALL, DOT, WALL, EMPTY, WALL, WALL, WALL, WALL, WALL, WALL, WALL, DOT, WALL, WALL, DOT, WALL, WALL, WALL, WALL, WALL, WALL, WALL, EMPTY, WALL, EMPTY, WALL, DOT, WALL},
            {WALL, DOT, WALL, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, DOT, WALL, WALL, DOT, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, WALL, EMPTY, WALL, DOT, WALL},
            {WALL, DOT, WALL, EMPTY, WALL, EMPTY, WALL, WALL, WALL, EMPTY, WALL, DOT, WALL, WALL, DOT, WALL, EMPTY, WALL, WALL, WALL, EMPTY, WALL, EMPTY, WALL, EMPTY, WALL, DOT, WALL},
            {WALL, DOT, WALL, EMPTY, WALL, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL, DOT, WALL, WALL, DOT, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, WALL, EMPTY, WALL, EMPTY, WALL, DOT, WALL},
            {WALL, POWER_PELLET, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, WALL, EMPTY, EMPTY, DOT, WALL, WALL, DOT, EMPTY, EMPTY, WALL, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL, POWER_PELLET, WALL, WALL, WALL},
            {WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL},
            {WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL},
            {WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL},
            {WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL},
            {WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL},
            {WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL},
            {WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL},
            {WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL},
            {WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL},
            {WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL},
            {WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL},
            {WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL},
            {WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL, EMPTY, EMPTY, EMPTY, WALL},
            {WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL}
    };

    memcpy(map, initialMap, sizeof(initialMap));
}

void Map::drawCell(float x, float y, float r, float g, float b) {
    GLfloat vertices[] = {
            x, y,
            x + (2.0f / MAP_WIDTH), y,
            x + (2.0f / MAP_WIDTH), y + (2.0f / MAP_HEIGHT),
            x, y + (2.0f / MAP_HEIGHT)
    };

    glVertexAttribPointer(positionHandle, 2, GL_FLOAT, GL_FALSE, 0, vertices);
    glEnableVertexAttribArray(positionHandle);
    glUniform4f(colorHandle, r, g, b, 1.0f);
    glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
}

void Map::drawMap(float *mvpMatrix) {
    glUseProgram(shaderProgram);
    glUniformMatrix4fv(mvpMatrixHandle, 1, GL_FALSE, mvpMatrix);

    for (int y = 0; y < MAP_HEIGHT; ++y) {
        for (int x = 0; x < MAP_WIDTH; ++x) {
            float posX = (x / (float)MAP_WIDTH) * 2.0f - 1.0f;
            float posY = (y / (float)MAP_HEIGHT) * 2.0f - 1.0f;

            switch (map[y][x]) {
                case WALL:
                    drawCell(posX, posY, 0.0f, 0.0f, 1.0f);
                    break;
                case DOT:
                    drawCell(posX, posY, 1.0f, 1.0f, 1.0f);
                    break;
                case POWER_PELLET:
                    drawCell(posX, posY, 1.0f, 1.0f, 0.0f);
                    break;
                case GHOST_HOUSE:
                    drawCell(posX, posY, 1.0f, 0.0f, 0.0f);
                    break;
                case EMPTY:
                default:
                    break;
            }
        }
    }
}

void Map::initShaders() {
    GLuint vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderSrc);
    GLuint fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderSrc);

    shaderProgram = glCreateProgram();
    glAttachShader(shaderProgram, vertexShader);
    glAttachShader(shaderProgram, fragmentShader);
    glLinkProgram(shaderProgram);

    GLint linked;
    glGetProgramiv(shaderProgram, GL_LINK_STATUS, &linked);

    if (!linked) {
        GLint infoLen = 0;
        glGetProgramiv(shaderProgram, GL_INFO_LOG_LENGTH, &infoLen);
        if (infoLen > 1) {
            char* infoLog = (char*) malloc(sizeof(char) * infoLen);
            glGetProgramInfoLog(shaderProgram, infoLen, NULL, infoLog);
            glDeleteProgram(shaderProgram);
            free(infoLog);
        }
    }

    positionHandle = glGetAttribLocation(shaderProgram, "a_Position");
    colorHandle = glGetUniformLocation(shaderProgram, "u_Color");
    mvpMatrixHandle = glGetUniformLocation(shaderProgram, "u_MVPMatrix");
}

GLuint Map::loadShader(GLenum type, const char *shaderSrc) {
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
            //LOGE("Error compiling shader:\n%s\n", infoLog);
            glDeleteShader(shader);
            free(infoLog);
        }
        return 0;
    }
    return shader;
}

void Map::updateVertices() {

}
