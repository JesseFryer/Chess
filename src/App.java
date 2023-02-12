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
import java.util.HashMap;
import java.util.Random;

import static utils.Constants.BLACK;
import static utils.Constants.WHITE;

public class App extends JFrame implements ActionListener {

    private JPanel chessBoard;

    private ArrayList<ArrayList<Tile>> tiles;

    private Tile selectedTile;
    private ArrayList<Tile> updatedTiles;

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
        for (int row = 0; row < 8; row++) {
            ArrayList<Tile> tileRow = new ArrayList<>();
            for (int column = 0; column < 8; column++) {
                Tile tile = new Tile(row, column);
                tile.addActionListener(this);

                // set colours of squares
                if (row % 2 != 0) {
                    if (column % 2 != 0) {
                        tile.setBackground(Constants.lightSquare);
                    } else tile.setBackground(Constants.darkSquare);
                } else if (column % 2 != 0) {
                    tile.setBackground(Constants.darkSquare);
                } else tile.setBackground(Constants.lightSquare);

                // add to list of tiles for later and the board
                chessBoard.add(tile);
                tileRow.add(tile);
            }
            tiles.add(tileRow);
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
        selectedTile = null;
        updatedTiles = new ArrayList<>();
        for (ArrayList<Tile> tileRow : tiles) {
            for (Tile tile : tileRow) {
                updatedTiles.add(tile);
                // set black or white piece
                if (tile.row == 0 || tile.row == 1) tile.setColour(BLACK);
                else if (tile.row == 6 || tile.row == 7) tile.setColour(WHITE);
                else tile.setColour(Piece.EMPTY);

                // set piece
                if (tile.row == 0 || tile.row == 7) {
                    if (tile.column == 0 || tile.column == 7) tile.setPiece(Piece.ROOK);
                    else if (tile.column == 1 || tile.column == 6) tile.setPiece(Piece.KNIGHT);
                    else if (tile.column == 2 || tile.column == 5) tile.setPiece(Piece.BISHOP);
                    else if (tile.column == 3) tile.setPiece(Piece.QUEEN);
                    else tile.setPiece(Piece.KING);
                }
                else if (tile.row == 1 || tile.row == 6) tile.setPiece(Piece.PAWN);
                else tile.setPiece(Piece.EMPTY);
            }
        }
        updateImages();
    }

    private void updateImages() {
        for (Tile tile : updatedTiles) {
            switch (tile.getColour()) {
                case WHITE:
                    tile.setIcon(whitePieceImages.get(tile.getPiece()));
                    break;
                case BLACK:
                    tile.setIcon(blackPieceImages.get(tile.getPiece()));
                    break;
                default:
                    tile.setIcon(null);
                }
            }
        updatedTiles.clear();
        revalidate();
    }

