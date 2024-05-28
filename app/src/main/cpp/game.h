#ifndef GAME_H
#define GAME_H

#include <vector>
#include <iostream>

class Position {
public:
	int x, y;
	Position(int x = 0, int y = 0) :x(x), y(y) {}
};

class Pacman
{
public:
	Position position;
	int score;
	void move(int dx, int dy);
	Pacman(int x, int y) :position(x, y) {}
};

class Ghost
{
public:
	Position position;
	void move(int dx, int dy);
	Ghost(int x, int y) :position(x, y) {}
};

class Cookie
{
public:
	Position position;
	Cookie(int x, int y) :position(x, y) {}
};

class GameMap
{
public:
	int width, height;
	std::vector<std::vector<int>> map;
	GameMap(int w, int h) : width(w), height(h) {
		map.resize(h, std::vector<int>(w, 0));
	}

	void initialize();
	bool isWall(int x, int y);
	void placeCookie(int x, int y);
};

class Game
{
public:
	Pacman pacman;
	std::vector<Ghost> ghosts;
	std::vector<Cookie> cookies;
	GameMap map;
	int score;

	Game() : pacman(1,1), map(10, 10), score(0) {}

	void initialize();
	void update();
	void handleInput(char input);
	void checkCollision();
	void render();
};

#endif 