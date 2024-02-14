package com.lykmast.Snake;

import java.util.Collection;

interface Console {
  void drawSnake(Collection<Position> squares);
  void whiteCanvas();
  void drawScore(int score);
  void drawFood(Position foodPosition);
  void gameOver(Game game);
  void refresh();
}
