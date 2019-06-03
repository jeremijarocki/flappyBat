import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class UpperRock {

    private Image upperRock;
    private int xLock = 0;
    private int yLock = 0;

    public UpperRock(int initialWidth, int initialHeight) {
        upperRock = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("pics/rock_upper.png"));
        scaleUpperRock(initialWidth, initialHeight);
    }

    // Method to scale the UpperRock sprite into the desired dimensions
    public void scaleUpperRock(int width, int height) {
        upperRock = upperRock.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    // Getter method for the UpperRock object.
    public Image getUpperRock() {
        return upperRock;
    }

    // getting the width of upperRock object
    public int getWidth() {
        return upperRock.getWidth(null);
    }

    // getting the height of upperRock object
    public int getHeight() {
        return upperRock.getHeight(null);
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

    // Method used to acquire a Rectangle that outlines the UpperRock's image
    public Rectangle getRectangle() {
        return (new Rectangle(xLock, yLock, upperRock.getWidth(null), upperRock.getHeight(null)));
    }

    // Method to acquire a BufferedImage that represents the UpperRock's image object
    public BufferedImage getBI() {
        BufferedImage bi = new BufferedImage(upperRock.getWidth(null), upperRock.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(upperRock, 0, 0, null);
        g.dispose();
        return bi;
    }
}