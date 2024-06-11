#include <math.h>

bool checkCollision(float x1, float y1, float x2, float y2, float radius) {
    float dx = x2 - x1;
    float dy = y2 - y1;
    float distance = sqrt(dx * dx + dy * dy);
    return distance < radius;
}
/*

void endGame() {
    // 게임 종료 로직 (예: 메시지 출력, 게임 상태 변경 등)
    printf("Game Over!\n");
    exit(0); // 프로그램 종료 (또는 다른 게임 종료 처리)
}
*/
