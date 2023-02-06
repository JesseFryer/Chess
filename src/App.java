import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class App extends JFrame {

    private JPanel chessBoard;

    private ArrayList<Tile> tiles;

    private JButton newGame;

    public App() {

        // initialising Window
        setTitle("Chess");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(Constants.WINDOW_SIZE);
        setPreferredSize(Constants.WINDOW_SIZE);
        setResizable(false);

        // initialise board
        chessBoard = new JPanel(new GridLayout(8,8));
        add(chessBoard, BorderLayout.CENTER);

        // creating tiles
        tiles = new ArrayList<>();
        for (int row = 1; row < 9; row++) {
            for (int column = 1; column < 9; column++) {
                Tile tile = new Tile(row, column);

                // set colours of squares
                if (row % 2 == 0) {
                    if (column % 2 == 0) {
                        tile.setBackground(Constants.lightSquare);
                    } else tile.setBackground(Constants.darkSquare);
                } else if (column % 2 == 0) {
                    tile.setBackground(Constants.darkSquare);
                } else tile.setBackground(Constants.lightSquare);

                // add to list of tiles for later and the board
                tiles.add(tile);
                chessBoard.add(tile);
            }
        }

        // create menu
        JPanel menu = new JPanel(new FlowLayout());
        menu.setMinimumSize(Constants.MENU_BUTTON_SIZE);
        menu.setPreferredSize(Constants.MENU_BUTTON_SIZE);
        add(menu, BorderLayout.SOUTH);

        // add menu buttons
        newGame = new JButton("New Game");
        menu.add(newGame);

        setVisible(true);
    }
}
