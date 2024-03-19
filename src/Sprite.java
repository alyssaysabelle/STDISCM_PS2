import java.awt.*;

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
            int spriteWidth = 10; // Original sprite width
            int spriteHeight = 10; // Adjust this if the sprite's original aspect ratio is not 1:1
            int scaledWidth = (int) (spriteWidth * scale);
            int scaledHeight = (int) (spriteHeight * scale);
        
            int centeredX = (canvasWidth / 2) - (scaledWidth / 2);
            int centeredY = (canvasHeight / 2) - (scaledHeight / 2);
        
            // Set color and draw the sprite centered on the canvas
            g.setColor(Color.RED);
            g.fillRect(centeredX, centeredY, scaledWidth, scaledHeight);
        }
        
       
}
