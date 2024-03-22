import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Sprite {
    private int x;
    private int y;
    private BufferedImage[] images; // Array to hold multiple sprite images
    private int currentImageIndex = 0; // Index of the current image

    public Sprite(int x, int y) {
        this.x = x;
        this.y = y;
        loadImages(); // Load multiple images
    }

    private void loadImages() {
        try {
            images = new BufferedImage[3];
            images[0] = ImageIO.read(getClass().getResourceAsStream("/pingu.png"));
            images[1] = ImageIO.read(getClass().getResourceAsStream("/pingu2.png"));
            images[2] = ImageIO.read(getClass().getResourceAsStream("/pingu.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
        if (dx != 0 || dy != 0) { // If moving
            if (currentImageIndex == 0) {
                currentImageIndex = 1; // Change to pingu2 image
            } else {
                currentImageIndex = 0; // Change to pingu image
            }
        } else {
            currentImageIndex = 2; // Change to pingu image when standing still
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void draw(Graphics g, JPanel drawPanel) {
        int canvasWidth = drawPanel.getWidth();
        int canvasHeight = drawPanel.getHeight();

        double scaleX = (double) canvasWidth / images[currentImageIndex].getWidth();
        double scaleY = (double) canvasHeight / images[currentImageIndex].getHeight();

        double scale = Math.min(scaleX, scaleY);

        int scaledWidth = (int) (images[currentImageIndex].getWidth() * scale);
        int scaledHeight = (int) (images[currentImageIndex].getHeight() * scale);
        int centeredX = (canvasWidth / 2) - (scaledWidth / 2);
        int centeredY = (canvasHeight / 2) - (scaledHeight / 2);

        g.drawImage(images[currentImageIndex], centeredX, centeredY, scaledWidth, scaledHeight, null);
    }
}
