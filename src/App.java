import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {

        // Defining the rows and column size of the game
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32; // size of each tile of the game in pixels
        int boardWidth = columnCount * tileSize; // total frame size in pixels
        int boardHeight = rowCount * tileSize;
        ImageIcon icon  = new ImageIcon("src/images/pacmanicon.png");

        // Setting up the game window
        JFrame frame = new JFrame("PacMan");
        frame.setSize(boardWidth, boardHeight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(icon.getImage());

        PacMan pacMan = new PacMan();
        pacMan.initialize();
        
        frame.add(pacMan);
        frame.pack();
        frame.setVisible(true);
    }
}
