import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Bat {

    private Image flappyBat;
    private int xLock = 0;
    private int yLock = 0;

    public Bat(int initialWidth, int initialHeight) {
        flappyBat = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("pics/sweet_bat.png"));
        scaleBat(initialWidth, initialHeight);
    }

    //Method to scale the bat sprite into given dimensions
    public void scaleBat(int width, int height) {
        flappyBat = flappyBat.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }


    // Getter method to get the flappyBat object.
    public Image getBat() {
        return flappyBat;
    }

    // width of the bat
    public int getWidth() {
        try {
            return flappyBat.getWidth(null);
        } catch (Exception e) {
            return -1;
        }
    }

    // height of the bat
    public int getHeight() {
        try {
            return flappyBat.getHeight(null);
        } catch (Exception e) {
            return -1;
        }
    }

    public void setX(int x) {
        xLock = x;
    }

    public int getX() {
        return xLock;
    }

    public void setY(int y) {
        yLock = y;
    }

    public int getY() {
        return yLock;
    }


    // Method used to acquire a Rectangle that outlines the Bat's image
    public Rectangle getRectangle() {
        return (new Rectangle(xLock, yLock, flappyBat.getWidth(null), flappyBat.getHeight(null)));
    }

    // Method to acquire a BufferedImage that represents the Bat's image object
    public BufferedImage getBI() {
        BufferedImage bi = new BufferedImage(flappyBat.getWidth(null), flappyBat.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(flappyBat, 0, 0, null);
        g.dispose();
        return bi;
    }
}