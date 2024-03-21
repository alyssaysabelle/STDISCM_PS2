import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Sprite {
    private int x;
    private int y;
    private BufferedImage image; // Image object to hold the sprite image

    public Sprite(int x, int y) {
        this.x = x;
        this.y = y;
        loadImage();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(new File("src/pingu.png")); // Replace "path/to/your/image.png" with your actual image path
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
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

        double scaleX = (double) canvasWidth / image.getWidth();
        double scaleY = (double) canvasHeight / image.getHeight();

        double scale = Math.min(scaleX, scaleY);

        int scaledWidth = (int) (image.getWidth() * scale);
        int scaledHeight = (int) (image.getHeight() * scale);
        int centeredX = (canvasWidth / 2) - (scaledWidth / 2);
        int centeredY = (canvasHeight / 2) - (scaledHeight / 2);

        g.drawImage(image, centeredX, centeredY, scaledWidth, scaledHeight, null);
    }
}

