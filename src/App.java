import utils.Constants;
import utils.Piece;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class App extends JFrame implements ActionListener {

    private JPanel chessBoard;

    private ArrayList<Tile> tiles;

    private JButton newGame;

    private ArrayList<ImageIcon> whitePieceImages;
    private ArrayList<ImageIcon> blackPieceImages;


    public App() {
        //load image sprites
        whitePieceImages = loadImages("/whitePieces.png");
        blackPieceImages = loadImages("/blackPieces.png");

        // initialising Window
        setTitle("Chess");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(Constants.WINDOW_SIZE);
        setPreferredSize(Constants.WINDOW_SIZE);
        setResizable(false);

        // initialise board
        chessBoard = new JPanel(new GridLayout(8, 8));
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
        newGame.addActionListener(this);
        menu.add(newGame);

        setVisible(true);
    }

    private void newGame() {
        for (Tile tile : tiles) {
            if (tile.row == 2 || tile.row == 7) tile.setPiece(Piece.PAWN);
            else if (tile.row == 1 || tile.row == 8) {
                if (tile.column == 1 || tile.column == 8) tile.setPiece(Piece.ROOK);
                if (tile.column == 2 || tile.column == 7) tile.setPiece(Piece.KNIGHT);
                if (tile.column == 3 || tile.column == 6) tile.setPiece(Piece.BISHOP);
                if (tile.column == 4) tile.setPiece(Piece.QUEEN);
                if (tile.column == 5) tile.setPiece(Piece.KING);
            } else tile.setPiece(Piece.EMPTY);
        }
        updateImages();
    }

    private void updateImages() {
        for (Tile tile : tiles) {
            if (tile.getPiece() != Piece.EMPTY) tile.setIcon(whitePieceImages.get(tile.getPiece()));
        }
        revalidate();
    }

    private ArrayList<ImageIcon> loadImages(String path) {
        ArrayList<ImageIcon> imageIcons = new ArrayList<>();
        InputStream is = getClass().getResourceAsStream(path);
        try {
            BufferedImage spriteSheet = ImageIO.read(is);
            for (int x = 0; x < 96; x += 16) {
                ImageIcon img = new ImageIcon(spriteSheet.getSubimage(x, 0, 16,16));
                Image scaledImg = img.getImage().getScaledInstance(50,50, Image.SCALE_DEFAULT);
                imageIcons.add(new ImageIcon(scaledImg));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageIcons;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGame) newGame();
    }
}