    private ArrayList<ImageIcon> loadImages(String path) {
        ArrayList<ImageIcon> imageIcons = new ArrayList<>();
        InputStream is = getClass().getResourceAsStream(path);
        try {
            BufferedImage spriteSheet = ImageIO.read(is);
            for (int x = 0; x < 96; x += 16) {
                ImageIcon img = new ImageIcon(spriteSheet.getSubimage(x, 0, 16,16));
                Image scaledImg = img.getImage().getScaledInstance(60,60, Image.SCALE_DEFAULT);
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

    private ArrayList<Tile> getValidMoves(Tile selectedTile) {
        ArrayList<Tile> validMoves = new ArrayList<>();
        int direction = 1;
        switch (selectedTile.getColour()) {
            case BLACK -> direction = 1;
            case WHITE -> direction = -1;
        }
        switch (selectedTile.getPiece()) {

            case Piece.PAWN:
                // pawn can always move one square if there is an empty tile
                Tile forward1 = selectedTile;
                try {
                    forward1 = tiles.get(selectedTile.row + (1 * direction)).get(selectedTile.column);
                    if (!forward1.isPiece()) validMoves.add(forward1);
                } catch (IndexOutOfBoundsException e) {}

                // first move, pawn can move 1 or 2 squares forward (if both squares empty
                if (validMoves.contains(forward1)) {
                    if (selectedTile.row == 6 && selectedTile.getColour() == WHITE ||
                            selectedTile.row == 1 && selectedTile.getColour() == BLACK) {
                        Tile forward2 = tiles.get(selectedTile.row + (2 * direction)).get(selectedTile.column);
                        if (!forward2.isPiece()) validMoves.add(forward2);
                    }
                }
                // pawn can take diagonally up
                try {
                    Tile takeLeft = tiles.get(selectedTile.row + direction).get(selectedTile.column - 1);
                    if (takeLeft.isPiece() && takeLeft.getColour() != selectedTile.getColour()) validMoves.add(takeLeft);
                } catch (IndexOutOfBoundsException e) {}
                try {
                    Tile takeRight = tiles.get(selectedTile.row + direction).get(selectedTile.column + 1);
                    if (takeRight.isPiece() && takeRight.getColour() != selectedTile.getColour()) validMoves.add(takeRight);
                } catch (IndexOutOfBoundsException e) {}
                break;

            case Piece.KNIGHT:
                // knight can move 8 squares total
                int[] leftAndRight = {selectedTile.row + 2, selectedTile.row - 2};
                int[] UpAndDown = {selectedTile.column + 2, selectedTile.column -2};

                // add the 4 tiles left/right 2 tiles and up/down 1 tile
                for (int row : leftAndRight) {
                    if (row >= 0 && row <= 7) {
                        int col1 = selectedTile.column + 1;
                        int col2 = selectedTile.column - 1;

                        if (col1 >= 0 && col1 <= 7) {
                            Tile tile1 = tiles.get(row).get(col1);
                            if (tile1.getColour() != selectedTile.getColour()) validMoves.add(tile1);
                        }
                        if (col2 >= 0 && col2 <= 7) {
                            Tile tile2 = tiles.get(row).get(col2);
                            if (tile2.getColour() != selectedTile.getColour()) validMoves.add(tile2);
                        }
                    }
                }

                // add the 4 tiles up/down 2 tiles and left/right 1 tile
                for (int col : UpAndDown) {
                    if (col >= 0 && col <= 7) {
                        int row1 = selectedTile.row + 1;
                        int row2 = selectedTile.row - 1;

                        if (row1 >= 0 && row1 <= 7) {
                            Tile tile1 = tiles.get(row1).get(col);
                            if (tile1.getColour() != selectedTile.getColour()) validMoves.add(tile1);
                        }
                        if (row2 >= 0 && row2 <= 7) {
                            Tile tile2 = tiles.get(row2).get(col);
                            if (tile2.getColour() != selectedTile.getColour()) validMoves.add(tile2);
                        }
                    }
                }
                break;


            case Piece.QUEEN:
                // queen adopts both rook and bishops logic

            case Piece.ROOK:
                // iterate through tiles up/down/left/right until not empty or out of bounds
                for (int row = selectedTile.row + 1; row < 8; row++) {
                    Tile tile = tiles.get(row).get(selectedTile.column);
                    if (!tile.isPiece() || tile.getColour() != selectedTile.getColour()) validMoves.add(tile);
                    if (tile.isPiece()) break;
                }
                for (int row = selectedTile.row - 1; row > -1; row--) {
                    Tile tile = tiles.get(row).get(selectedTile.column);
                    if (!tile.isPiece() || tile.getColour() != selectedTile.getColour()) validMoves.add(tile);
                    if (tile.isPiece()) break;
                }
                for (int col = selectedTile.column + 1; col < 8; col++) {
                    Tile tile = tiles.get(selectedTile.row).get(col);
                    if (!tile.isPiece() || tile.getColour() != selectedTile.getColour()) validMoves.add(tile);
                    if (tile.isPiece()) break;
                }
                for (int col = selectedTile.column - 1; col > -1; col--) {
                    Tile tile = tiles.get(selectedTile.row).get(col);
                    if (!tile.isPiece() || tile.getColour() != selectedTile.getColour()) validMoves.add(tile);
                    if (tile.isPiece()) break;
                }
                if (selectedTile.getPiece() == Piece.ROOK) break;

            case Piece.BISHOP:
                // iterate through tiles same as rook, except go diagonally.

                // down/right
                int colAdd = 1;
                for (int row = selectedTile.row + 1; row < 8; row++) {
                    int column = selectedTile.column + colAdd;
                    if (column < 8) {
                        Tile tile = tiles.get(row).get(column);
                        if (!tile.isPiece() || tile.getColour() != selectedTile.getColour()) {
                            validMoves.add(tile);
                            if (tile.isPiece()) break;
                            colAdd++;
                        } else break;
                    } else break;
                }

                // down/left
                colAdd = 1;
                for (int row = selectedTile.row + 1; row < 8; row++) {
                    int column = selectedTile.column - colAdd;
                    if (column > -1) {
                        Tile tile = tiles.get(row).get(column);
                        if (!tile.isPiece() || tile.getColour() != selectedTile.getColour()) {
                            validMoves.add(tile);
                            if (tile.isPiece()) break;
                            colAdd++;
                        } else break;
                    } else break;
                }

                // up/right
                colAdd = 1;
                for (int row = selectedTile.row - 1; row > -1; row--) {
                    int column = selectedTile.column + colAdd;
                    if (column < 8) {
                        Tile tile = tiles.get(row).get(column);
                        if (!tile.isPiece() || tile.getColour() != selectedTile.getColour()) {
                            validMoves.add(tile);
                            if (tile.isPiece()) break;
                            colAdd++;
                        } else break;
                    } else break;
                }

                // up/left
                colAdd = 1;
                for (int row = selectedTile.row - 1; row > -1; row--) {
                    int column = selectedTile.column - colAdd;
                    if (column > -1) {
                        Tile tile = tiles.get(row).get(column);
                        if (!tile.isPiece() || tile.getColour() != selectedTile.getColour()) {
                            validMoves.add(tile);
                            if (tile.isPiece()) break;
                            colAdd++;
                        } else break;
                    } else break;
                }
                break;

            case Piece.KING:
                // king moves 1 in any direction
                int[] directions = {0,1,-1};
                for (int deltaRow : directions) {
                    for (int deltaCol : directions) {
                        int row = selectedTile.row + deltaRow;
                        int column = selectedTile.column + deltaCol;
                        if (isInBounds(row, column)) {
                            Tile tile = tiles.get(row).get(column);
                            if (selectedTile != tile && !validMoves.contains(tile)) {
                                if (!(row == 0 && column == 0)) validMoves.add(tiles.get(row).get(column));
                            }
                        }
                    }
                }
                break;
        }
        return validMoves;
    }

    private boolean movePiece(Tile tileTo, Tile tileToMove) {
        if (tileTo.getColour() != tileToMove.getColour()
                && tileToMove.getPiece() != Piece.EMPTY
                && getValidMoves(tileToMove).contains(tileTo)) {
            tileTo.setPiece(tileToMove.getPiece());
            tileTo.setColour(tileToMove.getColour());
            tileToMove.clear();
            updatedTiles.add(tileToMove);
            updatedTiles.add(tileTo);
            return true;
        }
        selectedTile = tileTo;
        return false;
    }

    private boolean isInBounds(int row, int column) {
        if (row < 8 && row > -1 && column < 8 && column > -1) return true;
        return false;
    }

    private void randomMoveBlack() {
        HashMap<Tile, ArrayList<Tile>> blackPieceMoves = new HashMap<>();
        for (Tile tile : getTiles()) {
            if (tile.getColour() == BLACK) {
                ArrayList<Tile> validMoves = getValidMoves(tile);
                if (!validMoves.isEmpty()) {
                    blackPieceMoves.put(tile, validMoves);
                }
            }
        }
        // remove all moves which self check
        HashMap<Tile, ArrayList<Tile>> validMoves = new HashMap<>();
        for (Tile tileToMove : blackPieceMoves.keySet()) {
            ArrayList<Tile> moves = new ArrayList<>();
            for (Tile tileTo : blackPieceMoves.get(tileToMove)) {
                // save info to go back
                int tileColour = tileToMove.getColour();
                int tilePiece = tileToMove.getPiece();
                int moveColour = tileTo.getColour();
                int movePiece = tileTo.getPiece();
                // make the move, if not self check, add to refined moves
                move(tileTo, tileToMove);
                if (!isCheck(tileColour)) moves.add(tileTo);
                // reset to before move
                tileToMove.setPiece(tilePiece);
                tileToMove.setColour(tileColour);
                tileTo.setPiece(movePiece);
                tileTo.setColour(moveColour);
            }
            if (!moves.isEmpty()) validMoves.put(tileToMove, moves);
        }
        ArrayList<Tile> pieces = new ArrayList<>(validMoves.keySet());
        Random random = new Random();

        // chose random black piece from available ones
        int tileIndex = random.nextInt(pieces.size());
        Tile tileToMove = pieces.get(tileIndex);

        // select a random move for that piece
        ArrayList<Tile> potentialMoves = validMoves.get(tileToMove);
        System.out.println(potentialMoves.size());
        int moveIndex = random.nextInt(potentialMoves.size());
        Tile tileTo = potentialMoves.get(moveIndex);
        movePiece(tileTo, tileToMove);
    }

    private void move(Tile tileTo, Tile tileToMove) {
        tileTo.setColour(tileToMove.getColour());
        tileTo.setPiece(tileToMove.getPiece());
        tileToMove.clear();
    }

    private boolean isCheck(int colour) {
        for (Tile tile : getTiles()) {
            if (tile.isPiece() && tile.getColour() != colour) {
                ArrayList<Tile> moves = getValidMoves(tile);
                for (Tile move : moves) {
                    if (move.getPiece() == Piece.KING) return true;
                }
            }
        }
        return false;
    }

    private ArrayList<Tile> getTiles() {
        ArrayList<Tile> allTiles = new ArrayList<>();
        for (ArrayList<Tile> tileRow : tiles) {
            allTiles.addAll(tileRow);
        }
        return allTiles;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGame) newGame();
        else if (e.getSource().getClass() == Tile.class) {
            if (selectedTile != null && selectedTile.getColour() == WHITE) {
                if (movePiece((Tile) e.getSource(), selectedTile)) {
                    randomMoveBlack();
                    updateImages();
                }
            } else {
                Tile tile = (Tile) e.getSource();
                if (tile.getPiece() != Piece.EMPTY && tile.getColour() == WHITE) selectedTile = tile;
            }
        }
    }
}
