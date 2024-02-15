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
  private Direction cachedDirection;
  private Collection<Position> snakeCache;
  private Position foodCache;
  private Thread gameThread;
  
  private final Color themeColor1 = new Color(0, 51, 0);
  private final Color themeColor2 = new Color(153, 204, 0);
  private enum State {Playing, Paused}

  private State state;

  WindowConsole(int N, int M) {
    this.N = N; 
    this.M = M;
    
    game = new Game(N, M);
    
    initGameState();
    
    
  }


  public void playOnThread(){
    gameThread = new Thread(() -> play(), "Game Thread");
    gameThread.start();
  }

  private void invokeAndWait(Runnable exec){
    try {
      SwingUtilities.invokeAndWait(exec);
    } catch (InvocationTargetException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  private final Runnable initializeGuiRunnable = () -> {
      initFrame();
      createContentPane();
      createGamePanel();
      initGrid();
      theFrame.setVisible(true);
  };




  public void playOnThread(){
    gameThread = new Thread(() -> play(), "Game Thread");
    gameThread.start();
  }

  private void setDirection() {
    direction = cachedDirection;
  }
  private void play(){
    while(game.doIteration(direction) == GameState.GameNotOver) {
      invokeAndWait( () -> drawEverything());
      sleep(200);
      setDirection();
      check_pause();
    }

    SwingUtilities.invokeLater(() -> gameOver());
  }

  private void check_pause(){
    if (state == State.Paused){
      // SwingUtilities.invokeLater(() -> pauseUI());
      synchronized(gameThread){
        try {
          gameThread.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          // invokeAndWait(() -> unpauseUI());
        }
      }
    }
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
    cachedDirection = Direction.EAST;
    foodCache = null;
    state = State.Playing;
  }

  private void initFrame(){
    theFrame = new JFrame();
    theFrame.setTitle("Snake");
    theFrame.setResizable(false);
    theFrame.setSize(N*16,M*16);
    theFrame.setMinimumSize(new Dimension(N*2,M*2));
    theFrame.setLocationRelativeTo(null);
    theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    theFrame.addKeyListener(theKeyListener());
  }
  private final KeyListener theKeyListener() {
    return new KeyListener() {
      
      @Override
      public void keyReleased(KeyEvent e) {
        // keyPressed(e);
      }
      
      @Override
      public void keyPressed(KeyEvent e) {
        Direction newDirection = cachedDirection;
        switch (e.getKeyCode()) {
          case KeyEvent.VK_UP:
            newDirection = Direction.NORTH;
            break;
          case KeyEvent.VK_DOWN:
            newDirection = Direction.SOUTH;
            break;
          case KeyEvent.VK_LEFT:
            newDirection = Direction.EAST;
            break;
          case KeyEvent.VK_RIGHT:
            newDirection = Direction.WEST;
            break;
          case KeyEvent.VK_P:
          if (state == State.Paused){
            state = State.Playing;
            unpauseUI();
            synchronized(gameThread){
              gameThread.notify();
            }
          } else {
            pauseUI();
            state = State.Paused;
          }
          break;
          default:
            // Don't care about other keys.
            break;
        }
        if (newDirection != Direction.reverse(direction)){
          cachedDirection = newDirection;
        }
      }

      @Override
      public void keyTyped(KeyEvent e) {
        // do nothing
      }
    };
  }

  private void createContentPane(){
    JPanel contentPane = new JPanel();
    contentPane.setBackground(themeColor1);
    contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout());

    JPanel topPanel = new JPanel();
    topPanel.setOpaque(false);
    topPanel.setLayout(new GridLayout(1, 3, 10, 10));
    topPanel.add(new JLabel(""));

    JLabel lblTitle = new JLabel("Snake");
    lblTitle.setForeground(themeColor2);
    lblTitle.setFont(new Font("Century Gothic", Font.BOLD, 20));
    lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
    topPanel.add(lblTitle);
    
    scoreLabel = new JLabel("Score: 0");
    scoreLabel.setForeground(themeColor2);
    scoreLabel.setFont(new Font("Century Gothic", 0, 20));
    topPanel.add(scoreLabel);
    scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

    layered.setOpaque(false);
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
    gamePanel.setOpaque(false);
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
          gameOverDialog.dispose();
          theFrame.dispose();
          System.exit(0);
        }
      }
      
    });

    gameOverDialog.setLayout(new BorderLayout(20,20));
    gameOverDialog.add(gameOverText, BorderLayout.NORTH);
    gameOverText.setHorizontalAlignment(SwingConstants.CENTER);
   
    JPanel buttonPanel = new JPanel(new GridLayout(1,2,10,10));
    buttonPanel.add(playAgainButton);
    buttonPanel.add(exitButton);
    gameOverDialog.add(buttonPanel, BorderLayout.CENTER);
    gameOverDialog.pack();
    gameOverDialog.setResizable(false);
    gameOverDialog.setLocationRelativeTo(theFrame);
    gameOverDialog.setVisible(true);
  }


  private void pauseUI() {
    JPanel pausePanel = new JPanel(new BorderLayout());
    pausePanel.setOpaque(false);

    JLabel pauseLabel = new JLabel("Game paused");
    pauseLabel.setHorizontalAlignment(SwingConstants.CENTER);
    pauseLabel.setFont(new Font("Century Gothic", Font.BOLD, 30));
    pauseLabel.setOpaque(false);
    pauseLabel.setForeground(themeColor2);
    
    pausePanel.add(pauseLabel, BorderLayout.CENTER);
    pausePanel.setSize(layered.getSize());
    
    layered.add(pausePanel);
    layered.setLayer(pausePanel, 1);
  }

  private void unpauseUI() {
    layered.removeAll();
    layered.add(gamePanel);
    layered.setLayer(gamePanel, 0);
    layered.revalidate();
    layered.repaint();
  }

  private void refresh() {
    whiteCanvas();
    initGameState();
    game = new Game(N, M);
  }
}