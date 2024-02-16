package com.lykmast.Snake;

import java.util.List;

class Game {
  private Snake theSnake;
  private final Board theBoard;
  private Food theFood; 
  private int score = 0;
  
  public final static Direction START_DIRECTION= Direction.WEST;
  public enum GameState {GameOver, GameNotOver};
  
  Game(int N, int M) {
    theBoard = new Board(N, M);
    theSnake = new Snake(theBoard.getCenter(), START_DIRECTION);
    theFood = new Food();
    placeFood();
  }
  public int getScore() {
    return score;
  }
  
  public Position getFoodPosition() {
    return theFood.getPosition();
  }
  
  public List<Position> getSnakeSquares() {
    return theSnake.getSquares();
  }

  public GameState doIteration(Direction nextDirection) {
    theSnake.turn(nextDirection);
    
    boolean wouldIntersect = theSnake.wouldIntersect();
    move();
    
    if (!theBoard.inBounds(theSnake.getPosition()) || wouldIntersect){
      return GameState.GameOver;
    }
    return GameState.GameNotOver;
  }

  private void placeFood() {
    theFood.changePosition(
      theBoard.getRandomPositionExcluding(theSnake.getSquares())
    );
  }

  private void move(){
    theSnake.move();
    if (theSnake.getPosition().equals(theFood.getPosition())){
      theSnake.eat();
      placeFood();
      ++ score;
    }
  }
  
}
