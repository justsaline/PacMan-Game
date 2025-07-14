import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

//The main game class
public final class PacMan extends JPanel implements ActionListener, KeyListener {

    private String gameState = "MENU"; // Options: "MENU", "PLAYING", "PAUSED", "GAME_OVER"

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // default direction UP
        int velocityX = 0;
        int velocityY = 0;

        // constructor defining the building block of the game
        Block(int x, int y, int width, int height, Image image) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.image = image;
            this.startX = x;
            this.startY = y;
        }

        // Method to apply direction changes of game elements like pacman and ghosts
        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;

            // Method for game entity movement
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;

            // Loop to check for in game collisions between any two entities
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity(); // action taken if a collison occurs
                }
            }
        }

        // To uupdate directions of pacman, according to user presses
        void updateVelocity() {
            switch (this.direction) {
                case 'U' -> {
                    this.velocityX = 0;
                    this.velocityY = -tileSize / 4;
                }
                case 'D' -> {
                    this.velocityX = 0;
                    this.velocityY = tileSize / 4;
                }
                case 'L' -> {
                    this.velocityX = -tileSize / 4;
                    this.velocityY = 0;
                }
                case 'R' -> {
                    this.velocityX = tileSize / 4;
                    this.velocityY = 0;
                }
                default -> {
                }
            }
        }

        // Method to reset pacman position if game over or live lost
        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private final int rowCount = 21;
    private final int columnCount = 19;
    private final int tileSize = 32;
    private final int boardWidth = columnCount * tileSize;
    private final int boardHeight = rowCount * tileSize;

    private final Image wallImage;
    private final Image blueGhostImage;
    private final Image orangeGhostImage;
    private final Image pinkGhostImage;
    private final Image redGhostImage;

    private final Image pacmanUpImage;
    private final Image pacmanDownImage;
    private final Image pacmanLeftImage;
    private final Image pacmanRightImage;

    // Tilemap of the whole game stored as an array of strings
    // X = walls, b,p,o,r = ghosts ,P = pacman, whitespaces = food
    private final String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop; // Creating a timer to update game changes
    char[] directions = { 'U', 'D', 'R', 'L' }; // array of chars represting each direction
    Random random = new Random();
    int score = 0;
    int lives = 3;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        setFocusable(true);

        wallImage = new ImageIcon("src/images/wall.png").getImage();
        blueGhostImage = new ImageIcon("src/images/blueGhost.png").getImage();
        orangeGhostImage = new ImageIcon("src/images/orangeGhost.png").getImage();
        pinkGhostImage = new ImageIcon("src/images/pinkGhost.png").getImage();
        redGhostImage = new ImageIcon("src/images/redGhost.png").getImage();

        pacmanUpImage = new ImageIcon("src/images/pacmanUp.png").getImage();
        pacmanDownImage = new ImageIcon("src/images/pacmanDown.png").getImage();
        pacmanLeftImage = new ImageIcon("src/images/pacmanLeft.png").getImage();
        pacmanRightImage = new ImageIcon("src/images/pacmanRight.png").getImage();

        // initializing the game timer with this panel
        gameLoop = new Timer(50, this);
    }

    // Method to initialize the game screen before running
    public void initialize() {
        addKeyListener(this);
        loadMap(); // Loads the game tile map

        // Loop to add random movement of ghosts in game
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    // Uses the tilemap and replaces certain charectors with certain blocks
    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char titleMapChar = row.charAt(c);

                int x = c * tileSize; // Defines the size of bllock in pixels
                int y = r * tileSize;

                // creating Game map using blocks
                switch (titleMapChar) {
                    case 'X' -> {
                        Block wall = new Block(x, y, tileSize, tileSize, wallImage);
                        walls.add(wall);
                    }
                    case 'o' -> {
                        Block ghost = new Block(x, y, tileSize, tileSize, orangeGhostImage);
                        ghosts.add(ghost);
                    }
                    case 'b' -> {
                        Block ghost = new Block(x, y, tileSize, tileSize, blueGhostImage);
                        ghosts.add(ghost);
                    }
                    case 'p' -> {
                        Block ghost = new Block(x, y, tileSize, tileSize, pinkGhostImage);
                        ghosts.add(ghost);
                    }
                    case 'r' -> {
                        Block ghost = new Block(x, y, tileSize, tileSize, redGhostImage);
                        ghosts.add(ghost);
                    }
                    case 'P' -> pacman = new Block(x, y, tileSize, tileSize, pacmanRightImage);
                    case ' ' -> {
                        Block food = new Block(x + 14, y + 14, 4, 4, null);
                        foods.add(food);
                    }
                    default -> {
                    }
                }

            }
        }
    }

    // 3 modes of game with 3 different screens displayed depending in the game
    // state
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Check what state the game is currently in
        switch (gameState) {
            case "MENU" -> drawMenu(g);
            case "GAME_OVER" -> drawGameOver(g);
            default -> draw(g); // your normal game drawing
        }
    }

    // Displays the Start page or Start menu of the game
    public void drawMenu(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("PACMAN", 100, 100);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Press Y to Start", 100, 180);
        g.drawString("Press E to Exit", 100, 210);
    }

    // Final page or end(game over) page
    public void drawGameOver(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Game Over!", 100, 100);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, 100, 160);
        g.drawString("Press any key to Restart", 100, 190);
        g.drawString("Press E to Exit", 100, 220);
    }

    // The actual game map
    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        g.setFont(new Font("Arial", Font.BOLD, 18));
        if (gameState.equals("GAME_OVER")) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        } else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        }
    }

    // Method to update pacman velocity
    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        // Loop to look for pacman and wall collision to prevent moving through the
        // walls
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        // Checking collisions between ghosts and pacman, and lives counter
        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                lives -= 1;
                gameState = "Paused";
                pacman.reset();
                // If 3 collisions i.e lives = 0, game over
                if (lives == 0) {
                    gameState = "GAME_OVER";
                    resetPositions(); // resets pacman and ghost positions
                    return;
                }
            }

            // If statement to prevent ghosts getting stuck on the 9th row by forcing them
            // to move up
            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            // Preventing ghosts from going through walls
            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;

                    // Redirecting ghosts after they hit a wall
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        // updating and removing food blocks pacman passes through
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);
        if (foods.isEmpty()) {
            gameState = "GAME_OVER";
        }

    }

    // Method collision to check if two game entities are colliding with each other
    boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    // Resets game entities position to intial position
    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState.equals("PLAYING")) {
            move();
            repaint();
        } else if (gameState.equals("GAME_OVER")) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {
    }

    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        // MENU STATE
        if (gameState.equals("MENU")) {
            if (e.getKeyChar() == 'y' || e.getKeyChar() == 'Y') {
                gameState = "PLAYING";
                gameLoop.start();
                repaint();
            } else if (e.getKeyChar() == 'e' || e.getKeyChar() == 'E') {
                System.exit(0);
            }
            return;
        }

        // The state game is at after pacman collides with a ghost
        // with 1 or more lives left
        if (gameState.equals("Paused")) {
            gameState = "PLAYING";
            return;
        }

        // GAME OVER STATE
        if (gameState.equals("GAME_OVER")) {
            if (e.getKeyChar() == 'E' || e.getKeyChar() == 'e') {
                System.exit(0);
            } else {
                loadMap();
                resetPositions();
                lives = 3;
                score = 0;
                gameState = "PLAYING";
                gameLoop.start();
                repaint();
            }
            return;
        }

        // --- PLAYING STATE ---
        if (gameState.equals("PLAYING")) {
            // reading user input keys
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> pacman.updateDirection('U');
                case KeyEvent.VK_DOWN -> pacman.updateDirection('D');
                case KeyEvent.VK_LEFT -> pacman.updateDirection('L');
                case KeyEvent.VK_RIGHT -> pacman.updateDirection('R');
                default -> {
                }
            }

            // Updating pacman image depending on the direction it is moving
            switch (pacman.direction) {
                case 'D' -> pacman.image = pacmanDownImage;
                case 'U' -> pacman.image = pacmanUpImage;
                case 'R' -> pacman.image = pacmanRightImage;
                case 'L' -> pacman.image = pacmanLeftImage;

                default -> {
                }
            }
        }
    }

    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {
    }

}
