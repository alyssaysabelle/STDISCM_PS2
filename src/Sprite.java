import java.awt.*;

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
        public void draw(Graphics g) {
            g.setColor(Color.RED);
            g.fillRect(x, y, 10, 10);
        }
}
