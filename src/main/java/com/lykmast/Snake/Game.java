package com.lykmast.Snake;


class Game {
  private Snake theSnake;
  private final Board theBoard;
  private final int N = 40, M = 40;
  private Food theFood; 
  private int score = 0;
  private Controls theControls;
  private Console theConsole;

  private enum GameState {GameOver, GameNotOver};
  
  Game() {
    theBoard = new Board(N, M);
    theSnake = new Snake(theBoard.getCenter(), Direction.EAST);
    theFood = new Food();
    placeFood();
    WindowConsole windowConsole = new WindowConsole(N, M);
    theConsole = windowConsole;
    theControls = windowConsole;
  }
  
  private void placeFood() {
    theFood.changePosition(
      theBoard.getRandomPositionExcluding(theSnake.getSquares())
    );
  }
  void initSnake() {
    theSnake = new Snake(theBoard.getCenter(), Direction.EAST);
  }

  // void refreshConsole(){
  //   theConsole.refresh();
  // }

  void refreshGame() {
    score = 0;
    initSnake();
    placeFood();
    // refreshConsole();
  }

  private void move(){
    theSnake.move();
    if (theSnake.getPosition().equals(theFood.getPosition())){
      theSnake.eat();
      placeFood();
      ++ score;
    }
  }

  private void drawEverything() {
    // theConsole.whiteCanvas();
    theConsole.drawFood(theFood.getPosition());
    theConsole.drawScore(score);
    theConsole.drawSnake(theSnake.getSquares());
  }

  private GameState doIteration() {
    Direction nextDirection = theControls.getDirection();
    theSnake.turn(nextDirection);
    
    boolean wouldIntersect = theSnake.wouldIntersect();
    move();
    
    if (!theBoard.inBounds(theSnake.getPosition()) || wouldIntersect){
      theConsole.gameOver(this);
      return GameState.GameOver;
    }
    
    drawEverything();
    return GameState.GameNotOver;
  }

  void play(){
    while(doIteration() != GameState.GameOver){
      try {
        Thread.sleep(333);
        Position p = theSnake.getPosition();
        System.err.println("<"+ p.x +", " + p.y +">");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
