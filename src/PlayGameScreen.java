import javax.swing.*;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;

public class PlayGameScreen extends JPanel {
    //default reference ID
    private static final long serialVersionUID = 1L;

    //global variables
    private int screenWidth;
    private static int screenHeight;
    private boolean isSplash = true;
    private int successfulJumps = 0;
    private String message = "Flappy Bat";
    private Font primaryFont = new Font("Goudy Stout", Font.BOLD, 42);
    private Font failFont = new Font("Calibri", Font.BOLD, 42);
    private int messageWidth = 0;
    private int scoreWidth = 0;
    private BottomRock br1, br2;
    private UpperRock ur1, ur2;
    private Bat bat;

    // Default constructor
    public PlayGameScreen(int screenWidth, int screenHeight, boolean isSplash) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.isSplash = isSplash;
    }

    // Manually control what's drawn on this JPanel by calling the paintComponent method
    // with a graphics object and painting using that object

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(63, 70, 81)); //color for the cave
        g.fillRect(0, 0, screenWidth, screenHeight * 7 / 8); //create the sky rectangle
        g.setColor(new Color(13, 52, 114)); //blue color for ground
        g.fillRect(0, screenHeight * 7 / 8, screenWidth, screenHeight / 8); //create the ground rectangle
        g.setColor(Color.BLACK); //dividing line color, bat dies when touching it
        g.drawLine(0, screenHeight * 7 / 8, screenWidth, screenHeight * 7 / 8); //draw the dividing line

        //objects must be instantiated
        if (br1 != null && br2 != null && ur1 != null && ur2 != null) {
            g.drawImage(br1.getBottomRock(), br1.getX(), br1.getY(), null);
            g.drawImage(br2.getBottomRock(), br2.getX(), br2.getY(), null);
            g.drawImage(ur1.getUpperRock(), ur1.getX(), ur1.getY(), null);
            g.drawImage(ur2.getUpperRock(), ur2.getX(), ur2.getY(), null);
        }

        if (!isSplash && bat != null) {
            g.drawImage(bat.getBat(), bat.getX(), bat.getY(), null);
        }

        //backup font
        try {
            g.setFont(primaryFont);
            FontMetrics metric = g.getFontMetrics(primaryFont);
            messageWidth = metric.stringWidth(message);
            scoreWidth = metric.stringWidth(String.format("%d", successfulJumps));
        } catch (Exception e) {
            g.setFont(failFont);
            FontMetrics metric = g.getFontMetrics(failFont);
            messageWidth = metric.stringWidth(message);
            scoreWidth = metric.stringWidth(String.format("%d", successfulJumps));
        }

        g.drawString(message, screenWidth / 2 - messageWidth / 2, screenHeight / 4);

        if (!isSplash) {
            g.drawString(String.format("%d", successfulJumps), screenWidth / 2 - scoreWidth / 2, 50);
        }
    }

    // creating both bottomRocks
    public void setBottomRock(BottomRock br1, BottomRock br2) {
        this.br1 = br1;
        this.br2 = br2;
    }

    // creating both upperRocks
    public void setUpperRock(UpperRock ur1, UpperRock ur2) {
        this.ur1 = ur1;
        this.ur2 = ur2;
    }

    // creating the bat
    public void setBat(Bat bat) {
        this.bat = bat;
    }

    // counting number of successful jumps
    public void incrementJump() {
        successfulJumps++;
    }

    public int getScore() {
        return successfulJumps;
    }

    // show the message on the screen
    public void sendText(String message) {
        this.message = message;
    }
}