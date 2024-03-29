package com.lykmast.Snake;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Snake {
  private int size = 3;
  private Position position;
  private Direction direction;
  private Deque<Position> squares;
  
  Snake(Position startPosition, Direction startDirection) {
    position = startPosition;
    direction = startDirection;
    squares = calculateStartingSquares();
  }

  Position getPosition() { return position; }

  void move() {
    position = new Position(position.oneOver(direction));
    squares.addFirst(position);
    squares.removeLast();
  }

  void turn(Direction newDir) {
    direction = newDir;
  }

  void eat() {
    size += 1;
    addEatSquare();
  }


  boolean wouldIntersect(){
    // Should be checked before moving.
    
    // Intersect does not count if it's the tip of the tail
    // (the last square) because it will not be there on
    // next move. So we remove it from squares before checking
    // and then put it back in its place.
    Position last = squares.removeLast();
    boolean wouldIntersect = squares.contains(position.oneOver(direction));
    squares.addLast(last);
    return wouldIntersect;
  }

  List<Position> getSquares(){
    return new ArrayList<>(squares);
  }
  
  private void addEatSquare() {
    // This is a hack to add a square without causing an intersection.
    // the tip of the tail is added once more at the tail
    // so that it is removed on next `move()`. 
    squares.addLast(squares.getLast());
  }

  private Deque<Position> calculateStartingSquares() {
    Position pos = new Position(position);
    Direction oppositeDir = Direction.reverse(direction);
    Deque<Position> startingSquares = new ArrayDeque<>();
    
    for (int i = 0; i < size; i++) {
      startingSquares.addLast(new Position(pos));
      pos.moveOneOver(oppositeDir);
    }
    return startingSquares;
  }


}
