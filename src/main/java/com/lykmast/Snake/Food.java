package com.lykmast.Snake;

class Food {
  private Position position;

  Food(Position p) { changePosition(p);}
  
  Food() {}
  
  void changePosition(Position p) { position = p;}
  
  Position getPosition() { return position; }
}
