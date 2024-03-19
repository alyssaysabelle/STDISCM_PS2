import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public class ParticleSimulator extends JFrame {
    private final ArrayList<Particle> particles;
    private final ArrayList<Wall> walls;
    private final DrawPanel drawPanel;
    private final JTextField startField;
    private final JTextField endField;
    private final JTextField startVelocityField;
    private final JTextField endVelocityField;
    private final JTextField startAngleField;
    private final JTextField endAngleField;
    private final JTextField numParticlesField;
    private final JTextField startWallField;
    private final JTextField endWallField;
    private final JComboBox<String> combobox;
    private final JComboBox<String> multipleParticlesCombobox;
    private AtomicInteger actualFramesDrawn = new AtomicInteger();
    private long lastTime = System.nanoTime();
    private final double nsPerTick = 1000000000D / 60D;
    private int frames = 0;
    private int fps = 0;
    private long lastUpdateTime;

    private final ForkJoinPool pool;
    public ParticleSimulator() {
        super("Particle Simulator");
        pool = new ForkJoinPool();
        lastUpdateTime = System.nanoTime();
        particles = new ArrayList<>();
        walls = new ArrayList<>();
        drawPanel = new DrawPanel();
        drawPanel.setPreferredSize(new Dimension(1280, 720));
        add(drawPanel, BorderLayout.CENTER);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        startSimulationThread();
        startGameLoop();

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(20, 2));
        startWallField = new JTextField(10);
        endWallField = new JTextField(10);
        startField = new JTextField(10);
        endField = new JTextField(10);
        startVelocityField = new JTextField(10);
        startAngleField = new JTextField(10);
        numParticlesField = new JTextField(10);
        endAngleField = new JTextField(10);
        endVelocityField = new JTextField(10);

        String[] options = {"Single Particle", "Multiple Particles"};
        combobox = new JComboBox<>(options);
        combobox.setPreferredSize(new Dimension(200, combobox.getPreferredSize().height));

        String[] multipleParticlesOptions = {"Velocity & Angle", "Start Point & Velocity", "Start Point & Angle"};
        multipleParticlesCombobox = new JComboBox<>(multipleParticlesOptions);

        showInputField(inputPanel, "Single Particle", "");
        combobox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String option = (String) combobox.getSelectedItem();
                if(option == "Single Particle"){
                    inputPanel.removeAll();
                    inputPanel.revalidate();
                    inputPanel.repaint();
                    showCombobox(inputPanel, false);
                    showInputField(inputPanel, option, "");
                }
                else if(option == "Multiple Particles"){
                    inputPanel.removeAll();
                    inputPanel.revalidate();
                    inputPanel.repaint();
                    showCombobox(inputPanel, true);
                    showInputField(inputPanel, option, "Velocity & Angle");
                }
                String constant = (String) multipleParticlesCombobox.getSelectedItem();
                inputPanel.removeAll();
                inputPanel.revalidate();
                inputPanel.repaint();
                showInputField(inputPanel, option, constant);
            }
        });

        multipleParticlesCombobox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String option = (String) combobox.getSelectedItem();
                String constant = (String) multipleParticlesCombobox.getSelectedItem();
                inputPanel.removeAll();
                inputPanel.revalidate();
                inputPanel.repaint();
                showInputField(inputPanel, option, constant);
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlPanel, drawPanel);
        splitPane.setDividerLocation(350);
        add(splitPane);
        controlPanel.add(inputPanel, BorderLayout.NORTH);

    }

    /*
    private void showWallInput(JPanel inputPanel){
        inputPanel.add(new JLabel(" Add new wall"));
        inputPanel.add(new JLabel());
        inputPanel.add(new JLabel(" Start (x,y):"));
        inputPanel.add(startWallField);
        inputPanel.add(new JLabel(" End (x,y):"));
        inputPanel.add(endWallField);
        inputPanel.add(new JPanel());
        JButton addWallButton = new JButton("Add Wall");
        inputPanel.add(addWallButton);
        inputPanel.add(new JPanel());
        inputPanel.add(new JPanel());

        addWallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String startText = startWallField.getText();
                    String endText = endWallField.getText();
                    String[] startCoords = startText.split(",");
                    String[] endCoords = endText.split(",");
                    int startX = Integer.parseInt(startCoords[0].trim());
                    int startY = Integer.parseInt(startCoords[1].trim());
                    int endX = Integer.parseInt(endCoords[0].trim());
                    int endY = Integer.parseInt(endCoords[1].trim());
                    if (startX < 0 || startY < 0 || endX < 0 || endY < 0 || startX > drawPanel.getWidth() || startY > drawPanel.getHeight() || endX > drawPanel.getWidth() || endY > drawPanel.getHeight()) {
                        JOptionPane.showMessageDialog(ParticleSimulator.this, "Invalid input. Please check your values.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    walls.add(new Wall(startX, startY, endX, endY));
                    drawPanel.repaint();
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                    JOptionPane.showMessageDialog(ParticleSimulator.this, "Invalid input format. Please use format: x,y", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
     */
    private void showCombobox(JPanel inputPanel, Boolean show){
        //showWallInput(inputPanel);
        inputPanel.add(new JLabel(" Add:"));
        inputPanel.add(combobox);
        if(show == true){
            inputPanel.add(new JLabel(" Constant:"));
            inputPanel.add(multipleParticlesCombobox);
        }
    }
    private void showInputField(JPanel inputPanel, String particle, String constant){
        Boolean show = false;
        if(particle == "Multiple Particles"){
            show = true;
        }
        showCombobox(inputPanel, show);
        inputPanel.add(new JLabel());
        inputPanel.add(new JLabel());
        if(particle == "Single Particle"){
            // adding 1 particle
            inputPanel.add(new JLabel(" New particle"));
            inputPanel.add(new JLabel());
            inputPanel.add(new JLabel(" Start (x,y):"));
            inputPanel.add(startField);
            inputPanel.add(new JLabel(" Velocity:"));
            inputPanel.add(startVelocityField);
            inputPanel.add(new JLabel(" Angle :"));
            inputPanel.add(startAngleField);
            JButton addButton = new JButton("Add Particle");
            inputPanel.add(new JPanel());
            inputPanel.add(addButton);
            inputPanel.add(new JPanel());
            inputPanel.add(new JPanel());

            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String startText = startField.getText();
                        String velocityText = startVelocityField.getText();
                        String angleText = startAngleField.getText();
                        String[] startCoords = startText.split(",");
                        int startX = Integer.parseInt(startCoords[0].trim());
                        int startY = Integer.parseInt(startCoords[1].trim());
                        int velocity = Integer.parseInt(velocityText.trim());
                        int angle = Integer.parseInt(angleText.trim());
                        if (velocity <= 0 || startX < 0 || startY < 0 || startX > drawPanel.getWidth() || startY > drawPanel.getHeight()) {
                            JOptionPane.showMessageDialog(ParticleSimulator.this, "Invalid input. Please check your values.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        particles.add(new Particle(startX, startY, angle, velocity));
                        drawPanel.repaint();
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                        JOptionPane.showMessageDialog(ParticleSimulator.this, "Invalid input format. Please use format: x,y", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        } else {
            if(constant == "Velocity & Angle"){
                inputPanel.add(new JLabel(" Multiple particles"));
                inputPanel.add(new JLabel("(constant velocity and angle)"));
                inputPanel.add(new JLabel(" Number of particles:"));
                inputPanel.add(numParticlesField);
                inputPanel.add(new JLabel(" Start (x,y):"));
                inputPanel.add(startField);
                inputPanel.add(new JLabel(" End (x,y):"));
                inputPanel.add(endField);
                inputPanel.add(new JLabel(" Velocity:"));
                inputPanel.add(startVelocityField);
                inputPanel.add(new JLabel(" Angle :"));
                inputPanel.add(startAngleField);
                inputPanel.add(new JPanel());
                JButton addCvaButton = new JButton("Add Particles");
                inputPanel.add(addCvaButton);
                addCvaButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String startText = startField.getText();
                            String endText = endField.getText();
                            String velocityText = startVelocityField.getText();
                            String angleText = startAngleField.getText();
                            int numParticles = Integer.parseInt(numParticlesField.getText());
                            String[] startCoords = startText.split(",");
                            String[] endCoords = endText.split(",");
                            int startX = Integer.parseInt(startCoords[0].trim());
                            int startY = Integer.parseInt(startCoords[1].trim());
                            int endX = Integer.parseInt(endCoords[0].trim());
                            int endY = Integer.parseInt(endCoords[1].trim());
                            int velocity = Integer.parseInt(velocityText.trim());
                            int angle = Integer.parseInt(angleText.trim());
                            if (numParticles <= 1 || velocity <= 0 || startX < 0 || startY < 0 || endX < 0 || endY < 0 || startX > drawPanel.getWidth() || startY > drawPanel.getHeight() || endX > drawPanel.getWidth() || endY > drawPanel.getHeight()) {
                                JOptionPane.showMessageDialog(ParticleSimulator.this, "Invalid input. Please check your values.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            double distanceX = (endX - startX)/(double)(numParticles - 1);
                            double distanceY = (endY - startY)/(double)(numParticles - 1);

                            for (int i = 1; i <= numParticles; i++) {
                                double x = startX + (distanceX * (i - 1));
                                double y = startY + (distanceY * (i - 1));
                                particles.add(new Particle((int)x, (int)y, angle, velocity));
                            }
                            drawPanel.repaint();
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                            JOptionPane.showMessageDialog(ParticleSimulator.this, "Invalid input format. Please use format: x,y", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

            }
            else if(constant == "Start Point & Velocity"){
                inputPanel.add(new JLabel(" Multiple particles"));
                inputPanel.add(new JLabel("(constant start and velocity)"));
                inputPanel.add(new JLabel(" Number of particles:"));
                inputPanel.add(numParticlesField);
                inputPanel.add(new JLabel(" Start (x,y):"));
                inputPanel.add(startField);
                inputPanel.add(new JLabel(" Velocity:"));
                inputPanel.add(startVelocityField);
                inputPanel.add(new JLabel(" Start Angle :"));
                inputPanel.add(startAngleField);
                inputPanel.add(new JLabel(" End Angle :"));
                inputPanel.add(endAngleField);
                inputPanel.add(new JPanel());
                JButton addCsvButton = new JButton("Add Particles");
                inputPanel.add(addCsvButton);
                addCsvButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String startText = startField.getText();
                            String velocityText = startVelocityField.getText();
                            String startAngleText = startAngleField.getText();
                            String endAngleText = endAngleField.getText();
                            int numParticles = Integer.parseInt(numParticlesField.getText());
                            String[] startCoords = startText.split(",");
                            int startX = Integer.parseInt(startCoords[0].trim());
                            int startY = Integer.parseInt(startCoords[1].trim());
                            int velocity = Integer.parseInt(velocityText.trim());
                            int startAngle = Integer.parseInt(startAngleText.trim());
                            int endAngle = Integer.parseInt(endAngleText.trim());
                            if(numParticles <= 1 || velocity <= 0 || startX < 0 || startY < 0 || startX > drawPanel.getWidth() || startY > drawPanel.getHeight()){
                                JOptionPane.showMessageDialog(ParticleSimulator.this, "Invalid input. Please check your values.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            double distanceX = (endAngle - startAngle)/(double)(numParticles - 1);

                            for (int i = 1; i <= numParticles; i++) {
                                double angle = startAngle + (distanceX * (i - 1));
                                particles.add(new Particle(startX, startY, angle, velocity));
                            }
                            drawPanel.repaint();
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                            JOptionPane.showMessageDialog(ParticleSimulator.this, "Invalid input format. Please use format: x,y", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            }
            else if(constant == "Start Point & Angle"){
                inputPanel.add(new JLabel(" Multiple particles"));
                inputPanel.add(new JLabel("(constant start and angle)"));
                inputPanel.add(new JLabel(" Number of particles:"));
                inputPanel.add(numParticlesField);
                inputPanel.add(new JLabel(" Start (x,y):"));
                inputPanel.add(startField);
                inputPanel.add(new JLabel(" Start Velocity:"));
                inputPanel.add(startVelocityField);
                inputPanel.add(new JLabel(" End Velocity:"));
                inputPanel.add(endVelocityField);
                inputPanel.add(new JLabel(" Angle :"));
                inputPanel.add(startAngleField);
                inputPanel.add(new JPanel());
                JButton addCvaButton = new JButton("Add Particles");
                inputPanel.add(addCvaButton);

                addCvaButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String startText = startField.getText();
                            String velocityText = startVelocityField.getText();
                            String endVelocityText = endVelocityField.getText();
                            String angleText = startAngleField.getText();
                            int numParticles = Integer.parseInt(numParticlesField.getText());
                            String[] startCoords = startText.split(",");
                            int startX = Integer.parseInt(startCoords[0].trim());
                            int startY = Integer.parseInt(startCoords[1].trim());
                            int velocity = Integer.parseInt(velocityText.trim());
                            int endVelocity = Integer.parseInt(endVelocityText.trim());
                            int angle = Integer.parseInt(angleText.trim());

                            if(numParticles <= 1 || velocity <= 0 || endVelocity <= 0 || startX < 0 || startY < 0 || startX > drawPanel.getWidth() || startY > drawPanel.getHeight()){
                                JOptionPane.showMessageDialog(ParticleSimulator.this, "Invalid input. Please check your values.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            double distanceX = (endVelocity - velocity)/(double)(numParticles - 1);

                            for (int i = 1; i <= numParticles; i++) {
                                double v = velocity + (distanceX * (i - 1));
                                particles.add(new Particle(startX, startY, angle, v));
                            }
                            drawPanel.repaint();
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                            JOptionPane.showMessageDialog(ParticleSimulator.this, "Invalid input format. Please use format: x,y", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            }
        }
    }
    private void setupSimulation() {
        // Initialize simulation entities here


    }
    public int getAndResetFrameCount() {
        return actualFramesDrawn.getAndSet(0);
    }
    private void startGameLoop() {
        new Thread(() -> {
            final int targetFPS = 60;
            final long targetTimePerFrame = 1000 / targetFPS;
            long timer = System.currentTimeMillis();
            long lastLoopTime = System.currentTimeMillis();
            while (true) {
                long now = System.currentTimeMillis();
                long elapsedTime = now - lastLoopTime;
                lastLoopTime = now;
                SwingUtilities.invokeLater(drawPanel::repaint);

                if (System.currentTimeMillis() - timer > 500) {
                    fps = getAndResetFrameCount();
                    timer += 1000;
                }
                long sleepTime = targetTimePerFrame - elapsedTime;
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }, "Game Loop Thread").start();
    }



    private void startSimulationThread() {
        Runnable simulationTask = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                long now = System.nanoTime();
                double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0;
                lastUpdateTime = now;
                ParticleUpdateAction updateAction = new ParticleUpdateAction(particles, 0, particles.size(), deltaTime);
                pool.invoke(updateAction);


                SwingUtilities.invokeLater(drawPanel::repaint);

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        new Thread(simulationTask).start();
    }
    public static Optional<Point2D.Double> findIntersectionPoint(Line2D line1, Line2D line2) {
        double x1 = line1.getX1(), y1 = line1.getY1();
        double x2 = line1.getX2(), y2 = line1.getY2();

        double x3 = line2.getX1(), y3 = line2.getY1();
        double x4 = line2.getX2(), y4 = line2.getY2();

        double den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (den == 0) return Optional.empty();

        double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / den;
        double u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / den;

        if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
            double x = x1 + t * (x2 - x1);
            double y = y1 + t * (y2 - y1);
            return Optional.of(new Point2D.Double(x, y));
        }

        return Optional.empty();
    }

    class DrawPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Particle p : particles) {
                p.draw(g);
            }
            walls.parallelStream().forEach(w -> w.draw(g));
            g.drawString("FPS: " + fps, 10, 20);
            actualFramesDrawn.incrementAndGet();
        }
    }
    class ParticleUpdateAction extends RecursiveAction {
        private final ArrayList<Particle> particles;
        private final int start;
        private final int end;
        private final double deltaTime;
        private static final int THRESHOLD = 15;

        ParticleUpdateAction(ArrayList<Particle> particles, int start, int end, double deltaTime) {
            this.particles = particles;
            this.start = start;
            this.end = end;
            this.deltaTime = deltaTime;
        }

        @Override
        protected void compute() {
            if (end - start <= THRESHOLD) {
                for (int i = start; i < end; i++) {
                    Particle particle = particles.get(i);
                    Point2D.Double nextPosition = particle.getNextPosition(deltaTime);
                    Line2D.Double trajectory = new Line2D.Double(particle.x, particle.y, nextPosition.x, nextPosition.y);

                    Point2D.Double closestCollision = null;
                    Wall closestWall = null;
                    double closestDistance = Double.MAX_VALUE;


                    particle.checkCollisionWithBounds(drawPanel);
                    for (Wall wall : walls) {
                        Optional<Point2D.Double> intersectionPoint = findIntersectionPoint(trajectory, wall.toLine2D());
                        if (intersectionPoint.isPresent()) {
                            double distance = intersectionPoint.get().distance(particle.x, particle.y);
                            if (distance < closestDistance) {
                                closestCollision = intersectionPoint.get();
                                closestWall = wall;
                                closestDistance = distance;
                            }
                        }
                    }
                    if (closestCollision != null) {
                        particle.handleCollision(closestCollision, closestWall);
                    } else {

                        particle.move(deltaTime);
                    }
                }
            } else {
                int mid = start + (end - start) / 2;
                ParticleUpdateAction left = new ParticleUpdateAction(particles, start, mid, deltaTime);
                ParticleUpdateAction right = new ParticleUpdateAction(particles, mid, end, deltaTime);
                invokeAll(left, right);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ParticleSimulator().setVisible(true));
    }
    class Particle {
        double x, y;
        double angle, velocity;

        public Particle(double x, double y, double angle, double velocity) {
            this.x = x;
            this.y = y;
            this.angle = Math.toRadians(angle);
            this.velocity = velocity;
        }

        public void move(double deltaTime) {
            x += velocity * Math.cos(angle) * deltaTime;
            y += velocity * Math.sin(angle) * deltaTime;
        }
        public void checkCollisionWithBounds(DrawPanel drawPanel) {
            if (x < 0 || x > drawPanel.getWidth()) {
                angle = Math.PI - angle;
            }

            if (y < 0 || y > drawPanel.getHeight()) {
                angle = -angle;
            }
        }

        public Point2D.Double getNextPosition(double deltaTime) {
            double newX = x + velocity * Math.cos(angle) * deltaTime;
            double newY = y + velocity * Math.sin(angle) * deltaTime;
            return new Point2D.Double(newX, newY);
        }

        public void handleCollision(Point2D.Double collisionPoint, Wall wall) {
            this.x = collisionPoint.x;
            this.y = collisionPoint.y;
            double wallAngle = Math.atan2(wall.endY - wall.startY, wall.endX - wall.startX);
            double incidenceAngle = angle - wallAngle;
            angle = wallAngle - incidenceAngle;
            x += 0.1 * Math.cos(angle);
            y += 0.1 * Math.sin(angle);
        }

        public void draw(Graphics g) {
            g.fillOval((int) Math.round(x), (int) Math.round(drawPanel.getHeight() - y), 10, 10);
        }
    }

    class Wall {
        int startX, startY, endX, endY;

        public Wall(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        public Line2D.Double toLine2D() {
            return new Line2D.Double(startX, startY, endX, endY);
        }

        public void draw(Graphics g) {
            g.drawLine(startX, drawPanel.getHeight() - startY, endX, drawPanel.getHeight() - endY);
        }
    }
}
