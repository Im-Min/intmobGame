#include "game.h"
#include <iostream>

int main() {
    Game game;
    game.initialize();

    char input;
    while (true) {
        game.render();
        std::cout << "Enter move (w/a/s/d): ";
        std::cin >> input;
        game.handleInput(input);
        game.update();
    }

    return 0;
}
