#include "game.h"

void Pacman::move(int dx, int dy)
{
	position.x += dx;
	position.y += dy;
}

void Ghost::move(int dx, int dy)
{
	position.x += dx;
	position.y += dy;
}

void GameMap::initialize()
{
    map[1][1] = 2; // Cookie
    map[1][2] = 1; // Wall
    map[2][2] = 1; // Wall
    map[3][2] = 1; // Wall
    map[3][3] = 2;
}

bool GameMap::isWall(int x, int y) {
    return map[y][x] == 1;
}

void GameMap::placeCookie(int x, int y) {
    map[y][x] = 2;
}

void Game::initialize() {
    map.initialize();
    pacman = Pacman(1, 1);
    ghosts.push_back(Ghost(3, 3));
    ghosts.push_back(Ghost(5, 5));
    cookies.push_back(Cookie(1, 1));
    cookies.push_back(Cookie(3, 3));
    score = 0;
}

void Game::update() {
    // Update ghosts and check for collisions
    for (auto& ghost : ghosts) {
        ghost.move(1, 0); // Simple movement, should be replaced with AI
    }
    checkCollision();
}

void Game::handleInput(char input) {
    int dx = 0, dy = 0;
    switch (input) {
    case 'w': dy = -1; break;
    case 's': dy = 1; break;
    case 'a': dx = -1; break;
    case 'd': dx = 1; break;
    }
    if (!map.isWall(pacman.position.x + dx, pacman.position.y + dy)) {
        pacman.move(dx, dy);
        // Check if Pacman eats a cookie
        if (map.map[pacman.position.y][pacman.position.x] == 2) {
            score += 10;
            map.map[pacman.position.y][pacman.position.x] = 0;
        }
    }
}

void Game::checkCollision() {
    for (auto& ghost : ghosts) {
        if (ghost.position.x == pacman.position.x && ghost.position.y == pacman.position.y) {
            // Collision detected
            // Handle game over logic
            std::cout << "Game Over! Pacman was caught by a ghost.\n";
            exit(0);
        }
    }
}

void Game::render() {
    system("cls"); // Use "cls" for Windows
    for (int y = 0; y < map.height; ++y) {
        for (int x = 0; x < map.width; ++x) {
            if (pacman.position.x == x && pacman.position.y == y) {
                std::cout << "P ";
            }
            else {
                bool isGhost = false;
                for (auto& ghost : ghosts) {
                    if (ghost.position.x == x && ghost.position.y == y) {
                        std::cout << "G ";
                        isGhost = true;
                        break;
                    }
                }
                if (!isGhost) {
                    if (map.map[y][x] == 1) {
                        std::cout << "# ";
                    }
                    else if (map.map[y][x] == 2) {
                        std::cout << ". ";
                    }
                    else {
                        std::cout << "  ";
                    }
                }
            }
        }
        std::cout << "\n";
    }
    std::cout << "Score: " << score << "\n";
}