import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class BottomRock {

    private Image bottomRock;
    private int xLock = 0;
    private int yLock = 0;

    public BottomRock(int initialWidth, int initialHeight) {
        bottomRock = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("pics/rock_bottom.png"));
        scaleBottomRock(initialWidth, initialHeight);
    }

    //Method to scale the BottomRock sprite into the desired dimensions
    public void scaleBottomRock(int width, int height) {
        bottomRock = bottomRock.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    // Getter method for the BottomRock object.
    public Image getBottomRock() {
        return bottomRock;
    }

    // method to get the width of bottomRock
    public int getWidth() {
        return bottomRock.getWidth(null);
    }

    // method to get the height of bottomRock
    public int getHeight() {
        return bottomRock.getHeight(null);
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

    // Method used to acquire a Rectangle that outlines the BottomRock's image
    public Rectangle getRectangle() {
        return (new Rectangle(xLock, yLock, bottomRock.getWidth(null), bottomRock.getHeight(null)));
    }

    // Method to acquire a BufferedImage that represents the BottomRock's image object
    public BufferedImage getBI() {
        BufferedImage bi = new BufferedImage(bottomRock.getWidth(null), bottomRock.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(bottomRock, 0, 0, null);
        g.dispose();
        return bi;
    }
}