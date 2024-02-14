package com.lykmast.Snake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.lykmast.Snake.Game.GameState;

public class WindowConsole {
  private Game game;
  private JLayeredPane layered = new JLayeredPane();
  private JPanel gamePanel;
  private JFrame theFrame;
  private JPanel[][] grid;
  private JLabel scoreLabel;
  private final int N , M ;
  private Direction direction;
  private Collection<Position> snakeCache;
  private Position foodCache;
  private final ExecutorService gameExecutorService;

  WindowConsole(int N, int M) {
    gameExecutorService = Executors.newSingleThreadExecutor();
    this.N = N; 
    this.M = M;
    
    game = new Game(N, M);
    
    initGameState();
    
    
    try {
      SwingUtilities.invokeAndWait(initializeGuiRunnable);
    } catch (InvocationTargetException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  void invokeAndWait(Runnable exec){
    try {
      SwingUtilities.invokeAndWait(exec);
    } catch (InvocationTargetException | InterruptedException e) {
      e.printStackTrace();
    }
  }



  private final Runnable initializeGuiRunnable = new Runnable() {

    @Override
    public void run() {
      initFrame();
      createContentPane();
      createGamePanel();
      initGrid();
      theFrame.setVisible(true);
    }

  };




  public void playOnThread(){
    gameExecutorService.execute(new Runnable() {
      @Override public void run() { play(); }
    });
  }


  private void play(){ 
    while(game.doIteration(direction) == GameState.GameNotOver) {
      invokeAndWait( new Runnable() {
        @Override public void run() {drawEverything();}
      });
      sleep(333);
    }

    invokeAndWait(new Runnable() {
      @Override public void run() {gameOver();}
    });
  }

  private void sleep(long ms){
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void drawEverything() {
    drawFood();
    drawScore();
    drawSnake();
  }

  private void initGameState() {
    snakeCache = new ArrayList<>();
    direction = Direction.EAST;
    foodCache = null;
  }

  private void initFrame(){
    theFrame = new JFrame();
    theFrame.setTitle("Snake");
    theFrame.setResizable(true);
    theFrame.setSize(N*16,M*16);
    theFrame.setMinimumSize(new Dimension(N*2,M*2));
    theFrame.setLocationRelativeTo(null);
    theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    theFrame.addKeyListener(theKeyListener());
  }
  private final KeyListener theKeyListener() {
    return new KeyListener() {
      
      @Override
      public void keyPressed(KeyEvent arg0) {
        keyReleased(arg0);
      }

      @Override
      public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
          case KeyEvent.VK_UP:
            direction = Direction.NORTH;
            break;
          case KeyEvent.VK_DOWN:
            direction = Direction.SOUTH;
            break;
          case KeyEvent.VK_LEFT:
            direction = Direction.EAST;
            break;
          case KeyEvent.VK_RIGHT:
            direction = Direction.WEST;
          default:
            // Don't care about other keys.
            break;
        }

      }

      @Override
      public void keyTyped(KeyEvent arg0) {
        // do nothing
      }
    };
  }

  private void createContentPane(){
    JPanel contentPane = new JPanel();
    contentPane.setBackground(new Color(0, 51, 0));
    contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout());

    JPanel topPanel = new JPanel();
    topPanel.setOpaque(false);
    topPanel.setLayout(new GridLayout(1, 3, 10, 10));
    topPanel.add(new JLabel(""));

    JLabel lblTitle = new JLabel("Snake");
    lblTitle.setForeground(new Color(153, 204, 0));
    lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 20));
    lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
    topPanel.add(lblTitle);
    
    scoreLabel = new JLabel("Score: 0");
    scoreLabel.setForeground(new Color(153, 204, 0));
    scoreLabel.setFont(new Font("Century Gothic", 0, 20));
    topPanel.add(scoreLabel);
    scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

    layered.setOpaque(true);
    layered.setLayout(new BorderLayout());
    contentPane.add(layered, BorderLayout.CENTER);
    contentPane.add(topPanel, BorderLayout.NORTH);
    theFrame.setContentPane(contentPane);
  }


  private void initGrid() {
    grid = new JPanel[N][M];
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < M; j++) {
        grid[i][j] = new JPanel();
        grid[i][j].setBackground(Color.WHITE);
        grid[i][j].setOpaque(true);
        gamePanel.add(grid[i][j]);
      }
    }
  } 
  
  void createGamePanel() {
    gamePanel = new JPanel(new GridLayout(N,M));
    gamePanel.setOpaque(true);
    layered.add(gamePanel);
    layered.setLayer(gamePanel, 0);
  }


  private void whiteCanvas() {
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < M; j++) {
        drawSquare(i, j, Color.WHITE);
      }
    }
  }

  private void drawScore() {
    int score = game.getScore();
    scoreLabel.setText("Score: " + score);
  }

  private void drawSquare(int i, int j, Color c) {
    grid[i][j].setBackground(c);
  } 

  private void drawFood() {
    Position foodPosition = game.getFoodPosition();
    if (foodPosition != foodCache) {
      drawSquare(foodPosition.x, foodPosition.y, Color.BLACK);
      foodCache = foodPosition;
    }
  }

  private void drawSnake() {
    Collection<Position> newSnake = game.getSnakeSquares();
    Collection<Position> turnWhite = new ArrayList<Position>(snakeCache);
    Collection<Position> turnBlack = new ArrayList<Position>(newSnake);
    turnBlack.removeAll(snakeCache);
    turnWhite.removeAll(newSnake);
    snakeCache = new ArrayList<Position>(newSnake);
    
    for (Position position : turnBlack) {
      drawSquare(position.x, position.y, Color.BLACK);
    }

    for (Position position: turnWhite) {
      drawSquare(position.x, position.y, Color.WHITE);
    }


  }

  private void gameOver() {

    JDialog gameOverDialog = new JDialog(theFrame);
    JLabel gameOverText = new JLabel("Game is over!");
    gameOverText.setFont(new Font("Century Gothic", Font.BOLD, 15));

    JButton exitButton = new JButton("Exit.");
    
    JButton playAgainButton = new JButton("Play Again?");
    playAgainButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Play Again?")){
          gameOverDialog.dispose();
          refresh();
          playOnThread();
        }
      }
    });
    exitButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Exit.")){
          theFrame.dispose();
        }
      }
      
    });

    gameOverDialog.setLayout(new BorderLayout(20,20));
    gameOverDialog.add(gameOverText, BorderLayout.NORTH);
    gameOverText.setHorizontalAlignment(SwingConstants.CENTER);
   
    JPanel buttonPanel = new JPanel(new GridLayout(1,2,10,10));
    buttonPanel.add(playAgainButton);
    // JPanel buttonPanel = new JPanel(new BorderLayout(10,10));
    buttonPanel.add(exitButton);
    gameOverDialog.add(buttonPanel, BorderLayout.CENTER);
    gameOverDialog.pack();
    gameOverDialog.setResizable(false);
    gameOverDialog.setLocationRelativeTo(theFrame);
    gameOverDialog.setVisible(true);
  }

  private void refresh() {
    invokeAndWait( new Runnable() {
      @Override public void run() {whiteCanvas();}
    });

    initGameState();
    game = new Game(N, M);
  }
}