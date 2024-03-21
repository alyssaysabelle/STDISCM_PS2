/*import java.awt.*;

import javax.swing.JPanel;

public class Sprite {
        private int x;
        private int y;
        public Sprite(int x, int y) {
            this.x = x;
            this.y = y;
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
            // Canvas dimensions
            int canvasWidth = drawPanel.getWidth(); // 1280
            int canvasHeight = drawPanel.getHeight(); // 720
        
            // Periphery dimensions
            final int peripheryWidth = 33;
            final int peripheryHeight = 19;
        
            // Calculate scale factors to zoom in the periphery to fill the canvas
            double scaleX = (double) canvasWidth / peripheryWidth;
            double scaleY = (double) canvasHeight / peripheryHeight;
        
            // Use the smaller scale to ensure the sprite maintains its aspect ratio
            double scale = Math.min(scaleX, scaleY);
        
            // Calculate sprite's centered position on the canvas
            // Since the sprite is to be centered, it should be positioned at half the width and height of the canvas
            // Adjust the sprite size according to the scale and ensure it's centered by adjusting half its scaled size
            int spriteWidth = 5; // Original sprite width
            int spriteHeight = 5; // Adjust this if the sprite's original aspect ratio is not 1:1
            int scaledWidth = (int) (spriteWidth * scale);
            int scaledHeight = (int) (spriteHeight * scale);
        
            int centeredX = (canvasWidth / 2) - (scaledWidth / 2);
            int centeredY = (canvasHeight / 2) - (scaledHeight / 2);
        
            // Set color and draw the sprite centered on the canvas
            g.setColor(Color.RED);
            g.fillRect(centeredX, centeredY, scaledWidth, scaledHeight);
        }


}*/

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
        loadImage(); // Load the sprite image when creating a new Sprite object
    }

    private void loadImage() {
        try {
            // Load the image file from the specified path
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
        // Canvas dimensions
        int canvasWidth = drawPanel.getWidth();
        int canvasHeight = drawPanel.getHeight();

        // Calculate scale factors to fit the sprite within the canvas
        double scaleX = (double) canvasWidth / image.getWidth();
        double scaleY = (double) canvasHeight / image.getHeight();

        // Use the smaller scale to ensure the sprite maintains its aspect ratio
        double scale = Math.min(scaleX, scaleY);

        // Calculate sprite's centered position on the canvas
        int scaledWidth = (int) (image.getWidth() * scale);
        int scaledHeight = (int) (image.getHeight() * scale);
        int centeredX = (canvasWidth / 2) - (scaledWidth / 2);
        int centeredY = (canvasHeight / 2) - (scaledHeight / 2);

        // Draw the sprite image centered on the canvas
        g.drawImage(image, centeredX, centeredY, scaledWidth, scaledHeight, null);
    }
}

