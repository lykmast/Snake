package com.lykmast.Snake;

import java.util.Objects;

class Position {
  int x = 0, y = 0;
  
  Position(int _x, int _y) {
    x = _x; y = _y;
  }

  Position(Position p) {
    x = p.x; y = p.y;
  }

  void moveOneOver(Direction dir){
    switch (dir) {
      case NORTH:
        this.x -= 1;
        break;
      case SOUTH:
        this.x += 1;
        break;
      case EAST:
        this.y -= 1;
        break;
      case WEST:
        this.y += 1;
        break;
      default:
        throw new IllegalArgumentException("Unknown fifth direction: " + dir);
    }
  }

  @Override
  public boolean equals(Object obj) {
    return (
        obj instanceof Position
        && this.x == ((Position) obj).x 
        && this.y == ((Position) obj).y
    );
  }

  @Override
  public int hashCode(){
    return Objects.hash(this.x, this.y);
  }
  
  Position oneOver(Direction dir){
    Position newPos = new Position(x, y);
    newPos.moveOneOver(dir);
    return newPos;
  }
}
