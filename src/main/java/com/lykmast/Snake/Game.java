package com.lykmast.Snake;

import java.util.Collection;

class Game {
  private Snake theSnake;
  private final Board theBoard;
  private Food theFood; 
  private int score = 0;
  
  
  public enum GameState {GameOver, GameNotOver};
  
  Game(int N, int M) {
    theBoard = new Board(N, M);
    theSnake = new Snake(theBoard.getCenter(), Direction.EAST);
    theFood = new Food();
    placeFood();
  }
  public int getScore() {
    return score;
  }
  
  public Position getFoodPosition() {
    return theFood.getPosition();
  }
  
  public Collection<Position> getSnakeSquares() {
    return theSnake.getSquares();
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


  public GameState doIteration(Direction nextDirection) {
    theSnake.turn(nextDirection);
    
    boolean wouldIntersect = theSnake.wouldIntersect();
    move();
    
    if (!theBoard.inBounds(theSnake.getPosition()) || wouldIntersect){
      return GameState.GameOver;
    }
    return GameState.GameNotOver;
  }

  
}
