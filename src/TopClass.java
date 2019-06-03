import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class TopClass implements ActionListener, KeyListener {
    //global constant variables
    private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static final int ROCK_GAP = SCREEN_HEIGHT / 5; //distance in pixels between pipes
    private static final int ROCK_WIDTH = SCREEN_WIDTH / 8;
    private static final int ROCK_HEIGHT = 4 * ROCK_WIDTH;
    private static final int BAT_WIDTH = 120;
    private static final int BAT_HEIGHT = 75;
    private static final int UPDATE_DIFFERENCE = 25; //time in ms between updates
    private static final int X_MOVEMENT_DIFFERENCE = 7; //distance the pipes move every update
    private static final int SCREEN_DELAY = 300; //needed because of long load times forcing pipes to pop up mid-screen
    private static final int BAT_X_LOCATION = SCREEN_WIDTH / 7;
    private static final int BAT_JUMP_DIFF = 6;
    private static final int BAT_FALL_DIFF = BAT_JUMP_DIFF / 3;
    private static final int BAT_JUMP_HEIGHT = ROCK_GAP - BAT_HEIGHT - BAT_JUMP_DIFF * 3;

    //global variables
    private boolean loopVar = true; //false -> don't run loop; true -> run loop for pipes
    private boolean gamePlay = false; //false -> game not being played
    private boolean batThrust = false; //false -> key has not been pressed to move the bird vertically
    private boolean batFired = false; //true -> button pressed before jump completes
    private boolean released = true; //space bar released; starts as true so first press registers
    private int batYTracker = SCREEN_HEIGHT / 2 - BAT_HEIGHT;
    private Object buildComplete = new Object();

    //global swing objects
    private JFrame f = new JFrame("Flappy Bat");
    private JButton startGame;
    private JPanel topPanel; //declared globally to accommodate the repaint operation and allow for removeAll(), etc.

    //other global objects
    private static TopClass tc = new TopClass();
    private static PlayGameScreen pgs; //panel that has the moving background at the start of the game

    //default constructor
    public TopClass() {
    }

    public static void main(String[] args) {
        //build the GUI on a new thread
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tc.buildFrame();

                //create a new thread to keep the GUI responsive while the game runs
                Thread t = new Thread() {
                    public void run() {
                        tc.gameScreen(true);
                    }
                };
                t.start();
            }
        });
    }

    //Method to construct the JFrame and add the program content
    private void buildFrame() {
        Image icon = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("pics/sweet_bat.png"));

        f.setContentPane(createContentPane());
        f.setResizable(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setAlwaysOnTop(false);
        f.setVisible(true);
        f.setMinimumSize(new Dimension(SCREEN_WIDTH * 1 / 4, SCREEN_HEIGHT * 1 / 4));
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setIconImage(icon);
        f.addKeyListener(this);
    }

    private JPanel createContentPane() {
        topPanel = new JPanel(); //top-most JPanel in layout hierarchy
        topPanel.setBackground(Color.BLACK);
        //allow us to layer the panels
        LayoutManager overlay = new OverlayLayout(topPanel);
        topPanel.setLayout(overlay);

        //Start Game JButton
        startGame = new JButton("Start Playing! Press SPACE to fly. Press B to start a new game");
        startGame.setBackground(Color.RED);
        startGame.setForeground(Color.WHITE);
        startGame.setFocusable(false); //rather than just setFocusabled(false)
        startGame.setFont(new Font("Calibri", Font.BOLD, 42));
        startGame.setAlignmentX(0.5f); //center horizontally on-screen
        startGame.setAlignmentY(0.5f); //center vertically on-screen
        startGame.addActionListener(this);
        topPanel.add(startGame);

        //must add last to ensure button's visibility
        pgs = new PlayGameScreen(SCREEN_WIDTH, SCREEN_HEIGHT, true); //true --> we want pgs to be the splash screen
        topPanel.add(pgs);

        return topPanel;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startGame) {
            //stop the splash screen
            loopVar = false;
            fadeOperation();

        } else if (e.getSource() == buildComplete) {
            Thread t = new Thread() {
                public void run() {
                    loopVar = true;
                    gamePlay = true;
                    tc.gameScreen(false);
                }
            };
            t.start();
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && gamePlay == true && released == true) {
            //update a boolean that's tested in game loop to move the bird
            if (batThrust) { //need this to register the button press and reset the birdYTracker before the jump operation completes
                batFired = true;
            }
            batThrust = true;
            released = false;
        } else if (e.getKeyCode() == KeyEvent.VK_B && gamePlay == false) {
            batYTracker = SCREEN_HEIGHT / 2 - BAT_HEIGHT; //need to reset the bird's starting height
            batThrust = false; //if user presses SPACE before collision and a collision occurs before reaching max height, you get residual jump, so this is preventative
            actionPerformed(new ActionEvent(startGame, -1, ""));
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            released = true;
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    private void fadeOperation() {
        Thread t = new Thread() {
            public void run() {
                topPanel.remove(startGame);
                topPanel.remove(pgs);
                topPanel.revalidate();
                topPanel.repaint();

                //panel to fade
                JPanel temp = new JPanel();
                int alpha = 0; //alpha channel variable
                temp.setBackground(new Color(0, 0, 0, alpha)); //transparent, black JPanel
                topPanel.add(temp);
                topPanel.add(pgs);
                topPanel.revalidate();
                topPanel.repaint();

                long currentTime = System.currentTimeMillis();

                while (temp.getBackground().getAlpha() != 255) {
                    if ((System.currentTimeMillis() - currentTime) > UPDATE_DIFFERENCE / 2) {
                        if (alpha < 255 - 10) {
                            alpha += 10;
                        } else {
                            alpha = 255;
                        }

                        temp.setBackground(new Color(0, 0, 0, alpha));

                        topPanel.revalidate();
                        topPanel.repaint();
                        currentTime = System.currentTimeMillis();
                    }
                }

                topPanel.removeAll();
                topPanel.add(temp);
                pgs = new PlayGameScreen(SCREEN_WIDTH, SCREEN_HEIGHT, false);
                pgs.sendText(""); //remove title text
                topPanel.add(pgs);

                while (temp.getBackground().getAlpha() != 0) {
                    if ((System.currentTimeMillis() - currentTime) > UPDATE_DIFFERENCE / 2) {
                        if (alpha > 10) {
                            alpha -= 10;
                        } else {
                            alpha = 0;
                        }

                        temp.setBackground(new Color(0, 0, 0, alpha));

                        topPanel.revalidate();
                        topPanel.repaint();
                        currentTime = System.currentTimeMillis();
                    }
                }

                actionPerformed(new ActionEvent(buildComplete, -1, "Build Finished"));
            }
        };

        t.start();
    }

    // movements on the splash screen
    private void gameScreen(boolean isSplash) {
        BottomRock br1 = new BottomRock(ROCK_WIDTH, ROCK_HEIGHT);
        BottomRock br2 = new BottomRock(ROCK_WIDTH, ROCK_HEIGHT);
        UpperRock ur1 = new UpperRock(ROCK_WIDTH, ROCK_HEIGHT);
        UpperRock ur2 = new UpperRock(ROCK_WIDTH, ROCK_HEIGHT);
        Bat bat = new Bat(BAT_WIDTH, BAT_HEIGHT);

        //variables to track x and y image locations for the bottom rock
        int xLoc1 = SCREEN_WIDTH + SCREEN_DELAY, xLoc2 = (int) ((double) 3.0 / 2.0 * SCREEN_WIDTH + ROCK_WIDTH / 2.0) + SCREEN_DELAY;
        int yLoc1 = bottomRockLoc(), yLoc2 = bottomRockLoc();
        int batX = BAT_X_LOCATION, batY = batYTracker;

        //variable to hold the loop start time
        long startTime = System.currentTimeMillis();

        while (loopVar) {
            if ((System.currentTimeMillis() - startTime) > UPDATE_DIFFERENCE) {
                //check if a set of rocks has left the screen
                //if so, reset the rocks's X location and assign a new Y location
                if (xLoc1 < (0 - ROCK_WIDTH)) {
                    xLoc1 = SCREEN_WIDTH;
                    yLoc1 = bottomRockLoc();
                } else if (xLoc2 < (0 - ROCK_WIDTH)) {
                    xLoc2 = SCREEN_WIDTH;
                    yLoc2 = bottomRockLoc();
                }

                //decrement the rock locations by the predetermined amount
                xLoc1 -= X_MOVEMENT_DIFFERENCE;
                xLoc2 -= X_MOVEMENT_DIFFERENCE;

                if (batFired && !isSplash) {
                    batYTracker = batY;
                    batFired = false;
                }

                if (batThrust && !isSplash) {
                    //move bat vertically
                    if (batYTracker - batY - BAT_JUMP_DIFF < BAT_JUMP_HEIGHT) {
                        if (batY - BAT_JUMP_DIFF > 0) {
                            batY -= BAT_JUMP_DIFF; //coordinates different
                        } else {
                            batY = 0;
                            batYTracker = batY;
                            batThrust = false;
                        }
                    } else {
                        batYTracker = batY;
                        batThrust = false;
                    }
                } else if (!isSplash) {
                    batY += BAT_FALL_DIFF;
                    batYTracker = batY;
                }

                //update the BottomRock and UpperRock locations
                br1.setX(xLoc1);
                br1.setY(yLoc1);
                br2.setX(xLoc2);
                br2.setY(yLoc2);
                ur1.setX(xLoc1);
                ur1.setY(yLoc1 - ROCK_GAP - ROCK_HEIGHT); //ensure ur1 placed in proper location
                ur2.setX(xLoc2);
                ur2.setY(yLoc2 - ROCK_GAP - ROCK_HEIGHT); //ensure ur2 placed in proper location

                if (!isSplash) {
                    bat.setX(batX);
                    bat.setY(batY);
                    pgs.setBat(bat);
                }

                //set the BottomRock and UpperRock local variables in PlayGameScreen by parsing the local variables
                pgs.setBottomRock(br1, br2);
                pgs.setUpperRock(ur1, ur2);

                if (!isSplash && bat.getWidth() != -1) { //need the second part because if bat not on-screen, cannot get image width and have cascading error in collision
                    collisionDetection(br1, br2, ur1, ur2, bat);
                    updateScore(br1, br2, bat);
                }

                //update pgs's JPanel
                topPanel.revalidate();
                topPanel.repaint();

                //update the time-tracking variable after all operations completed
                startTime = System.currentTimeMillis();
            }
        }
    }

    // Calculates a random int for the bottom rock's placement
    private int bottomRockLoc() {
        int temp = 0;
        //iterate until temp is a value that allows both rocks to be onscreen
        while (temp <= ROCK_GAP + 50 || temp >= SCREEN_HEIGHT - ROCK_GAP) {
            temp = (int) ((double) Math.random() * ((double) SCREEN_HEIGHT));
        }
        return temp;
    }

    // Method that checks whether the score needs to be updated

    private void updateScore(BottomRock br1, BottomRock br2, Bat bat) {
        if (br1.getX() + ROCK_WIDTH < bat.getX() && br1.getX() + ROCK_WIDTH > bat.getX() - X_MOVEMENT_DIFFERENCE) {
            pgs.incrementJump();
        } else if (br2.getX() + ROCK_WIDTH < bat.getX() && br2.getX() + ROCK_WIDTH > bat.getX() - X_MOVEMENT_DIFFERENCE) {
            pgs.incrementJump();
        }
    }

    // Method to test whether a collision has occurred
    private void collisionDetection(BottomRock br1, BottomRock br2, UpperRock ur1, UpperRock ur2, Bat bat) {
        collisionHelper(bat.getRectangle(), br1.getRectangle(), bat.getBI(), br1.getBI());
        collisionHelper(bat.getRectangle(), br2.getRectangle(), bat.getBI(), br2.getBI());
        collisionHelper(bat.getRectangle(), ur1.getRectangle(), bat.getBI(), ur1.getBI());
        collisionHelper(bat.getRectangle(), ur2.getRectangle(), bat.getBI(), ur2.getBI());

        if (bat.getY() + BAT_HEIGHT > SCREEN_HEIGHT * 7 / 8) { //ground detection
            pgs.sendText("Game Over. Press B to start again");
            loopVar = false;
            gamePlay = false; //game has ended
        }
    }

    // Helper method to test the Bat object's potential collision with a rock object.
    private void collisionHelper(Rectangle r1, Rectangle r2, BufferedImage b1, BufferedImage b2) {
        if (r1.intersects(r2)) {
            Rectangle r = r1.intersection(r2);

            int firstI = (int) (r.getMinX() - r1.getMinX()); //firstI is the first x-pixel to iterate from
            int firstJ = (int) (r.getMinY() - r1.getMinY()); //firstJ is the first y-pixel to iterate from
            int bp1XHelper = (int) (r1.getMinX() - r2.getMinX()); //helper variables to use when referring to collision object
            int bp1YHelper = (int) (r1.getMinY() - r2.getMinY());

            for (int i = firstI; i < r.getWidth() + firstI; i++) { //
                for (int j = firstJ; j < r.getHeight() + firstJ; j++) {
                    if ((b1.getRGB(i, j) & 0xFF000000) != 0x00 && (b2.getRGB(i + bp1XHelper, j + bp1YHelper) & 0xFF000000) != 0x00) {
                        pgs.sendText("Game Over. Press B to start again");
                        loopVar = false; //stop the game loop
                        gamePlay = false; //game has ended
                        break;
                    }
                }
            }
        }
    }
}